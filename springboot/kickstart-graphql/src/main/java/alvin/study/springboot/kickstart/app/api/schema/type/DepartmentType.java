package alvin.study.springboot.kickstart.app.api.schema.type;

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
 * 参考 {@code classpath:graphql/department.graphqls} 中定义查询的 schema
 * </p>
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentType extends AuditedType {
    // 部门名称
    private String name;

    // 上级部门 id
    private Long parentId;
}
