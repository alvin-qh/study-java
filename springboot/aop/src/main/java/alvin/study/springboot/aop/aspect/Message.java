package alvin.study.springboot.aop.aspect;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 发送的消息类型
 */
@Data
@RequiredArgsConstructor
public class Message {
    /**
     * 拦截器对象
     */
    private final Object adviceObject;

    /**
     * 拦截的时机
     *
     * @see Step
     */
    private final Step step;

    /**
     * 被拦截的方法签名
     */
    private final String signature;

    /**
     * 被拦截的方法所属对象
     *
     * <p>
     * 对于静态方法该值为 {@code null}
     * </p>
     */
    private final Object thiz;

    /**
     * 被拦截方法的参数列表
     */
    private final Object[] arguments;

    /**
     * 被拦截方法的原始返回值
     *
     * <p>
     * 仅当 {@link Step#AFTER_RETURNING} 时返回该值
     * </p>
     */
    private final Object returnObject;

    /**
     * 表示被拦截的时机
     *
     * <p>
     * 被拦截的时机表示目标方法是在 Advice 的那个拦截点被拦截的
     * </p>
     */
    public static enum Step {
        /**
         * 对应 {@link org.aspectj.lang.annotation.Before @Before} 注解
         */
        BEFORE,

        /**
         * 对应 {@link org.aspectj.lang.annotation.After @After} 注解
         */
        AFTER,

        /**
         * 对应 {@link org.aspectj.lang.annotation.AfterReturning @AfterReturning} 注解
         */
        AFTER_RETURNING,

        /**
         * 对应 {@link org.aspectj.lang.annotation.Around @Around} 注解
         */
        AROUND,

        /**
         * 对应 {@link org.aspectj.lang.annotation.AfterThrowing @AfterThrowing} 注解
         */
        AFTER_THROWING
    }
}
