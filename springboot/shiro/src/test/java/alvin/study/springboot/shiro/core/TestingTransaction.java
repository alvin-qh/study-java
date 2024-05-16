package alvin.study.springboot.shiro.core;

/**
 * 用于测试的事务处理类型
 *
 * <p>
 * 该类型对象用于启动和提交事务, 该类型对象构造时会启动事务, 调用 {@link TestingTransaction#commit()}
 * 方法时会提交事务
 * </p>
 *
 * <p>
 * 该类型继承了 {@link AutoCloseable} 接口, 意味着将事务作为一个资源看待, 当事务使用完毕后可以自动的提交改事务,
 * 即可以应用类似如下代码完成事务的启动和提交:
 *
 * <pre>
 * try (var tx = createTestingTransactionObject()) {
 *     // 数据操作
 * }
 * </pre>
 * </p>
 */
public interface TestingTransaction extends AutoCloseable {
    /**
     * 提交当前事务
     */
    void commit();

    /**
     * 关闭资源对象, 意味着操作结束, 此时提交事务, 即:
     *
     * <pre>
     * try (var tx = createTestingTransactionObject()) {
     *     // 数据操作
     * }
     * </pre>
     */
    @Override
    default void close() {
        this.commit();
    }
}
