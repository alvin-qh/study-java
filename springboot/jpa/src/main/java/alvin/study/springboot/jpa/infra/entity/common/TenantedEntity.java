package alvin.study.springboot.jpa.infra.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.Filter;

import lombok.Getter;
import lombok.Setter;

/**
 * 多租户实体类的超类
 *
 * <p>
 * 所谓"多租户", 最基本的实现即通过一个 {@code orgId} 字段区分不同数据所属于的相关的"租户". 在创建每一条数据时,
 * 需要根据数据所属的租户, 为每条数据增加 {@code orgId} 值; 查询数据时, 需要在 SQL 的 {@code where} 子句中添加
 * {@code orgId = :orgId} 条件来将查询范围约束在指定的租户下
 * </p>
 *
 * <p>
 * 通过 {@link EntityListeners @EntityListeners} 注解为实体类型设置
 * {@link TenantedEntityListener} 监听器, 为实体增加"租户 {@code id}" 设置功能
 * </p>
 *
 * <p>
 * 查询时, 需要自动在 SQL 的 {@code where} 子句中增加 {@code orgId=:orgId} 条件, 这个工作可以由
 * Hibernate 的查询过滤器 {@link Filter @Filter} 来完成
 * <ol>
 * <li>
 * 在包含所有实体对象的包中, 定义 {@code /alvin/study/infra/entity/package-info.java} 文件,
 * 在其中定义过滤器, 参见 {@link org.hibernate.annotations.FilterDef @FilterDef} 注解,
 * 需要定义过滤器的名称和用于过滤的字段名
 * </li>
 * <li>
 * 如果定义的过滤器比较少, 也可以在实体类型上直接定义, 或者定义在实体的超类上, 例如 {@link TenantedEntity}
 * 作为所有多租户实体类型的超类, 在其上定义 {@link org.hibernate.annotations.FilterDef @FilterDef}
 * 注解, 所有的子类型都会继承
 * </li>
 * <li>
 * 通过 {@link Filter @Filter} 注解, 注解在需要使用过滤器的实体类型上, 并指定要使用过滤器的名称和过滤条件
 * </li>
 * <li>
 * 要使过滤器生效, 除了上述两步外, 还需要为数据库当前的会话连接启用特定名称的过滤器, 具体方法为:
 * <p>
 * <b>
 * 获取当前线程上下文的 {@link javax.persistence.EntityManager EntityManager} 对象
 * </b>
 *
 * <pre>
 * @PersistenceContext
 * private EntityManager em;
 * </pre>
 * <p>
 * {@link javax.persistence.PersistenceContext @PersistenceContext} 注解用于注入
 * {@code EntityManager} 对象
 * </p>
 *
 * <p>
 * <b>
 * 获取和 {@code EntityManager} 对象相关的 {@link org.hibernate.Session Session} 对象,
 * 并启用过滤器
 * </b>
 *
 * <pre>
 * var session = em.unwrap(Session.class);
 * session.enableFilter("tenantFilter").setParameter("orgId", org.getId()).validate();
 * </pre>
 * <p>
 * 表示启动名为 {@code "tenantFilter"} 的过滤器, 并设置过滤器参数 {@code "orgId"} 的值, 之后, 在当前线程下,
 * {@code EntityManager} 对象的指定过滤器被开启
 * </p>
 * </li>
 * <li>
 * 最后, 需要启过滤器, 需要有事务的支持, 即方法上需要添加
 * {@link org.springframework.transaction.annotation.Transactional @Transactional}
 * 注解, 或者在代码中手动开启事务
 * </li>
 * </ol>
 * </p>
 */
// @FilterDef(name = "tenantFilter", parameters = { @ParamDef(name = "orgId",
// type = "long") })
@Getter
@Setter
@Filter(name = "tenantFilter", condition = "org_id = :orgId")
@MappedSuperclass
@EntityListeners({ TenantedEntityListener.class })
public abstract class TenantedEntity extends BaseEntity {
    /**
     * 租户字段
     *
     * <p>
     * 该字段表示一个组织 {@code id}, 即租户 {@code id}, 不同的租户需要通过该字段予以区分
     * </p>
     *
     * <p>
     * 已共享数据表的多租户实线方式来说, 每个实体都需要有区分租户的 {@code org_id} 字段, 每个查询都需要在 {@code where}
     * 条件中增加租户 {@code id}, 即 {@code where org_id=? and ...}, 这样才能起到多个租户共同使用一张数据表的作用
     * </p>
     */
    @Column(name = "org_id", updatable = false)
    private Long orgId;
}
