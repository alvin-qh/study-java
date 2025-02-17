package alvin.study.springboot.jooq.infra.repository;

import static alvin.study.springboot.jooq.infra.model.public_.Tables.USER;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import alvin.study.springboot.jooq.infra.model.public_.tables.records.UserRecord;
import alvin.study.springboot.jooq.infra.repository.common.BaseRepository;

/**
 * 操作 {@code USER} 表实体的 Repository 类型
 */
@Repository
public class UserRepository extends BaseRepository<UserRecord> {
    /**
     * 构造器, 设置对应的 {@link org.jooq.Table Table} 对象
     */
    public UserRepository() {
        super(USER);
    }

    /**
     * 根据 id 查询实体对象
     *
     * @param id 实体主键
     * @return {@link Optional} 对象, 内部为 {@link UserRecord} 类型对象
     */
    public Optional<UserRecord> selectById(Long id) {
        // 查询用户记录所有字段
        var users = dsl().select()
                .from(USER)
                // 查询条件
                .where(USER.ID.eq(id))
                .fetch()
                // 返回结果转换
                .into(UserRecord.class);

        return asOptional(users);
    }

    /**
     * 根据账号查询实体对象
     *
     * @param account 账号名称
     * @return {@link Optional} 对象, 内部为 {@link UserRecord} 类型对象
     */
    public Optional<UserRecord> selectByAccount(String account) {
        // 查询用户记录所有字段
        var users = dsl().select()
                .from(USER)
                // 查询条件
                .where(USER.ACCOUNT.eq(account))
                .fetch()
                // 返回结果转换
                .into(UserRecord.class);

        return asOptional(users);
    }
}
