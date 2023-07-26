package alvin.study.springboot.ds.app.domain.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

/**
 * 所有服务类超类
 */
public abstract class BaseService {
    // 注入事务管理器对象
    @Autowired
    private PlatformTransactionManager tm;

    /**
     * 启动事务
     *
     * @return 事务状态对象
     */
    protected TransactionStatus beginTransaction() {
        return tm.getTransaction(null);
    }

    /**
     * 提交事务
     *
     * @param status 启动事务时获得的状态对象
     */
    protected void commit(TransactionStatus status) {
        tm.commit(status);
    }

    /**
     * 回滚事务
     *
     * @param status 启动事务时获得的状态对象
     */
    protected void rollback(TransactionStatus status) {
        tm.rollback(status);
    }
}
