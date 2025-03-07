package alvin.study.springboot.graphql.builder;

import alvin.study.springboot.graphql.infra.entity.common.AuditedEntity;
import alvin.study.springboot.graphql.infra.entity.common.TenantedEntity;

/**
 * 实体构建器超类
 */
public abstract class Builder<T> {
    protected Long orgId;
    protected Long createdBy;
    protected Long updatedBy;

    /**
     * 创建实体对象 (非持久化)
     *
     * @return 未进行持久化操作的实体对象
     */
    public abstract T build();

    /**
     * 创建实体对象 (持久化)
     *
     * @return 以进行持久化操作的实体对象
     */
    public abstract T create();

    /**
     * 设置组织 id
     *
     * @param orgId 组织 id
     * @return 当前对象
     */
    public Builder<T> withOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    /**
     * 填充 {@code orgId} 字段
     *
     * @param entity 多租户实体对象
     * @return 返回多租户实体对象
     */
    protected <R extends TenantedEntity> R complete(R entity) {
        if (orgId != null) {
            entity.setOrgId(orgId);
        }
        if (entity instanceof AuditedEntity ae) {
            ae.setCreatedBy(createdBy);
            ae.setUpdatedBy(updatedBy);
        }
        return entity;
    }
}
