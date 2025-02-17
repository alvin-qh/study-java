package alvin.study.springboot.ds.infra.repository;

import alvin.study.springboot.ds.infra.entity.ConfigEntity;
import alvin.study.springboot.ds.infra.repository.common.BaseRepository;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.time.Instant;
import java.util.Optional;

/**
 * {@link ConfigEntity} 实体类型的持久化操作类
 */
@Repository
public class ConfigRepository extends BaseRepository {
    // 映射字段和对象属性的对象
    private static final RowMapper<ConfigEntity> ROW_MAPPER = (rs, row) -> {
        var entity = new ConfigEntity();
        entity.setId(rs.getLong("id"));
        entity.setOrg(rs.getString("org"));
        entity.setDbName(rs.getString("db_name"));
        entity.setValid(rs.getInt("valid") == 1);
        entity.setCreatedAt(map(rs.getString("created_at"), Instant.class));
        entity.setUpdatedAt(map(rs.getString("updated_at"), Instant.class));
        return entity;
    };

    /**
     * 插入一条数据
     *
     * @param entity 实体对象
     * @return 持久化后的实体对象, 具备 {@code id} 属性值
     */
    public ConfigEntity insert(ConfigEntity entity) {
        // 用于存储插入后自动产生的 Key 值
        var keyHolder = new GeneratedKeyHolder();

        // 执行插入操作
        template().update((PreparedStatementCreator) conn -> {
            var state = conn.prepareStatement(
                "insert into `config` (`org`, `db_name`, `valid`, `created_at`, `updated_at`) values (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

            var now = Instant.now();
            state.setString(1, entity.getOrg());
            state.setString(2, entity.getDbName());
            state.setInt(3, 1);
            state.setString(4, now.toString());
            state.setString(5, now.toString());

            return state;
        }, keyHolder);

        // 获取返回的自增 id 属性值
        entity.setId(Optional.ofNullable(keyHolder.getKeys())
                .map(h -> (Long) h.get("id"))
                .orElseThrow());
        return entity;
    }

    /**
     * 更新一条数据
     *
     * @param entity 实体对象
     */
    public void update(ConfigEntity entity) {
        // 执行更新操作
        template().update((PreparedStatementCreator) conn -> {
            var state = conn.prepareStatement("""
                update `config` set `org` = ?, `db_name` = ?, `valid` = ?, `updated_at` = ?
                where `id` = ?
                """);

            var now = Instant.now();
            state.setString(1, entity.getOrg());
            state.setString(2, entity.getDbName());
            state.setInt(3, entity.isValid() ? 1 : 0);
            state.setString(4, now.toString());
            state.setLong(5, entity.getId());

            return state;
        });
    }

    /**
     * 根据 {@code org} 值查询 {@link ConfigEntity} 实体对象
     *
     * @param org {@code org} 属性值
     * @return 实体对象
     */
    public Optional<ConfigEntity> selectByOrg(String org) {
        var list = template().query("""
            select `id`, `org`, `db_name`, `valid`, `created_at`, `updated_at`
            from `config`
            where `org` = ? and `valid` = 1
            """, ROW_MAPPER, org);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
