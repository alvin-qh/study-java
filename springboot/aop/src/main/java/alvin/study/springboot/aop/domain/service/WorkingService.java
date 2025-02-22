package alvin.study.springboot.aop.domain.service;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.annotations.VisibleForTesting;

import lombok.AllArgsConstructor;

import alvin.study.springboot.aop.aspect.AnnotationAdvice;
import alvin.study.springboot.aop.aspect.MethodAdvice;
import alvin.study.springboot.aop.aspect.Transactional;
import alvin.study.springboot.aop.domain.model.Worker;

/**
 * 包含被拦截方法的类
 *
 * <p>
 * 因 {@link WorkingService#changeObjectMapper(ObjectMapper)} 方法, 当前类无法为单例,
 * 参见类型上的 {@link Scope @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)} 注解
 * </p>
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Service
@AllArgsConstructor
public class WorkingService {
    // 象序列化为 JSON 字符串
    private ObjectMapper objectMapper;

    /**
     * 测试 {@link MethodAdvice MethodAdvice} 切面所拦截的目标方法
     *
     * @param worker 方法参数
     * @return 字符串
     * @throws JsonProcessingException
     */
    public String work(Worker worker) throws JsonProcessingException {
        return objectMapper.writeValueAsString(worker);
    }

    /**
     * 测试 {@link AnnotationAdvice AnnotationAdvice} 切面所拦截的目标方法
     *
     * @param worker 方法参数
     * @return 字符串
     * @throws JsonProcessingException
     */
    @Transactional
    public String workWithTransactional(Worker worker) throws JsonProcessingException {
        return objectMapper.writeValueAsString(worker);
    }

    /**
     * 供测试使用的方法, 用于注入 mock 过的 {@link ObjectMapper} 类型
     *
     * <p>
     * 这里由于提供了修改对象成员字段的方法, 破坏了当前类型的幂等性, 所以需要将当前类型注解为非单例模式, 参见当前类型的
     * {@link Scope @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)} 注解
     * </p>
     *
     * @param objectMapper mock 过的 {@link ObjectMapper} 参数
     */
    @VisibleForTesting
    /* private */ void changeObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
