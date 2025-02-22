package alvin.study.springboot.ds.infra.repository;

import java.sql.Statement;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import alvin.study.springboot.ds.infra.entity.DataEntity;
import alvin.study.springboot.ds.infra.repository.common.BaseRepository;

/**
 * {@link DataEntity} 实体类型的持久化操作类
 */
@Repository
public class DataRepository extends BaseRepository {
    // 映射字段和对象属性的对象
    private static final RowMapper<DataEntity> ROW_MAPPER = (rs, row) -> {
        var entity = new DataEntity();
        entity.setId(rs.getLong("id"));
        entity.setName(rs.getString("name"));
        entity.setValue(rs.getString("value"));
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
    public DataEntity insert(DataEntity entity) {
        // 用于存储插入后自动产生的 Key 值
        var keyHolder = new GeneratedKeyHolder();

        // 执行插入操作
        template().update((PreparedStatementCreator) conn -> {
            var state = conn.prepareStatement(
                "insert into `data` (`name`, `value`, `created_at`, `updated_at`) values (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

            var now = Instant.now();
            state.setString(1, entity.getName());
            state.setString(2, entity.getValue());
            state.setString(3, now.toString());
            state.setString(4, now.toString());

            return state;
        }, keyHolder);

        // 获取返回的自增 id 属性值
        entity.setId(Optional.ofNullable(keyHolder.getKeys())
                .map(h -> (Long) h.get("id"))
                .orElseThrow());
        return entity;
    }

    /**
     * 根据 {@code id} 值查询 {@link DataEntity} 实体对象
     *
     * @param id {@code id} 属性值
     * @return 实体对象
     */
    public Optional<DataEntity> selectById(Long id) {
        var list = template().query(
            "select `id`, `name`, `value`, `created_at`, `updated_at` from `data` where `id` = ?", ROW_MAPPER, id);

        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /**
     * 查询所有 {@link DataEntity} 实体对象
     *
     * @return {@link DataEntity} 实体对象集合
     */
    public List<DataEntity> selectAll() {
        return template().query(
            "select `id`, `name`, `value`, `created_at`, `updated_at` from `data`", ROW_MAPPER);
    }
}
