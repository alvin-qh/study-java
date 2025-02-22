package alvin.study.springboot.mybatis.core;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import lombok.RequiredArgsConstructor;

/**
 * 测试用手动事务管理器
 *
 * <p>
 * 在测试时, 有时候不方便应用
 * {@link org.springframework.transaction.annotation.Transactional @Transactional}
 * 注解, 此时可以使用该事务管理器手动启动和结束事务
 * </p>
 *
 * <p>
 * 该类型内部使用了 {@link PlatformTransactionManager} 对象进行事务管理
 * </p>
 *
 * <p>
 * 该类型对象通过
 * {@code TestingConfig.testingTransactionManager(PlatformTransactionManager)} 方法进行装配, 产生的对象交由 Bean 容器管理
 * </p>
 */
@RequiredArgsConstructor
public class TestingTransactionManager {
    // Spring 事务管理接口对象
    private final PlatformTransactionManager tm;

    /**
     * 启动事务, 即实例化一个 {@link TestingTransaction} 类型对象并返回
     *
     * @param readOnly 事务的只读性
     * @return {@link TestingTransaction} 类型事务对象
     */
    public TestingTransaction begin(boolean readOnly) {
        return new TestingTransactionImpl(readOnly);
    }

    /**
     * 实现 {@link TestingTransaction} 接口, 通过 {@link PlatformTransactionManager}
     * 对象完成实际的事务启动和提交操作
     */
    class TestingTransactionImpl implements TestingTransaction {
        // 事务的进行状态对象
        private TransactionStatus status;

        /**
         * 构造器, 同时启动事务
         *
         * @param readOnly 是否为只读事务. 部分数据库会对只读事务进行优化
         */
        TestingTransactionImpl(boolean readOnly) {
            // 实例化一个事务定义对象
            var definition = new DefaultTransactionDefinition();
            // 设置事务的只读性
            definition.setReadOnly(readOnly);

            // 获取事务, 会根据前一个事务的传播性, 启动新事物或合并到前一个事务中
            status = tm.getTransaction(definition);
        }

        @Override
        public void commit() {
            if (status != null) {
                tm.commit(status);
                status = null;
            }
        }
    }
}
