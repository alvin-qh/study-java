package alvin.study.springboot.graphql.app.api.query.common;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;

import org.springframework.graphql.data.method.annotation.SchemaMapping;

import alvin.study.springboot.graphql.infra.entity.User;
import alvin.study.springboot.graphql.infra.entity.common.AuditedEntity;

/**
 * 继承 {@link TenantedBaseQuery} 类, 实现 {@link AuditedEntity} 实体类的 GraphQL 查询
 *
 * 该类为子类提供了 {@code createdAt}, {@code updatedAt}, {@code createdByUser}, {@code updatedByUser} 这四个字段的查询
 */
public class AuditedBaseQuery<T extends AuditedEntity> extends TenantedBaseQuery<T> {
    /**
     * 解析 {@code createdAt} 字段, 表示实体的创建时间
     *
     * <p>
     * 该方法将实体中存储的 {@code createdAt} 字段值由 {@link java.time.Instant Instant} 类型转换为
     * {@link java.time.OffsetDateTime OffsetDateTime} 类型
     * </p>
     *
     * @param entity 实体对象
     * @return {@link java.time.OffsetDateTime OffsetDateTime} 类型的实体创建时间
     */
    @SchemaMapping
    public OffsetDateTime createdAt(T entity) {
        return entity.getCreatedAt().atOffset(ZoneOffset.UTC);
    }

    /**
     * 解析 {@code updatedAt} 字段, 表示实体的更新时间
     *
     * <p>
     * 该方法将实体中存储的 {@code updatedAt} 字段值由 {@link java.time.Instant Instant} 类型转换为
     * {@link java.time.OffsetDateTime OffsetDateTime} 类型
     * </p>
     *
     * @param entity 实体对象
     * @return {@link java.time.OffsetDateTime OffsetDateTime} 类型的实体更新时间
     */
    @SchemaMapping
    public OffsetDateTime updatedAt(T entity) {
        return entity.getUpdatedAt().atOffset(ZoneOffset.UTC);
    }

    /**
     * 解析实体的创建人 {@link User} 对象
     *
     * <p>
     * 该方法将实体中的 {@code createdBy} 字段值 ({@link Long} 类型) 转换为 {@link User} 类型对象
     * </p>
     *
     * <p>
     * 如果批量查询 {@link User} 类型实体对象时, 可以通过 {@link DataLoader} 类型对象进行优化,
     * {@link DataLoader} 类型查询的原理为:
     * <p>
     * {@link DataLoader} 类型对象通过异步方式执行查询, 当批量查询 {@link User} 类型实体对象时,
     * 每个查询都会返回一个 {@link CompletableFuture} 类型对象, 当批量查询结束后,
     * 会将所有查询进行合并, 包括: 将查询条件合并为一个集合; 将查询给到
     * {@link alvin.study.springboot.graphql.app.dataloader.UserLoader UserLoader}
     * 类型对象, 批量查询结果后, 返回一个 {@link java.util.Map Map} 类型对象, 再根据 {@code Key}
     * 值将查询结果进行分发
     * </p>
     *
     * <p>
     * 除了通过 {@link org.dataloader.DataLoader DataLoader} 类型对象进行优化外, 还可以通过在
     * {@code Controller} 类中通过 {@link org.springframework.graphql.data.method.annotation.BatchMapping BatchMapping}
     * 注解将多次单次查询合并为一次批量查询, 从而减少查询次数, 参见
     * {@link alvin.study.springboot.graphql.app.api.query.DepartmentQuery#children(alvin.study.springboot.graphql.infra.entity.Department, String, int)
     * DepartmentQuery.children(Department, String, int)} 方法
     * </p>
     * </p>
     *
     * @param entity 实体对象
     * @param loader {@link DataLoader} 对象, 用于异步加载 {@link User} 对象, 实际注入了
     *               {@link alvin.study.springboot.graphql.app.dataloader.UserLoader UserLoader} 类型对象
     * @return {@link User} 类型的实体创建人
     */
    @SchemaMapping
    public CompletableFuture<User> createdByUser(T entity, DataLoader<Long, User> loader) {
        return loader.load(entity.getCreatedBy());
    }

    /**
     * 解析实体的更新人 {@link User} 对象
     *
     * <p>
     * 该方法将实体中的 {@code updatedBy} 字段值 ({@link Long} 类型) 转换为 {@link User} 类型对象
     * </p>
     *
     * <p>
     * 如果批量查询 {@link User} 类型实体对象时, 可以通过 {@link DataLoader} 类型对象进行优化,
     * {@link DataLoader} 类型查询的原理为:
     *
     * <p>
     * {@link DataLoader} 类型对象通过异步方式执行查询, 当批量查询 {@link User} 类型实体对象时,
     * 每个查询都会返回一个 {@link CompletableFuture} 类型对象, 当批量查询结束后,
     * 会将所有查询进行合并, 包括: 将查询条件合并为一个集合; 将查询给到
     * {@link alvin.study.springboot.graphql.app.dataloader.UserLoader UserLoader}
     * 类型对象, 批量查询结果后, 返回一个 {@link java.util.Map Map} 类型对象, 再根据 {@code Key}
     * 值将查询结果进行分发
     * </p>
     *
     * <p>
     * 除了通过 {@link org.dataloader.DataLoader DataLoader} 类型对象进行优化外, 还可以通过在
     * {@code Controller} 类中通过 {@link org.springframework.graphql.data.method.annotation.BatchMapping BatchMapping}
     * 注解将多次单次查询合并为一次批量查询, 从而减少查询次数, 参见
     * {@link alvin.study.springboot.graphql.app.api.query.DepartmentQuery#children(alvin.study.springboot.graphql.infra.entity.Department, String, int)
     * DepartmentQuery.children(Department, String, int)} 方法
     * </p>
     * </p>
     *
     * @param entity 实体对象
     * @param loader {@link DataLoader} 对象, 用于异步加载 {@link User} 对象
     * @return {@link User} 类型的实体更新人
     */
    @SchemaMapping
    public CompletableFuture<User> updatedByUser(T entity, DataLoader<Long, User> loader) {
        return loader.load(entity.getUpdatedBy());
    }
}
