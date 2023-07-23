package alvin.study.app.api.schema.type.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 所有具备多租户 id (组织 id) 的 Type 类型超类
 *
 * <p>
 * 对应 {@link alvin.study.infra.entity.common.TenantedEntity TenantedEntity} 类型
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class TenantedType extends BaseType {
    // 组织 id
    protected Long orgId;
}
