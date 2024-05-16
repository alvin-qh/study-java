package alvin.study.guava.eventbus.handler;

/**
 * 事件订阅和处理接口
 */
public interface Handler {
    /**
     * 注销事件订阅
     */
    void unregister();
}
