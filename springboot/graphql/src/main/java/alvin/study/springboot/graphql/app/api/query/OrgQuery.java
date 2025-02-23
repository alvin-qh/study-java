package alvin.study.springboot.graphql.app.api.query;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.service.OrgService;
import alvin.study.springboot.graphql.core.exception.InputException;
import alvin.study.springboot.graphql.infra.entity.Org;

/**
 * 对应 {@link Org} 类型的 GraphQL 查询对象
 *
 * <p>
 * 对应 {@code classpath:graphql/org.graphqls} 中的定义
 * </p>
 */
@Controller
@RequiredArgsConstructor
public class OrgQuery {
    private final OrgService orgService;

    /**
     * 用户查询
     *
     * @param id 用户 id
     * @return 用户对象
     */
    @QueryMapping
    public Org org(@Argument String id) {
        // 查询组织
        return orgService.findById(Long.parseLong(id))
                .orElseThrow(() -> new InputException("Invalid org id"));
    }

    /**
     * 解析 `createdAt` 字段, 表示实体的创建时间
     *
     * <p>
     * 该方法将 {@link Org Org} 类型实体中存储的 {@code createdAt} 字段值由 {@link java.time.Instant Instant} 类型转换为
     * {@link java.time.OffsetDateTime OffsetDateTime} 类型
     * </p>
     *
     * @param org {@link Org Org} 类型实体对象
     * @return {@link java.time.OffsetDateTime OffsetDateTime} 类型的实体创建时间
     */
    @SchemaMapping
    public OffsetDateTime createdAt(Org org) {
        return org.getCreatedAt().atOffset(ZoneOffset.UTC);
    }

    /**
     * 解析 {@code updatedAt} 字段, 表示实体的更新时间
     *
     * <p>
     * 该方法将 {@link Org Org} 实体中存储的 {@code updatedAt} 字段值由 {@link java.time.Instant Instant} 类型转换为
     * {@link java.time.OffsetDateTime OffsetDateTime} 类型
     * </p>
     *
     * @param org {@link Org Org} 类型实体对象
     * @return {@link java.time.OffsetDateTime OffsetDateTime} 类型的实体更新时间
     */
    @SchemaMapping
    public OffsetDateTime updatedAt(Org org) {
        return org.getUpdatedAt().atOffset(ZoneOffset.UTC);
    }
}
