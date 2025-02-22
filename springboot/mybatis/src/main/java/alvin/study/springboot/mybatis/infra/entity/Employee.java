package alvin.study.springboot.mybatis.infra.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import lombok.Data;
import lombok.EqualsAndHashCode;

import alvin.study.springboot.mybatis.infra.entity.common.AuditedEntity;

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
 *
 * <p>
 * 对于 {@link EmployeeInfo} 类型字段, 其 {@link TableField @TableField} 注解的
 * {@code typeHandler} 属性表示该字段, 即一个 Java 类型字段需要转换为 SQL 类型字段时,
 * 需要通过"字段类型处理器"类型来完成, 这里用到了 {@link JacksonTypeHandler} 类型处理器, 表示将一个 Java 类型转为
 * JSON 字符串对应到 {@code VARCHAR} 类型数据表字段 {@code info} 中
 * </p>
 *
 * <p>
 * 字段处理器的使用场景有两个:
 * <ul>
 * <li>
 * 一类是通过 Mybatis 提供的 {@link com.baomidou.mybatisplus.core.mapper.Mapper Mapper}
 * 接口子接口通过 Java 代码处理实体对象时, 需要在实体类型上注解 {@link TableName @TableName} 时设置属性
 * {@code autoResultMap = true}
 * </li>
 * <li>
 * 一类是通过 Mybatis 提供的 {@link com.baomidou.mybatisplus.core.mapper.Mapper Mapper}
 * 接口子接口通过 XML 描述实体映射关系时, 在字段映射标签中进行定义 {@code typeHandler} 属性, 即:
 *
 * <pre>
 * <![CDATA[
 * <resultMap id="employeeResultMap" type="alvin.study.infra.entity.Employee">
 *   <result
 *      property="info"
 *      column="info"
 *      typeHandler="com...JacksonTypeHandler"
 *   />
 * </resultMap>
 * ]]>
 * </pre>
 *
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 也可以通过自定义字段类型处理器处理特殊字段
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
     * 职员明细信息
     *
     * <p>
     * {@link TableField @TableField} 注解的 {@code typeHandler} 属性表示该字段通过
     * {@link JacksonTypeHandler} 这个字段类型处理器进行转换后存储到数据表; 从数据表中读取数据后也会经过逆向转换为
     * {@link EmployeeInfo} 类型实例后作为实体对象字段
     * </p>
     */
    @TableField(value = "info", typeHandler = JacksonTypeHandler.class)
    private EmployeeInfo info = new EmployeeInfo();

    /**
     * 软删除标识字段
     */
    @TableLogic(value = "0", delval = "#{id}")
    private long deleted = 0L;

    /**
     * 员工所属部门
     *
     * <p>
     * 该类成员字段并不对应到数据表字段
     * </p>
     */
    @TableField(exist = false)
    private List<Department> departments;
}
