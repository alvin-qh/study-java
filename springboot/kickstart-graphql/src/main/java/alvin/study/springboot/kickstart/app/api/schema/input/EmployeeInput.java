package alvin.study.springboot.kickstart.app.api.schema.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 变更雇员信息的输入类
 *
 * <p>
 * 对应的 schema 参考 {@code classpath:graphql/employee.graphqls} 文件内容
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInput {
    /**
     * 雇员姓名
     */
    private String name;

    /**
     * 雇员电邮
     */
    private String email;

    /**
     * 雇员职称
     */
    private String title;

    /**
     * 雇员信息
     */
    private Map<String, ?> info;

    /**
     * 所属部门 id
     */
    private List<Long> departmentIds;
}
