package alvin.study.springboot.kickstart.app.service;

import alvin.study.springboot.kickstart.core.exception.InputException;
import alvin.study.springboot.kickstart.infra.entity.Department;
import alvin.study.springboot.kickstart.infra.entity.DepartmentEmployee;
import alvin.study.springboot.kickstart.infra.entity.Employee;
import alvin.study.springboot.kickstart.infra.mapper.DepartmentEmployeeMapper;
import alvin.study.springboot.kickstart.infra.mapper.DepartmentMapper;
import alvin.study.springboot.kickstart.infra.mapper.EmployeeMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 部门服务类
 */
@Component
@RequiredArgsConstructor
public class EmployeeService {
    /**
     * 注入雇员服务类对象
     */
    private final EmployeeMapper employeeMapper;

    /**
     * 注入部门服务类对象
     */
    private final DepartmentMapper departmentMapper;

    /**
     * 注入部门员工关系服务类对象
     */
    private final DepartmentEmployeeMapper departmentEmployeeMapper;

    /**
     * 根据组织 id 名查询部门信息
     *
     * @param id 部门 id
     * @return 部门实体的 {@link Optional} 包装对象
     */
    @Transactional(readOnly = true)
    public Optional<Employee> findById(long id) {
        return Optional.ofNullable(employeeMapper.selectById(id));
    }

    /**
     * 创建一个 {@link Employee} 实体对象
     *
     * @param employee      {@link Employee} 对象
     * @param departmentIds 所属部门 id 集合
     */
    @Transactional
    public void create(Employee employee, Collection<Long> departmentIds) {
        employeeMapper.insert(employee);

        if (departmentIds != null && !departmentIds.isEmpty()) {
            // 建立关联关系
            bindWithDepartments(employee, new HashSet<>(departmentIds));
        }
    }

    /**
     * 更新一个 {@link Employee} 实体对象
     *
     * @param id            实体 {@code id}
     * @param employee      {@link Employee} 对象
     * @param departmentIds 所属部门 id 集合
     */
    @Transactional
    public Optional<Employee> update(long id, Employee employee, Collection<Long> departmentIds) {
        var originalEmployee = employeeMapper.selectById(id);
        if (originalEmployee == null) {
            return Optional.empty();
        }

        originalEmployee.setName(employee.getName());
        originalEmployee.setEmail(employee.getEmail());
        originalEmployee.setTitle(employee.getTitle());
        originalEmployee.setInfo(employee.getInfo());

        if (employeeMapper.updateById(originalEmployee) == 0) {
            return Optional.empty();
        }

        if (departmentIds != null && !departmentIds.isEmpty()) {
            // 删除之前的关联关系
            departmentEmployeeMapper.delete(
                Wrappers.lambdaQuery(DepartmentEmployee.class)
                        .eq(DepartmentEmployee::getEmployeeId, employee.getId()));

            // 建立新的关联关系
            bindWithDepartments(originalEmployee, new HashSet<>(departmentIds));
        }

        return Optional.of(originalEmployee);
    }

    /**
     * 将雇员和指定的部门进行绑定
     *
     * @param employee      员工对象
     * @param departmentIds 所属部门 id 集合
     */
    private void bindWithDepartments(Employee employee, Set<Long> departmentIds) {
        var departments = departmentMapper.selectByIds(departmentIds);
        if (departmentIds.size() != departments.size()) {
            departmentIds.removeAll(departments.stream().map(Department::getId).collect(Collectors.toSet()));
            throw new InputException(String.format("Department with id %s ", departmentIds));
        }

        departments.forEach(d -> {
            var de = new DepartmentEmployee();
            de.setDepartmentId(d.getId());
            de.setEmployeeId(employee.getId());

            departmentEmployeeMapper.insert(de);
        });
    }

    /**
     * 删除一个 {@link Employee} 实体
     *
     * @param id 实体 id
     */
    @Transactional
    public boolean delete(long id) {
        // 删除之前的关联关系
        departmentEmployeeMapper.delete(
            Wrappers.lambdaQuery(DepartmentEmployee.class)
                    .eq(DepartmentEmployee::getEmployeeId, id));

        return employeeMapper.deleteById(id) > 0;
    }

    /**
     * 根据部门 id 查询部门下的员工
     *
     * @param page         分页对象
     * @param departmentId 部门 id
     * @return 部门下员工的分页集合
     */
    @Transactional(readOnly = true)
    public IPage<Employee> listByDepartmentId(IPage<Employee> page, long departmentId) {
        return employeeMapper.selectByDepartmentId(page, departmentId);
    }
}
