package alvin.study.springboot.graphql.app.api.query.common;

import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;

import org.springframework.graphql.data.method.annotation.SchemaMapping;

import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.entity.common.TenantedEntity;

/**
 * 实现 {@link TenantedEntity} 类型的 GraphQL 查询映射
 */
public class TenantedBaseQuery<T extends TenantedEntity> {
    /**
     * 查询 {@link TenantedEntity} 类型的 {@code org} 字段, 将其从 {@link Long} 类型转换为 {@link Org} 类型
     *
     * <p>
     * 如果批量查询 {@link Org} 类型实体对象时, 可以通过 {@link DataLoader} 类型对象进行优化,
     * {@link DataLoader} 类型查询的原理为:
     * <p>
     * {@link DataLoader} 类型对象通过异步方式执行查询, 当批量查询 {@link Org} 类型实体对象时,
     * 每个查询都会返回一个 {@link CompletableFuture} 类型对象, 当批量查询结束后,
     * 会将所有查询进行合并, 包括: 将查询条件合并为一个集合; 将查询给到
     * {@link alvin.study.springboot.graphql.core.graphql.dataloader.OrgLoader OrgLoader}
     * 类型对象, 批量查询结果后, 返回一个 {@link java.util.Map Map} 类型对象, 再根据 {@code Key} 值将查询结果进行分发
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
     * @param loader {@link DataLoader} 对象, 用于异步获取 {@link Org} 对象
     * @return {@link Org} 对象
     */
    @SchemaMapping
    public CompletableFuture<Org> org(T entity, DataLoader<Long, Org> loader) {
        return loader.load(entity.getOrgId());
    }
}
