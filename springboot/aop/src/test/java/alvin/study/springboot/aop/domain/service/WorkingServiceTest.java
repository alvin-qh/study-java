package alvin.study.springboot.aop.domain.service;

import alvin.study.springboot.aop.IntegrationTest;
import alvin.study.springboot.aop.aspect.AnnotationAdvice;
import alvin.study.springboot.aop.aspect.Message;
import alvin.study.springboot.aop.aspect.Message.Step;
import alvin.study.springboot.aop.aspect.MethodAdvice;
import alvin.study.springboot.aop.domain.model.Worker;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * 测试 AOP
 *
 * <p>
 * {@link MethodAdvice} 切面类型用于对指定特征的方法进行拦截; {@link AnnotationAdvice}
 * 切面用于对具备指定注解的方法进行拦截
 * </p>
 *
 * <p>
 * 当调用 {@link WorkingService#work(Worker)} 方法时, {@link MethodAdvice} 切面会被触发; 而调用
 * {@link WorkingService#workWithTransactional(Worker)} 方法时,
 * {@link AnnotationAdvice} 切面会被触发
 * </p>
 *
 * <p>
 * 上述两个切面均实现了 {@link org.aspectj.lang.annotation.Before @Before},
 * {@link org.aspectj.lang.annotation.After @After},
 * {@link org.aspectj.lang.annotation.AfterReturning @AfterReturning},
 * {@link org.aspectj.lang.annotation.AfterReturning @AfterReturning} 和
 * {@link org.aspectj.lang.annotation.AfterReturning @AfterReturning} 注解,
 * 对调用目标方法的各个环节进行拦截
 * </p>
 */
class WorkingServiceTest extends IntegrationTest {
    /**
     * 注入的目标类型, 将对其 {@link WorkingService#work(Worker)} 和
     * {@link WorkingService#workWithTransactional(Worker)} 两个方法进行拦截
     */
    @Autowired
    private WorkingService service;

    /**
     * {@link MethodAdvice} 切面在进行目标方法拦截时发送消息的消息队列, 从该队列中可获取整个拦截过程的信息
     */
    @Autowired
    @Qualifier("mqForMethodAdvice")
    private BlockingQueue<Message> mqForMethodAdvice;

    /**
     * {@link AnnotationAdvice} 切面在进行目标方法拦截时发送消息的消息队列, 从该队列中可获取整个拦截过程的信息
     */
    @Autowired
    @Qualifier("mqForAnnotationAdvice")
    private BlockingQueue<Message> mqForAnnotationAdvice;

    // 注入切面对象
    @Autowired
    private MethodAdvice methodAdvice;

    // 注入切面对象
    @Autowired
    private AnnotationAdvice annotationAdvice;

    // 处理 JSON
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 确认 {@link WorkingService#work(Worker)} 方法正确执行时, 切面 {@link MethodAdvice}
     * 的行为符合预期
     */
    @Test
    @SneakyThrows
    void shouldMethodAdviceWorked() {
        // 期待的被拦截方法的签名
        var signature = "public java.lang.String " +
                        "alvin.study.springboot.aop.domain.service.WorkingService.work" +
                        "(alvin.study.springboot.aop.domain.model.Worker)";

        // 传递给目标方法的参数, 被拦截到的参数也应该是该对象
        var worker = new Worker("Alvin", "Engineer");
        var result = service.work(worker);

        // 确认方法正确执行后的结果, 注意, MethodAdvice.afterReturning 拦截方法会给结果中增加 "addition" 字段
        then(result).isEqualTo("{\"name\":\"Alvin\",\"title\":\"Engineer\",\"addition\":\"MethodAdvice\"}");

        // 确认各个阶段拦截器工作正常
        then(mqForMethodAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, methodAdvice, Step.AROUND, null, worker));
        then(mqForMethodAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, methodAdvice, Step.BEFORE, null, worker));

        // 确认获取的目标方法原始返回值符合预期
        then(mqForMethodAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(
                    signature,
                    methodAdvice,
                    Step.AFTER_RETURNING,
                    "{\"name\":\"Alvin\",\"title\":\"Engineer\"}",
                    worker));
        then(mqForMethodAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, methodAdvice, Step.AFTER, null, worker));

        // 确认拦截过程中的所有消息均已被消费
        then(mqForMethodAdvice).isEmpty();
    }

    @Test
    @SneakyThrows
    void shouldMethodAdviceGotException() {
        // 期待的被拦截方法的签名
        var signature = "public java.lang.String " +
                        "alvin.study.springboot.aop.domain.service.WorkingService.work" +
                        "(alvin.study.springboot.aop.domain.model.Worker)";

        // 期待的异常对象
        var exception = new JsonParseException(objectMapper.createParser("{}"), "test");

        // 对目标对象的字段进行 mock 操作, 以便能引发期待的异常
        var spyObjectMapper = spy(objectMapper);
        when(spyObjectMapper.writeValueAsString(any())).thenThrow(exception);

        // 更换目标对象的字段, 以在执行时引发异常
        service.changeObjectMapper(spyObjectMapper);
        // 执行目标方法的参数
        var worker = new Worker("Alvin", "Engineer");

        // 期待的异常对象
        thenThrownBy(() -> service.work(worker)).isInstanceOf(JsonProcessingException.class);

        // 确认各个阶段拦截器工作正常
        then(mqForMethodAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, methodAdvice, Step.AROUND, null, worker));
        then(mqForMethodAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, methodAdvice, Step.BEFORE, null, worker));

        // 确认拦截到指定的异常
        then(mqForMethodAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, methodAdvice, Step.AFTER_THROWING, exception, worker));
        then(mqForMethodAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, methodAdvice, Step.AFTER, null, worker));

        // 确认拦截过程中的所有消息均已被消费
        then(mqForMethodAdvice).isEmpty();
        service.changeObjectMapper(objectMapper);
    }

    /**
     * 确认 {@link WorkingService#workWithTransactional(Worker)} 方法正确执行时, 切面
     * {@link AnnotationAdvice}
     * 的行为符合预期
     */
    @Test
    @SneakyThrows
    void shouldAnnotationAdviceWorked() {
        // 期待的被拦截方法的签名
        var signature = "public java.lang.String " +
                        "alvin.study.springboot.aop.domain.service.WorkingService.workWithTransactional" +
                        "(alvin.study.springboot.aop.domain.model.Worker)";

        // 传递给目标方法的参数, 被拦截到的参数也应该是该对象
        var worker = new Worker("Alvin", "Engineer");
        var result = service.workWithTransactional(worker);

        // 确认方法正确执行后的结果, 注意, MethodAdvice.afterReturning 拦截方法会给结果中增加 "addition" 字段
        then(result).isEqualTo("{\"name\":\"Alvin\",\"title\":\"Engineer\",\"addition\":\"AnnotationAdvice\"}");

        // 确认各个阶段拦截器工作正常
        then(mqForAnnotationAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, annotationAdvice, Step.AROUND, null, worker));
        then(mqForAnnotationAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, annotationAdvice, Step.BEFORE, null, worker));

        // 确认获取的目标方法原始返回值符合预期
        then(mqForAnnotationAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(
                    signature,
                    annotationAdvice,
                    Step.AFTER_RETURNING,
                    "{\"name\":\"Alvin\",\"title\":\"Engineer\"}",
                    worker));
        then(mqForAnnotationAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, annotationAdvice, Step.AFTER, null, worker));

        // 确认拦截过程中的所有消息均已被消费
        then(mqForAnnotationAdvice).isEmpty();
    }

    @Test
    @SneakyThrows
    void shouldAnnotationAdviceGotException() {
        // 期待的被拦截方法的签名
        var signature = "public java.lang.String " +
                        "alvin.study.springboot.aop.domain.service.WorkingService.workWithTransactional" +
                        "(alvin.study.springboot.aop.domain.model.Worker)";

        // 期待的异常对象
        var exception = new JsonParseException(objectMapper.createParser("{}"), "test");

        // 对目标对象的字段进行 mock 操作, 以便能引发期待的异常
        var spyObjectMapper = spy(objectMapper);
        when(spyObjectMapper.writeValueAsString(any())).thenThrow(exception);

        // 更换目标对象的字段, 以在执行时引发异常
        service.changeObjectMapper(spyObjectMapper);
        // 执行目标方法的参数
        var worker = new Worker("Alvin", "Engineer");

        // 期待的异常对象
        thenThrownBy(() -> service.workWithTransactional(worker)).isInstanceOf(JsonProcessingException.class);

        // 确认各个阶段拦截器工作正常
        then(mqForAnnotationAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, annotationAdvice, Step.AROUND, null, worker));
        then(mqForAnnotationAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, annotationAdvice, Step.BEFORE, null, worker));

        // 确认拦截到指定的异常
        then(mqForAnnotationAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, annotationAdvice, Step.AFTER_THROWING, exception, worker));
        then(mqForAnnotationAdvice.poll(0, TimeUnit.SECONDS))
                .has(new MessageCondition(signature, annotationAdvice, Step.AFTER, null, worker));

        // 确认拦截过程中的所有消息均已被消费
        then(mqForAnnotationAdvice).isEmpty();
        service.changeObjectMapper(objectMapper);
    }

    /**
     * 对 {@link Message} 对象进行断言
     */
    @RequiredArgsConstructor
    static class MessageCondition extends Condition<Message> {
        private final String signature;
        private final Object advice;
        private final Step step;
        private final Object returnObj;
        private final Worker worker;

        @Override
        public boolean matches(Message value) {
            return Objects.equal(value.getSignature(), signature)
                   && Objects.equal(value.getAdviceObject(), advice)
                   && Objects.equal(value.getStep(), step)
                   && Objects.equal(value.getReturnObject(), returnObj)
                   && List.of(value.getArguments()).contains(worker);
        }
    }
}
