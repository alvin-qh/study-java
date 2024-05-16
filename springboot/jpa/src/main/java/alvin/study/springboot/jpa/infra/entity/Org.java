package alvin.study.springboot.jpa.infra.entity;

import java.time.Instant;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import alvin.study.springboot.jpa.infra.entity.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 组织实体, 同时表示租户, 对应 {@code org} 表
 *
 * <p>
 * 通过 {@link EntityListeners @EntityListeners} 注解配合
 * {@link AuditingEntityListener} 类处理 {@code createdAt} 和 {@code updatedAt}
 * 审计字段
 * </p>
 *
 * <p>
 * 当前类支持软删除: 即并不从数据表中实际删除数据, 而是通过一个标记字段表示一条数据是否可用. 软删除通过 {@link SQLRestriction @SQLRestriction}
 * 注解和 {@link SQLDelete @SQLDelete} 注解共同实现, 前者表示当前实体类型对应的查询 SQL 必须附加的查询条件,
 * 后者表示当删除当前实体对象时, 实际执行的 SQL 语句
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "org")
@SQLRestriction("deleted = 0")
@SQLDelete(sql = "UPDATE org SET deleted = id WHERE id = ?")
@EntityListeners({ AuditingEntityListener.class })
public class Org extends BaseEntity {
    /**
     * 组织名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 记录实体创建时间
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    /**
     * 记录实体修改时间
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
