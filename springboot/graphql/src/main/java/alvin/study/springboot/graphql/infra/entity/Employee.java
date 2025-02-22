package alvin.study.springboot.graphql.infra.entity;

import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import lombok.Data;
import lombok.EqualsAndHashCode;

import alvin.study.springboot.graphql.infra.entity.common.AuditedEntity;

/**
 * 雇员实体, 对应 {@code employee} 表
 *
 * <p>
 * {@link TableField @TableField} 注解表示数据表字段的定义, 包括:
 * <ul>
 * <li>
 * {@code value} 属性表示对应的自定名称
 * </li>
 * <li>
 * {@code exist = false} 不实际对应数据表的字段
 * </li>
 * </ul>
 * </p>
 */
@Data
@TableName(value = "employee", autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class Employee extends AuditedEntity {
    /**
     * 职员名称
     */
    @TableField("name")
    private String name;

    /**
     * 职员电子邮件地址
     */
    @TableField("email")
    private String email;

    /**
     * 职员职称
     */
    @TableField("title")
    private String title;

    /**
     * 软删除标识字段
     */
    @TableLogic(value = "0", delval = "#{id}")
    private long deleted = 0L;

    /**
     * 职员明细信息
     *
     * <p>
     * {@link TableField @TableField} 注解的 {@code typeHandler} 属性表示该字段通过
     * {@link JacksonTypeHandler} 这个字段类型处理器进行转换后存储到数据表; 从数据表中读取数据后也会经过逆向转换为
     * {@link Map} 类型实例后作为实体对象字段
     * </p>
     */
    @TableField(value = "info", typeHandler = JacksonTypeHandler.class)
    private Map<String, ?> info = new HashMap<>();
}
