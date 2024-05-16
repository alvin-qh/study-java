package alvin.study.springboot.mybatis.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 员工 DTO 类型
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto implements Serializable {
    /**
     * 员工 id
     */
    private Long id;

    /**
     * 员工姓名
     */
    private String name;

    /**
     * 员工邮件
     */
    private String email;

    /**
     * 员工职称
     */
    private String title;

    /**
     * 员工详细信息
     */
    private EmployeeInfoDto info;

    /**
     * 员工所属部门
     */
    private List<DepartmentDto> departments;
}
