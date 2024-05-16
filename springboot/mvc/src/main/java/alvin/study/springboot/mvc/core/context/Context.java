package alvin.study.springboot.mvc.core.context;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 上下文对象接口
 *
 * <p>
 * 该接口的实例化对象在 {@code BeansConfig.context()} 方法中指定, 会为每次请求实例化一个对象, 该对象存储一组 Key/Value 值,
 * 并可以在当前请求的所有后续操作中获取
 * </p>
 *
 * <p>
 * 通过
 * {@link org.springframework.web.context.annotation.RequestScope @RequestScope}
 * 注解将 {@code Context} 的生命周期指定为"每次请求", 即为不同的请求创建不同的 {@code Context} 对象
 * </p>
 *
 * <p>
 * 进一步,
 * {@link org.springframework.web.context.annotation.RequestScope @RequestScope}
 * 注解的作用是将对象交由
 * {@link RequestContextHolder} 对象管理, 其
 * {@link RequestContextHolder#currentRequestAttributes()} 方法用于获取一个
 * {@link RequestAttributes} 请求上下文对象, 可以在在对象中存储需要在其后使用的键值对
 * </p>
 *
 * @see WebContext
 */
public interface Context {
    // 获取 Context 存储的内置 Key 值
    String KEY = ScopedProxyUtils.getTargetBeanName("context");

    // 存储 OrgCode 的 Key 值
    String KEY_ORG_CODE = "ORG_CODE";
    // 存储 UserId 的 Key 值
    String KEY_USER_ID = "USER_ID";

    /**
     * 获取当前请求范围内注册的 {@link Context} 对象
     *
     * @return 当前请求范围内注册的 {@link Context} 对象
     * @see ScopedProxyUtils#getTargetBeanName(String)
     * @see RequestContextHolder#currentRequestAttributes()
     */
    public static Context current() {
        // 获取当前请求上下文属性集合
        var attributes = RequestContextHolder.currentRequestAttributes();
        // 从属性集合中根据 Context 对象的 Key 获取 Context 对象
        return (Context) attributes.getAttribute(KEY, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 当前接口实现类的所给的名称
     *
     * @return 名称字符串
     */
    String name();

    /**
     * 根据 {@code Key} 获取 {@code Value} 值
     *
     * @param <T>  {@code Value} 值的类型
     * @param name {@code Key} 值
     * @return {@code Value} 值
     */
    default <T> T get(String name) {
        // 获取 Value 值, 如果 name 不存在则返回 null
        T val = getOrDefault(name, null);
        // 如果返回的值为 null, 则抛出异常
        if (val == null) {
            throw new NoContextAttributeException(String.format("context with name \"%s\" not exist", name));
        }
        return val;
    }

    /**
     * 判断上下文 {@code Key} 是否存在
     *
     * @param name {@code Key} 值
     * @return {@code Key} 是否存在
     */
    boolean has(String name);

    /**
     * 保持一组键值对
     *
     * @param name  {@code Key} 值
     * @param value {@code Value} 值
     */
    void set(String name, Object value);

    /**
     * 删除指定的 Key
     *
     * @param key {@code Key} 值
     */
    void remove(String key);

    /**
     * 清除所有的 Key
     */
    void clear();

    /**
     * 通过 {@code Key} 获取 {@code Value} 值, 如果 {@code Key} 不存在则返回默认值
     *
     * @param <T>          {@code Value} 的类型
     * @param name         {@code Key} 值
     * @param defaultValue {@code Key} 不存在情况下的默认值
     * @return {@code Value} 值
     */
    <T> T getOrDefault(String name, T defaultValue);
}
