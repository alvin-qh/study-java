package alvin.study.springboot.kickstart.app.api.schema.payload;

import alvin.study.springboot.kickstart.app.api.schema.type.DepartmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新部门实体的返回结果
 *
 * <p>
 * 该类型为一个包装类型, 实际返回的结果为 {@link DepartmentType} 类型对象
 * </p>
 *
 * <p>
 * 对应的 schema 参考 {@code classpath:graphql/department.graphqls} 文件内容
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepartmentPayload {
    /**
     * 更新后的部门实体
     */
    private DepartmentType department;
}
