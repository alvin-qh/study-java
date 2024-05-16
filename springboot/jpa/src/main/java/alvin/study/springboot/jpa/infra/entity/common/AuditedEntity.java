package alvin.study.springboot.jpa.infra.entity.common;

import alvin.study.springboot.jpa.conf.JpaConfig;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * 包含审计字段的实体超类
 *
 * <p>
 * {@link MappedSuperclass @MappedSuperclass} 注解表示当前类是实体类型的超类, 是一个必备的注解,
 * 否则子类无法将当前类中应用的配置继承下去
 * </p>
 *
 * <p>
 * 审计字段包括:
 * <ol>
 * <li>
 * 操作人员字段: {@code created_by} 和 {@code updated_by}, 均为 {@link Long} 类型,
 * 表示操作人的 id
 * </li>
 * <li>
 * 操作时间字段: {@code created_at} 和 {@code updated_at}, 均为 {@link Instant}
 * 类型, 表示操作时间
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * 和审计相关的注解包括:
 * <ul>
 * <li>{@link CreatedBy @CreatedBy} 设置创建人 {@code id}</li>
 * <li>{@link CreatedDate @CreatedDate} 设置创建时间</li>
 * <li>{@link LastModifiedBy @LastModifiedBy} 设置修改人 {@code id}</li>
 * <li>{@link LastModifiedDate @LastModifiedDate} 设置修改时间</li>
 * </ul>
 * </p>
 *
 * <p>
 * 对于
 * {@link CreatedBy @CreatedBy}, {@link LastModifiedBy @LastModifiedBy} 这两个注解,
 * 则需要配置一个实现 {@link org.springframework.data.domain.AuditorAware AuditorAware}
 * 接口的类来获取用户 {@code id}, 参见
 * {@link JpaConfig#getCurrentAuditor()
 * JpaConfig.getCurrentAuditor()} 方法中的定义
 * </p>
 *
 * <p>
 * 要令上述的审计字段生效, 需要通过 {@link EntityListeners @EntityListeners} 注解为实体类型设置
 * {@link AuditingEntityListener} 监听器
 * </p>
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners({ AuditingEntityListener.class })
public abstract class AuditedEntity extends TenantedEntity {
    /**
     * 记录实体创建者
     *
     * <p>
     * {@link CreatedBy @CreatedBy} 注解表示该字段用于记录创建当前记录的用户 {@code id}
     * </p>
     *
     * <p>
     * {@link Column @Column} 注解的 {@code updatable = false} 参数表示该字段只能在创建记录时赋值, 不允许更新
     * </p>
     */
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    /**
     * 记录实体更新者
     *
     * <p>
     * {@link LastModifiedBy @LastModifiedBy} 注解表示该字段用于记录创建当前记录的用户 {@code id}
     * </p>
     */
    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * 记录实体创建时间
     *
     * <p>
     * {@link CreatedDate @CreatedDate} 注解表示该字段用于记录创建当前记录的时间
     * </p>
     *
     * <p>
     * {@link Column @Column} 注解的 {@code updatable = false} 参数表示该字段只能在创建记录时赋值, 不允许更新
     * </p>
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    /**
     * 记录实体更新时间
     *
     * <p>
     * {@link CreatedDate @CreatedDate} 注解表示该字段用于记录更新当前记录的时间
     * </p>
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
