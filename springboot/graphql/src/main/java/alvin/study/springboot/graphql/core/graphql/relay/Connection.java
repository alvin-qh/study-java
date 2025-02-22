package alvin.study.springboot.kickstart.core.graphql.relay;

/**
 * 扩展原本的 {@link graphql.relay.Connection} 类型, 增加 {@link #getTotalCount()} 属性
 */
public interface Connection<T> extends graphql.relay.Connection<T> {
    /**
     * 获取总记录数
     *
     * @return 总记录数
     */
    int getTotalCount();
}
