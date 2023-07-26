package alvin.study.springboot.mybatis.builder;

import alvin.study.springboot.mybatis.infra.entity.common.TenantedEntity;

/**
 * 实体构建器超类
 */
public abstract class Builder<T> {
    // 组织 id
    protected Long orgId;

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
    protected <R extends TenantedEntity> R fillOrgId(R entity) {
        if (orgId != null) {
            entity.setOrgId(orgId);
        }
        return entity;
    }
}
