package alvin.study.springboot.kickstart.app.api.schema.input;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * 变更部门信息的输入类
 *
 * <p>
 * 对应的 schema 参考 {@code classpath:graphql/department.graphqls} 文件内容
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentInput {
    /**
     * 部门名称
     */
    @NotBlank
    @Length(min = 3, max = 50)
    private String name;

    /**
     * 上级部门 Id
     */
    private Long parentId;
}
