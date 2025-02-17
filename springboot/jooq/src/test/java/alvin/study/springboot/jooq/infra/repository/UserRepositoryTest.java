package alvin.study.springboot.jooq.infra.repository;

import alvin.study.springboot.jooq.IntegrationTest;
import alvin.study.springboot.jooq.infra.model.UserType;
import alvin.study.springboot.jooq.infra.repository.common.BaseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link UserRepository} 类型对
 * {@link alvin.study.springboot.jooq.infra.model.public_.tables.User#USER USER} 表进行增删查改操作
 */
class UserRepositoryTest extends IntegrationTest {
    // 注入 Repository 对象
    @Autowired
    private UserRepository repository;

    /**
     * 测试实体对象持久化操作
     *
     * <p>
     * 通过
     * {@link BaseRepository#newRecord(java.util.function.Consumer)
     * BaseRepository.newRecord(Consumer)} 方法产生一个
     * {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.UserRecord UserRecord} 持久化对象
     * </p>
     *
     * <p>
     * 通过 {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.UserRecord#store()
     * UserRecord.store()} 方法对实体对象进行持久化操作
     * </p>
     */
    @Test
    @Transactional
    void store_shouldInsertRecord() {
        // 持久化实体对象
        var rec = repository.newRecord(
            r -> r.setAccount("alvin")
                    .setPassword(passwordUtil.encrypt("123456"))
                    .setType(UserType.NORMAL)
                    .store());
        then(rec.getId()).isNotNull();

        // 确认实体对象已被持久化
        var mayUser = repository.selectById(rec.getId());
        then(mayUser).isPresent().get().matches(
            r -> Objects.equals(r.getAccount(), "alvin")
                 && passwordUtil.verify("123456", r.getPassword()));
    }

    /**
     * 测试实体对象更新操作
     *
     * <p>
     * 通过 {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.UserRecord#update()
     * UserRecord.update()} 方法对实体对象进行更新操作
     * </p>
     */
    @Test
    @Transactional
    void update_shouldUpdateRecord() {
        // 持久化实体对象
        var rec = repository.newRecord(
            r -> r.setAccount("alvin")
                    .setPassword(passwordUtil.encrypt("123456"))
                    .setType(UserType.NORMAL)
                    .store());
        then(rec.getId()).isNotNull();

        // 更新实体字段
        var n = rec.setAccount("alvin-new").setType(UserType.ADMIN).update();
        then(n).isOne();

        // 确认实体对象已被更新
        var mayUser = repository.selectById(rec.getId());
        then(mayUser).isPresent().get().matches(
            r -> Objects.equals(r.getAccount(), "alvin-new")
                 && Objects.equals(r.getType(), UserType.ADMIN));
    }

    /**
     * 测试实体对象删除操作
     *
     * <p>
     * 通过 {@link alvin.study.springboot.jooq.infra.model.public_.tables.records.UserRecord#delete()
     * UserRecord.delete()} 方法对实体对象进行删除操作
     * </p>
     */
    @Test
    @Transactional
    void delete_shouldDeleteRecord() {
        // 持久化实体对象
        var rec = repository.newRecord(
            r -> r.setAccount("alvin")
                    .setPassword(passwordUtil.encrypt("123456"))
                    .setType(UserType.NORMAL)
                    .store());
        then(rec.getId()).isNotNull();

        // 删除实体对象
        rec.delete();

        // 确认实体对象已被删除
        var mayUser = repository.selectById(rec.getId());
        then(mayUser).isEmpty();
    }

    /**
     * 测试 {@link UserRepository#selectByAccount(String)} 方法, 查询指定账号的用户对象
     * ({@link alvin.study.springboot.jooq.infra.model.public_.tables.records.UserRecord
     * UserRecord})
     */
    @Test
    @Transactional
    void selectByAccount_shouldSelectUserByAccount() {
        // 持久化实体对象
        var rec = repository.newRecord(
            r -> r.setAccount("alvin")
                    .setPassword(passwordUtil.encrypt("123456"))
                    .setType(UserType.NORMAL)
                    .store());
        then(rec.getId()).isNotNull();

        // 根据账号名称查询用户实体
        // 确认查询结果正确
        var mayUser = repository.selectByAccount("alvin");
        then(mayUser).isPresent().get().matches(
            r -> Objects.equals(r.getId(), rec.getId())
                 && Objects.equals(r.getAccount(), "alvin"));
    }
}
