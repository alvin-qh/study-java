package alvin.study.springboot.jpa.infra.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import alvin.study.springboot.jpa.infra.entity.common.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户实体, 对应 {@code user} 表
 *
 * <p>
 * 当前类继承自 {@link AuditedEntity} 类, 引入租户和审计字段
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
@Table(name = "`user`")
@SQLRestriction("deleted = 0")
@SQLDelete(sql = "UPDATE `user` SET deleted = id WHERE id = ?")
public class User extends AuditedEntity {
    /**
     * 用户登录账号
     */
    @Column(name = "account")
    private String account;

    /**
     * 用户登录密码
     */
    @Column(name = "password")
    private String password;
}
