package alvin.study.springboot.graphql.builder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.springboot.graphql.infra.entity.DepartmentEmployee;
import alvin.study.springboot.graphql.infra.mapper.DepartmentEmployeeMapper;

/**
 * 构建部门职员关系实体
 */
public class DepartmentEmployeeBuilder extends Builder<DepartmentEmployee> {
    @Autowired
    private DepartmentEmployeeMapper mapper;

    // 部门 id
    private Long departmentId;

    // 职员 id
    private Long employeeId;

    /**
     * 设置部门 id
     */
    public DepartmentEmployeeBuilder withDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
        return this;
    }

    /**
     * 设置职员 id
     */
    public DepartmentEmployeeBuilder withEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    @Override
    public DepartmentEmployee build() {
        assertNotNull(departmentId);
        assertNotNull(employeeId);

        var de = new DepartmentEmployee();
        de.setDepartmentId(departmentId);
        de.setEmployeeId(employeeId);
        return complete(de);
    }

    @Override
    public DepartmentEmployee create() {
        var de = build();
        mapper.insert(de);
        return de;
    }
}
