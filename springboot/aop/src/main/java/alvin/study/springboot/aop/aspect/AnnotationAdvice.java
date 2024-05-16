package alvin.study.springboot.aop.aspect;

import alvin.study.springboot.aop.aspect.Message.Step;
import alvin.study.springboot.aop.domain.model.Worker;
import alvin.study.springboot.aop.domain.service.WorkingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * 定义用于拦截具备指定注解方法的切面类型
 *
 * <p>
 * 切面由一系列方法组成:
 * <ul>
 * <li>
 * <p>
 * {@link AnnotationAdvice#point()} 方法定义连接点, 即通过 {@link Pointcut @Pointcut}
 * 注解配合表达式指定被拦截方法的特征, 例如:
 *
 * <pre>
 *  &#64;Pointcut("@annotation(alvin.study.aspect.Transactional)")
 * </pre>
 * </p>
 *
 * <p>
 * 也可以不定义专门的连接点方法, 在每个具体的连接点上来定义连接方法特征表达式, 这种方式适合仅有一个拦截方法的场景, 例如:
 *
 * <pre>
 * &#64;Around("@annotation(alvin.study.aspect.Transactional)")
 * </pre>
 * </p>
 * </li>
 *
 * <li>
 * {@link Around @Around([pointcut])} 注解用于完全拦截目标方法的执行, 如需执行目标方法, 则可以在拦截方法中通过
 * {@link ProceedingJoinPoint} 参数的 {@link ProceedingJoinPoint#proceed()} 方法进行,
 * 参考: {@link AnnotationAdvice#around(ProceedingJoinPoint)} 方法
 * </li>
 *
 * <li>
 * {@link Before @Before([pointcut])} 注解用于在目标方法执行前进行拦截, 传递 {@link JoinPoint} 参数,
 * 参考 {@link AnnotationAdvice#before(JoinPoint)} 方法
 * </li>
 *
 * <li>
 * <p>
 * {@link AfterReturning @AfterReturning(pointcut = [pointcut], returning =
 * [argument name])} 注解用于在目标方法执行后且返回值返回前进行拦截, 可以获取到目标方法的原始返回值
 * </p>
 *
 * <p>
 * 通过在注解上指定 {@code returning} 属性, 可以指定要获取的目标方法原始返回值的参数名, 例如:
 *
 * <pre>
 * &#64;AfterReturning(pointcut = "point()", returning = "result")
 * </pre>
 * <p>
 * 其中的 {@code returning = "result"} 表示该拦截方法通过 {@code result} 参数接收目标方法的原始返回值, 参考:
 * {@link AnnotationAdvice#afterReturning(JoinPoint, Object)} 方法
 * </p>
 * </li>
 *
 * <li>
 * {@link After @After([pointcut])} 注解用于在目标方法执行后进行拦截, 传递 {@link JoinPoint} 参数,
 * 参考 {@link AnnotationAdvice#before(JoinPoint)} 方法
 * </li>
 *
 * <li>
 * <p>
 * {@link AfterThrowing @AfterThrowing(pointcut = [pointcut], throwing =
 * [argument name])} 注解用于在目标方法抛出异常时进行拦截, 可以获取到目标方法抛出的原始异常
 * </p>
 *
 * <p>
 * 通过在注解上指定 {@code throwing} 属性, 可以指定要获取的目标方法原始异常的参数名, 例如:
 *
 * <pre>
 * &#64;AfterThrowing(pointcut = "point()", throwing = "exception")
 * </pre>
 * <p>
 * 其中的 {@code throwing = "exception"} 表示该拦截方法通过 {@code exception} 参数接收目标方法的原始异常,
 * 参考:
 * {@link AnnotationAdvice#afterThrowing(JoinPoint, Throwable)} 方法
 * </p>
 * </li>
 * </ul>
 * </p>
 */
@Slf4j
@Aspect
@Component
public class AnnotationAdvice {
    /**
     * 用于发送拦截消息的消息队列
     */
    private final BlockingQueue<Message> messageQueue;

    /**
     * 用于对象 JSON 序列化
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造器, 注入所需参数
     *
     * @param messageQueue 用于发送拦截消息的消息队列
     * @param objectMapper 用于对象 JSON 序列化
     */
    public AnnotationAdvice(
        @Qualifier("mqForAnnotationAdvice") BlockingQueue<Message> messageQueue,
        ObjectMapper objectMapper) {
        this.messageQueue = messageQueue;
        this.objectMapper = objectMapper;
    }

    /**
     * 定义切点
     *
     * <p>
     * {@code @annotation} 用来定义要拦截方法所应具备的注解, 在本例中, 所有具备
     * {@link Transactional @Transactional} 注解的方法均会被拦截
     * </p>
     *
     * <p>
     * 在本例中, 被拦截的目标方法为
     * {@link WorkingService#workWithTransactional(Worker)
     * WorkingService.workWithTransactional(Worker)} 方法, 其具备
     * {@link Transactional @Transactional} 注解
     * </p>
     */
    @Pointcut("@annotation(alvin.study.springboot.aop.aspect.Transactional)")
    public void point() { }

    /**
     * 在目标方法执行前进行拦截的方法
     *
     * @param jp 连接点对象, 表示被拦截的目标方法信息
     */
    @Before("point()")
    public void before(JoinPoint jp) {
        log.info("[AnnotationAdvice] Method \"{}\" will be called", jp.getSignature());
        messageQueue.add(new Message(
            this,
            Step.BEFORE,
            jp.getSignature().toLongString(),
            jp.getThis(),
            jp.getArgs(),
            null));
    }

    /**
     * 在目标方法执行后进行拦截的方法
     *
     * @param jp 连接点对象, 表示被拦截的目标方法信息
     */
    @After("point()")
    public void after(JoinPoint jp) {
        log.info("[AnnotationAdvice] Method \"{}\" was called", jp.getSignature());
        messageQueue.add(new Message(
            this,
            Step.AFTER,
            jp.getSignature().toLongString(),
            jp.getThis(),
            jp.getArgs(),
            null));
    }

    /**
     * 在目标方法执行后进行拦截的方法
     *
     * <p>
     * 可以通过指定一个参数来接收目标方法的原始返回值, 参数指定通过 {@link AfterReturning @AfterReturning} 注解的
     * {@code returning} 参数指定, 对应方法的 {@code result} 参数
     * </p>
     *
     * @param jp 连接点对象, 表示被拦截的目标方法信息
     */
    @AfterReturning(pointcut = "point()", returning = "result")
    public void afterReturning(JoinPoint jp, Object result) {
        log.info("[AnnotationAdvice] Method \"{}\" was called, and return value is: {}", jp.getSignature(), result);
        messageQueue.add(new Message(
            this,
            Step.AFTER_RETURNING,
            jp.getSignature().toLongString(),
            jp.getThis(),
            jp.getArgs(),
            result));
    }

    /**
     * 在目标方法抛出异常时进行拦截的方法
     *
     * <p>
     * 可以通过指定一个参数来接收目标方法抛出的原始异常, 参数指定通过 {@link AfterThrowing @AfterThrowing} 注解的
     * {@code throwing} 参数指定, 对应方法的 {@code exception} 参数
     * </p>
     *
     * @param jp        连接点对象, 表示被拦截的目标方法信息
     * @param exception 目标方法抛出的异常对象
     */
    @AfterThrowing(pointcut = "point()", throwing = "exception")
    public void afterThrowing(JoinPoint jp, Throwable exception) {
        log.info(
            "[AnnotationAdvice] Method \"{}\" was called, and raised exception is", jp.getSignature(), exception);

        messageQueue.add(
            new Message(
                this,
                Step.AFTER_THROWING,
                jp.getSignature().toLongString(),
                jp.getThis(),
                jp.getArgs(),
                exception
            )
        );
    }

    /**
     * 取代目标方法执行的拦截方法
     *
     * <p>
     * 当执行目标方法时, 首先会进入该拦截方法, 在拦截方法内部, 通过 {@link ProceedingJoinPoint} 参数的
     * {@link {@link ProceedingJoinPoint#proceed()} 来执行目标方法
     * </p>
     *
     * <p>
     * 该方法灵活度较大, 可以对要调用的目标函数的参数进行修改, 亦可对目标方法的返回值进行修改
     * </p>
     *
     * @param jp 连接点对象, 表示被拦截的目标方法信息, 并且可以通过 {@link ProceedingJoinPoint#proceed()}
     *           方法执行目标方法本身
     * @throws Throwable 抛出目标方法可能会抛出的异常
     */
    @Around("point()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        log.info("[AnnotationAdvice] Method \"{}\" was called", jp.getSignature());

        // 演示如何从 ProceedingJoinPoint 对象获取标记在方法上的注解
        var sign = (MethodSignature) jp.getSignature();
        var anno = sign.getMethod().getAnnotation(Transactional.class);
        log.info("Annotation \"{}\" was got", anno);

        messageQueue.add(
            new Message(
                this,
                Step.AROUND,
                jp.getSignature().toLongString(),
                jp.getThis(),
                jp.getArgs(),
                null
            )
        );

        var result = jp.proceed();
        if (result instanceof String r) {
            try {
                var json = objectMapper.readValue(r, new TypeReference<Map<String, Object>>() { });
                json.put("addition", this.getClass().getSimpleName());

                result = objectMapper.writeValueAsString(json);
            } catch (Exception ignored) {
            }
        }
        return result;
    }
}
