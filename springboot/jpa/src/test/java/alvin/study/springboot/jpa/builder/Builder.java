package alvin.study.springboot.jpa.builder;

import java.util.Locale;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.github.javafaker.Faker;

import alvin.study.springboot.jpa.infra.entity.common.TenantedEntity;

/**
 * 实体构建器超类
 */
public abstract class Builder<T> {
    protected static final Faker FAKER = new Faker(Locale.CHINA);

    // 实体管理器对象
    @PersistenceContext
    protected EntityManager em;

    // 组织 id
    protected Long orgId;

    /**
     * 创建实体对象 (非持久化)
     */
    public abstract T build();

    /**
     * 创建实体对象 (持久化)
     */
    public T create() {
        var obj = build();
        em.persist(obj);
        return obj;
    }

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
