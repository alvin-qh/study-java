package alvin.study.springboot.jpa.infra.entity.common;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * 所有实体类超类
 *
 * <p>
 * 该类为所有子实体类定义了 {@code id} 字段
 * </p>
 *
 * <p>
 * {@link MappedSuperclass @MappedSuperclass} 注解表示当前类是实体类型的超类, 是一个必备的注解,
 * 否则子类无法将当前类中应用的配置继承下去
 * </p>
 *
 * <p>
 * 一般情况下, {@code id} 字段表示主键, 数据表的 {@code id} 字段值值由 DBMS 自动生成且无法修改, 所以 {@code id}
 * 字段为只读字段, 无需 {@code setId} 方法. 但为了能让 JPA 框架设置 {@code id} 字段, 需要为其加上
 * {@link Access @Access} 注解并将 {@code value} 参数设置为 {@link AccessType#FIELD},
 * 表示该字段无需通过 set 方法设置值
 * </p>
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    /**
     * 为子实体类增加 id 字段
     *
     * <p>
     * {@code @Access(AccessType.FIELD)} 注解表示可以直接通过字段设置值, 无需为该字段定义 set 方法
     * </p>
     *
     * <p>
     * {@link GeneratedValue @GeneratedValue} 注解表示 {@code id} 字段的生成方式, 一般情况下将
     * {@code strategy} 设置为 {@link GenerationType#IDENTITY} 表示由 DBMS 根据数据表定义自行生成
     * {@code id}
     * </p>
     */
    @Id
    @Access(AccessType.FIELD)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() { return id; }

    /**
     * 比较两个实体对象, 比较 {@code id} 字段即可
     */
    @Override
    public boolean equals(Object obj) {
        if (!getClass().isInstance(obj)) {
            return false;
        }

        var e = (BaseEntity) obj;
        return Objects.equals(id, e.id);
    }

    /**
     * 生成当前对象的哈希值, 生成 {@code id} 字段的哈希值即可
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
