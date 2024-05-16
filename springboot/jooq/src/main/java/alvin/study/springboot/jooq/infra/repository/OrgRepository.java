package alvin.study.springboot.jooq.infra.repository;

import static alvin.study.springboot.jooq.infra.model.public_.Tables.ORG;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;

import alvin.study.springboot.jooq.infra.model.public_.tables.records.OrgRecord;
import alvin.study.springboot.jooq.infra.repository.common.JdbcBaseRepository;

/**
 * 操作 {@code ORG} 表实体的 Repository 类型
 */
@Repository
public class OrgRepository extends JdbcBaseRepository<OrgRecord> {
    /**
     * 构造器, 设置对应的 {@link org.jooq.Table Table} 对象
     */
    public OrgRepository() {
        super(ORG);
    }

    /**
     * 持久化 {@code ORG} 表格对应的实体
     *
     * <p>
     * Jooq 有两种持久化实体的方法:
     * <ul>
     * <li>
     * 通过 {@link OrgRecord} 对象的 {@link OrgRecord#store()} 方法进行持久化操作, 需要通过
     * {@link JdbcBaseRepository#newRecord(Consumer)} 方法产生一个 Record 对象, 或者通过
     * {@link JdbcBaseRepository#newRecord(Object, Consumer)} 方法将一个 Pojo 对象转化为 Record 对象
     * </li>
     * <li>
     * 通过 {@link DSLContext#insertInto(org.jooq.Table)
     * DSLContext.insertInto(Table)...} 方法插入一条记录, 从而对实体进行持久化
     * </li>
     * </ul>
     * </p>
     *
     * @param orgRecord 实体对象
     * @return 被持久化的实体的 Record 对象
     */
    public OrgRecord insert(OrgRecord orgRecord) throws DataAccessException, SQLException {
        // 执行一条 insert 语句
        return Objects.requireNonNull(
                contextManager.get().insertInto(ORG)
                    .set(ORG.NAME, orgRecord.getName())
                    .set(ORG.CREATED_AT, LocalDateTime.now(ZoneOffset.UTC))
                    .set(ORG.UPDATED_AT, LocalDateTime.now(ZoneOffset.UTC))
                    // 在返回结果中包括所有字段 (包括自增 id)
                    .returning(ORG.fields())
                    .fetchOne()
            )
            // 返回结果转为 Record 对象
            .into(OrgRecord.class);
    }

    /**
     * 更新 {@code ORG} 表格对应的实体
     *
     * <p>
     * Jooq 有两种更新实体的方法:
     * <ul>
     * <li>
     * 通过 {@link OrgRecord} 对象的 {@link OrgRecord#update()} 方法进行更新操作, 需要通过
     * {@link JdbcBaseRepository#newRecord(Consumer)} 方法产生一个 Record 对象, 或者通过
     * {@link JdbcBaseRepository#newRecord(Object, Consumer)} 方法将一个 Pojo 对象转化为 Record 对象
     * </li>
     * <li>
     * 通过 {@link DSLContext#update(org.jooq.Table) DSLContext.update(Table)...}
     * 方法更新一条记录, 从而对实体进行更新
     * </li>
     * </ul>
     * </p>
     *
     * @param orgRecord 实体的 Pojo 对象
     * @return 更新数据的条数, 取值 {@code 0} 或 {@code 1}
     */
    public int update(OrgRecord orgRecord) throws DataAccessException, SQLException {
        return contextManager.get().update(ORG)
            // 要更新的字段信息
            .set(ORG.NAME, orgRecord.getName())
            .set(ORG.UPDATED_AT, LocalDateTime.now(ZoneOffset.UTC))
            // 检索条件
            .where(ORG.ID.eq(orgRecord.getId()))
            // 执行语句
            .execute();
    }

    /**
     * 删除 {@code ORG} 表格对应的实体
     *
     * <p>
     * Jooq 有两种更新实体的方法:
     *
     * <ul>
     * <li>
     * 通过 {@link OrgRecord} 对象的 {@link OrgRecord#delete()} 方法进行删除操作, 需要通过
     * {@link JdbcBaseRepository#newRecord(Consumer)}  方法产生一个 Record 对象, 或者通过
     * {@link JdbcBaseRepository#newRecord(Object, Consumer)}  方法将一个 Pojo 对象转化为 Record 对象
     * </li>
     * <li>
     * 通过 {@link DSLContext#delete(org.jooq.Table)
     * DSLContext.delete(Table)...} 方法删除一条记录, 从而对实体进行删除
     * </li>
     * </ul>
     * </p>
     *
     * @param orgRecord 实体的 Pojo 对象
     * @return 删除数据的条数, 取值 {@code 0} 或 {@code 1}
     */
    public int delete(OrgRecord orgRecord) throws DataAccessException, SQLException {
        return contextManager.get().delete(ORG)
            // 检索条件
            .where(ORG.ID.eq(orgRecord.getId()))
            // 执行语句
            .execute();
    }

    /**
     * 根据 id 查询实体对象
     *
     * @param id 实体主键
     * @return {@link Optional} 对象, 内部为 {@link OrgRecord} 类型对象
     */
    public Optional<@NotNull OrgRecord> selectById(Long id) throws DataAccessException, SQLException {
        // 查询员工记录所有字段
        var orgs = contextManager.get().select()
            .from(ORG)
            // 查询条件
            .where(ORG.ID.eq(id))
            .fetch()
            // 返回结果转换
            .into(OrgRecord.class);

        return asOptional(orgs);
    }
}
