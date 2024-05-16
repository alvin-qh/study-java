package alvin.study.springboot.jpa.core.context;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 自定义请求属性对象 (请求上下文)
 *
 * <p>
 * 当没有实际请求而又需要使用
 * {@link org.springframework.web.context.annotation.RequestScope @RequestScope}
 * 注解范围的注入对象, 可以通过这个类自定义一个请求上下文对象
 * </p>
 *
 * @see RequestAttributes
 * @see RequestContextHolder
 */
public class CustomRequestAttributes implements RequestAttributes {
    // 存储属性值的 Map 对象
    private final Map<String, Object> attributes = new HashMap<>();

    /**
     * 将 Context 对象注册到当前请求属性对象中
     */
    public static Context register(Context context) {
        var cra = new CustomRequestAttributes();
        cra.setAttribute(Context.KEY, context, SCOPE_REQUEST);
        RequestContextHolder.setRequestAttributes(cra);
        return context;
    }

    /**
     * 取消当前 Context 对象的注册
     */
    public static void unregister() {
        var attributes = RequestContextHolder.currentRequestAttributes();
        attributes.removeAttribute(Context.KEY, SCOPE_REQUEST);
    }

    /**
     * 获取请求属性值
     */
    @Override
    public Object getAttribute(String name, int scope) {
        if (scope != RequestAttributes.SCOPE_REQUEST) {
            return null;
        }
        return this.attributes.get(name);
    }

    /**
     * 设置请求属性值
     */
    @Override
    public void setAttribute(String name, Object value, int scope) {
        if (scope == RequestAttributes.SCOPE_REQUEST) {
            this.attributes.put(name, value);
        }
    }

    /**
     * 删除请求属性值
     */
    @Override
    public void removeAttribute(String name, int scope) {
        if (scope == RequestAttributes.SCOPE_REQUEST) {
            this.attributes.remove(name);
        }
    }

    /**
     * 获取请求属性值的 key 集合
     */
    @Override
    public String[] getAttributeNames(int scope) {
        if (scope == RequestAttributes.SCOPE_REQUEST) {
            return this.attributes.keySet().toArray(String[]::new);
        }
        return new String[0];
    }

    /**
     * 注册属性并回调函数
     */
    @Override
    public void registerDestructionCallback(String name, Runnable callback, int scope) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object resolveReference(String key) {
        return null;
    }

    @Override
    public String getSessionId() { return ""; }

    @Override
    public Object getSessionMutex() { return this; }
}
