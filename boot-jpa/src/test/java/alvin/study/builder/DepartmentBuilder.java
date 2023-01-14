package alvin.study.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import alvin.study.infra.entity.Department;
import alvin.study.infra.entity.Employee;

/**
 * 部门实体构建器类
 */
public class DepartmentBuilder extends Builder<Department> {
    private final static AtomicInteger SEQUENCE = new AtomicInteger();

    private String name = "Department" + SEQUENCE.incrementAndGet();
    private Department parent = null;
    private List<Department> subDepartments = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();

    /**
     * 设置部门名称
     */
    public DepartmentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置上级部门
     */
    public DepartmentBuilder withParent(Department parent) {
        this.parent = parent;
        return this;
    }

    /**
     * 设置下级部门列表
     */
    public DepartmentBuilder withChildren(List<Department> subDepartments) {
        this.subDepartments = subDepartments;
        return this;
    }

    /**
     * 设置部门员工
     */
    public DepartmentBuilder withEmployees(List<Employee> employees) {
        this.employees = employees;
        return this;
    }

    @Override
    public Department build() {
        var department = new Department();
        department.setName(name);
        department.setParent(parent);

        // 添加子部门
        for (var dep : subDepartments) {
            department.addSubDepartment(dep);
        }

        // 添加部门员工
        for (var employee : employees) {
            department.addEmployee(employee);
        }

        return fillOrgId(department);
    }
}
