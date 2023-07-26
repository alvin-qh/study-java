package alvin.study.springboot.jpa.infra.entity.common;

import alvin.study.springboot.jpa.core.context.Context;
import alvin.study.springboot.jpa.infra.entity.Org;
import jakarta.persistence.PrePersist;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 实体操作监听器, 监听对实体的增删查改操作
 *
 * <p>
 * 配合实体类型上的 {@link jakarta.persistence.EntityListeners @EntityListeners} 注解,
 * 用来启用该监听器, 对于继承了 {@link TenantedEntity} 的实体类对象进行处理
 * </p>
 *
 * <p>
 * 该监听器的作用是在实体创建时, 为实体设置多租户 {@code orgId}, 参见: {@link PrePersist @PrePersist} 注解
 * </p>
 *
 * <p>
 * {@code orgId} 参数值通过 {@link Context#get(String)} 方法获取
 * </p>
 */
public class TenantedEntityListener {
    // 上下文对象
    @Autowired
    private Context context;

    /**
     * 获取租户 ID, 并设置到对应的实体对象中
     */
    @PrePersist
    public void touchForCreate(Object target) {
        // 判断要存储的实体对象是否为 TenantedEntity 类型
        if (target instanceof TenantedEntity entity) {
            // 从上下文中获取 Org 对象, 即租户对象
            Org org = context.getOrDefault(Context.ORG, null);
            if (entity.getOrgId() == null && org != null) {
                // 给实体设置租户 ID
                entity.setOrgId(org.getId());
            }
        }
    }
}
