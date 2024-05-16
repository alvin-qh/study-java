package alvin.study.springboot.kickstart.app.api.schema.type.common;

import alvin.study.springboot.kickstart.infra.entity.common.TenantedEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 所有具备多租户 id (组织 id) 的 Type 类型超类
 *
 * <p>
 * 对应 {@link TenantedEntity TenantedEntity} 类型
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
