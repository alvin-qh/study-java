package alvin.study.springboot.kickstart.app.api.schema.type;

import java.util.Map;

import alvin.study.springboot.kickstart.app.api.schema.type.common.AuditedType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 部门类型
 *
 * <p>
 * {@link #info} 字段为 {@link Map} 类型, 对应 Schema 中的 {@code JSON} 类型字段, 表示会将
 * {@link Map} 类型转为 JSON 结构后传递到客户端
 * </p>
 *
 * <p>
 * 参考 {@code classpath:graphql/employee.graphqls} 中定义查询的 schema
 * </p>
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeType extends AuditedType {
    // 雇员名称
    private String name;

    // 雇员电邮
    private String email;

    // 雇员职称
    private String title;

    // 雇员信息
    private Map<String, ?> info;
}
