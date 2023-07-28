package alvin.study.guice.aspect.anno;

import java.lang.reflect.Method;

/**
 * 事件处理类型的接口
 */
public interface EventHandler {
    /**
     * 处理一个事件
     *
     * @param obj       引发事件的对象
     * @param method    引发事件的方法
     * @param arguments 引发事件方法的参数
     */
    void handler(Object obj, Method method, Object[] arguments);
}
