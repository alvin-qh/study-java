package alvin.study.app.api.schema.type.common;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 所有具备审计字段的 Type 类超类
 *
 * <p>
 * 对应 {@link alvin.study.infra.entity.common.AuditedEntity AuditedEntity} 类型
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AuditedType extends TenantedType {
    // 创建人
    protected Long createdBy;

    // 更新人
    protected Long updatedBy;

    // 创建时间
    protected OffsetDateTime createdAt;

    // 更新时间
    protected OffsetDateTime updatedAt;
}
