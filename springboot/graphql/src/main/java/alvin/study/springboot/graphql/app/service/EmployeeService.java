package alvin.study.springboot.graphql.app.service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.context.ContextKey;
import alvin.study.springboot.graphql.core.context.ContextHolder;
import alvin.study.springboot.graphql.core.exception.InputException;
import alvin.study.springboot.graphql.core.exception.NotFoundException;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.entity.DepartmentEmployee;
import alvin.study.springboot.graphql.infra.entity.Employee;
import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.mapper.DepartmentEmployeeMapper;
import alvin.study.springboot.graphql.infra.mapper.DepartmentMapper;
import alvin.study.springboot.graphql.infra.mapper.EmployeeMapper;

/**
 * 雇员服务类, 用于 {@link Employee} 类型数据操作
 */
@Component
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeMapper employeeMapper;
    private final DepartmentMapper departmentMapper;
    private final DepartmentEmployeeMapper departmentEmployeeMapper;

    /**
     * 根据雇员 {@code ID} 查询 {@link Employee} 类型雇员实体
     *
     * @param id 部门 {@code ID}
     * @return {@link Employee} 类型雇员实体的 {@link Optional} 类型包装对象
     */
    @Transactional(readOnly = true)
    public Employee findById(long id) {
        return Optional.ofNullable(
            employeeMapper.selectById(id))
                .orElseThrow(() -> new NotFoundException(String.format("Employee not exist by id = %d", id)));
    }

    /**
     * 创建 {@link Employee} 雇员实体对象
     *
     * @param employee      {@link Employee} 类型雇员实体对象
     * @param departmentIds 所属部门 {@code ID} 集合
     */
    @Transactional
    public void create(Employee employee, Collection<Long> departmentIds) {
        if (employeeMapper.insert(employee) == 0) {
            return;
        }

        if (departmentIds != null && !departmentIds.isEmpty()) {
            // 建立关联关系
            bindWithDepartments(employee, Set.copyOf(departmentIds));
        }
    }

    /**
     * 更新 {@link Employee} 雇员实体对象
     *
     * @param id            实体 {@code ID}
     * @param employee      {@link Employee} 类型雇员实体对象
     * @param departmentIds 所属部门 {@code ID} 集合
     * @return {@link Employee} 类型雇员实体的 {@link Optional} 类型包装对象
     */
    @Transactional
    public Employee update(Employee employee, Collection<Long> departmentIds) {
        var existEmployee = employeeMapper.selectById(employee.getId());
        existEmployee.setName(employee.getName());
        existEmployee.setEmail(employee.getEmail());
        existEmployee.setTitle(employee.getTitle());
        existEmployee.setInfo(employee.getInfo());

        if (employeeMapper.updateById(existEmployee) == 0) {
            throw new NotFoundException(String.format("Employee not exist by id = %d", employee.getId()));
        }

        // 删除之前的关联关系
        departmentEmployeeMapper.delete(
            Wrappers.lambdaQuery(DepartmentEmployee.class)
                    .eq(DepartmentEmployee::getEmployeeId, employee.getId()));

        if (departmentIds != null && !departmentIds.isEmpty()) {
            // 建立新的关联关系
            bindWithDepartments(employee, Set.copyOf(departmentIds));
        }
        return employeeMapper.selectById(employee.getId());
    }

    /**
     * 将雇员和指定的部门进行绑定
     *
     * <p>
     * 该方法用于将指定的 {@link Employee} 类型员工实体对象加入到指定的部门中
     * </p>
     *
     * @param employee      {@link Employee} 类型员工实体对象
     * @param departmentIds 所属部门 {@code ID} 集合
     */
    private void bindWithDepartments(Employee employee, Set<Long> departmentIds) {
        var departments = departmentMapper.selectByIds(departmentIds);
        if (departmentIds.size() != departments.size()) {
            departmentIds.removeAll(departments.stream().map(Department::getId).collect(Collectors.toSet()));
            throw new InputException(String.format("Department id in %s not a valid id", departmentIds));
        }

        departments.forEach(d -> {
            var de = new DepartmentEmployee();
            de.setDepartmentId(d.getId());
            de.setEmployeeId(employee.getId());
            departmentEmployeeMapper.insert(de);
        });
    }

    /**
     * 删除一个 {@link Employee} 类型实体对象
     *
     * @param id 雇员实体的 {@code ID} 值
     * @return {@code true} 表示删除成功, {@code false} 表示删除失败
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
     * 根据部门 {@code ID} 查询部门下的所有 {@link Employee} 类型员工实体对象集合
     *
     * @param page         {@link IPage} 类型分页对象
     * @param departmentId {@link Department} 类型部门实体的 {@code ID} 值
     * @return {@link IPage} 类型分页对象, 包含一页数量的 {@link Employee} 类型的员工实体对象集合
     */
    @Transactional(readOnly = true)
    public IPage<Employee> listByDepartmentId(IPage<Employee> page, long departmentId) {
        var ctx = ContextHolder.getValue();
        return employeeMapper.selectByDepartmentId(page, ctx.<Org>get(ContextKey.KEY_ORG).getId(), departmentId);
    }
}
