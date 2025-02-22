package alvin.study.springboot.jooq.infra.repository;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import lombok.SneakyThrows;

import alvin.study.springboot.jooq.IntegrationTest;
import alvin.study.springboot.jooq.core.jooq.dsl.JdbcDSLContextManager;
import alvin.study.springboot.jooq.infra.model.public_.tables.records.OrgRecord;

/**
 * 测试 {@link OrgRepository} 类型对
 * {@link alvin.study.springboot.jooq.infra.model.public_.tables.Org#ORG ORG} 表进行增删查改操作
 */
class OrgRepositoryTest extends IntegrationTest {
    // 注入 Repository 对象
    @Autowired
    private OrgRepository repository;

    /**
     * 测试 {@link OrgRepository#insert(OrgRecord)} 方法, 插入实体对象
     *
     * <p>
     * 这里使用了 {@link Transactional @Transactional} 注解处理事务
     * </p>
     */
    @Test
    @SneakyThrows
    @Transactional
    void insert_shouldInsertNewRecord() {
        var rec = new OrgRecord().setName(makeUniqueName("alvin.edu"));

        rec = repository.insert(rec);
        then(rec.getId()).isNotNull();

        var mayOrg = repository.selectById(rec.getId());
        then(mayOrg).isPresent().get().matches(
            r -> Objects.nonNull(r.getCreatedAt()) && Objects.nonNull(r.getUpdatedAt()));
    }

    /**
     * 测试通过
     * {@link JdbcDSLContextManager#tx(org.jooq.TransactionalRunnable)
     * JdbcDSLContextManager.tx(TransactionalRunnable)} 以及
     * {@link JdbcDSLContextManager#txr(org.jooq.TransactionalCallable)
     * JdbcDSLContextManager.txr(TransactionalCallable)} 两个方法进行事务处理
     *
     * <p>
     * {@link JdbcDSLContextManager#tx(org.jooq.TransactionalRunnable)
     * JdbcDSLContextManager.tx(TransactionalRunnable)} 在
     * {@link org.jooq.TransactionalRunnable TransactionalRunnable} 回调中执行事务代码,
     * 并且该回调不返回结果
     * </p>
     *
     * <p>
     * {@link JdbcDSLContextManager#txr(org.jooq.TransactionalCallable)
     * JdbcDSLContextManager.txr(TransactionalCallable)} 在
     * {@link org.jooq.TransactionalCallable TransactionalCallable} 回调中执行事务代码,
     * 并且该回调需要返回一个值, 表示事务执行的结果
     * </p>
     */
    @Test
    @SneakyThrows
    void txr_shouldDSLContextTransactionWorked() {
        var orgName = makeUniqueName("alvin.edu");

        // 启动事务, 持久化一个实体对象
        var rec = contextManager.txr(c -> repository.newRecord(r -> r.setName(orgName).store()));

        // 确认持久化操作成功
        var mayOrg = repository.selectById(rec.getId());
        then(mayOrg).isPresent().get().matches(
            r -> Objects.equals(r.getName(), orgName)
                 && Objects.nonNull(r.getUpdatedAt())
                 && Objects.nonNull(r.getUpdatedAt()));
    }

    /**
     * 测试 {@link OrgRepository#update(OrgRecord)} 方法, 更新一个已经持久化的实体对象
     *
     * <p>
     * 这里使用了 {@link Transactional @Transactional} 注解处理事务
     * </p>
     */
    @Test
    @SneakyThrows
    @Transactional
    void update_shouldUpdateRecord() {
        // 持久化一个实体对象
        var rec = repository.newRecord(r -> r.setName(makeUniqueName("alvin.edu")).store());

        // 对已经持久化的实体对象进行更新操作
        var newName = makeUniqueName("alvin.edu.org");
        var n = rec.setName(newName).update();
        // 确认更新操作成功执行
        then(n).isOne();

        // 确认更新操作有效
        var mayOrg = repository.selectById(rec.getId());
        then(mayOrg).isPresent().get().extracting("name").isEqualTo(newName);
    }

    /**
     * 测试 {@link OrgRepository#delete(OrgRecord)} 方法, 删除一个已经持久化的实体对象
     *
     * <p>
     * 这里使用了 {@link Transactional @Transactional} 注解处理事务
     * </p>
     */
    @Test
    @SneakyThrows
    @Transactional
    void delete_shouldDeleteRecord() {
        // 持久化一个实体对象
        var rec = repository.newRecord(r -> r.setName(makeUniqueName("alvin.edu")).store());

        // 确认持久化操作有效
        var mayOrg = repository.selectById(rec.getId());
        then(mayOrg).isPresent();

        // 执行删除操作
        repository.delete(mayOrg.get());

        // 确认删除操作有效
        mayOrg = repository.selectById(rec.getId());
        then(mayOrg).isEmpty();
    }
}
