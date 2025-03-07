package alvin.study.springboot.graphql.app.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.core.exception.InputException;
import alvin.study.springboot.graphql.core.exception.NotFoundException;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.entity.DepartmentEmployee;
import alvin.study.springboot.graphql.infra.mapper.DepartmentEmployeeMapper;
import alvin.study.springboot.graphql.infra.mapper.DepartmentMapper;

/**
 * 部门服务类, 用于 {@link Department} 类型数据操作
 */
@Component
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentMapper departmentMapper;
    private final DepartmentEmployeeMapper departmentEmployeeMapper;

    /**
     * 根据部门实体 {@code ID} 值查询 {@link Department} 类型部门实体对象
     *
     * @param id 部门实体 {@code ID}
     * @return {@link Department} 类型部门实体对象的 {@link Optional} 包装对象
     */
    @Transactional(readOnly = true)
    public Department findById(long id) {
        return Optional.ofNullable(
            departmentMapper.selectById(id))
                .orElseThrow(() -> new NotFoundException(String.format("Department not exist by id = %d", id)));
    }

    /**
     * 根据部门 {@code ID} 集合查询 {@link Department} 类型部门实体集合
     *
     * @param departmentIds 部门 {@code ID} 集合
     * @return {@link Department} 类型部门实体集合
     */
    @Transactional(readOnly = true)
    public List<Department> listByIds(Collection<Long> departmentIds) {
        return departmentMapper.selectByIds(departmentIds);
    }

    /**
     * 创建 {@link Department} 类型部门实体对象
     *
     * @param department {@link Department} 类型部门实体对象
     */
    @Transactional
    public void create(Department department) {
        departmentMapper.insert(department);
    }

    /**
     * 更新 {@link Department} 类型部门实体对象
     *
     * @param id         部门实体 {@code ID}
     * @param department {@link Department} 部门实体对象的 {@link Optional} 包装对象
     * @return {@link Department} 类型部门实体对象的 {@link Optional} 包装对象
     */
    @Transactional
    public void update(Department department) {
        if (department.getParentId() == department.getId()) {
            throw new InputException("Cannot set parent department as self");
        }
        if (departmentMapper.updateById(department) == 0) {
            throw new NotFoundException(String.format("Department not exist by id = %d", department.getId()));
        }
    }

    /**
     * 根据部门 {@code ID} 查询相关的 {@link Department} 类型子部门实体集合
     *
     * @param parentId 父一级部门 {@code ID}
     * @param page     {@link IPage} 类型分页对象
     * @return {@link IPage} 类型分页对象, 包含一页数量的 {@link Department} 类型子部门实体集合
     */
    @Transactional(readOnly = true)
    public IPage<Department> listChildren(IPage<Department> page, long parentId) {
        var query = Wrappers.lambdaQuery(Department.class)
                .eq(Department::getParentId, parentId);
        return departmentMapper.selectPage(page, query);
    }

    /**
     * 删除 {@link Department} 类型部门实体
     *
     * @param id 部门实体 {@code ID} 值
     * @return {@code true} 表示删除成功, {@code false} 表示删除失败
     */
    @Transactional
    public boolean delete(long id) {
        return departmentMapper.deleteById(id) > 0;
    }

    /**
     * 根据雇员 {@code ID} 查询其 {@link Department} 类型所属的部门实体对象
     *
     * @param employeeId 雇员 {@code ID}
     * @return 雇员所属部门的 {@link Department} 类型实体集合
     */
    @Transactional(readOnly = true)
    public List<DepartmentEmployee> listByEmployeeIds(Collection<Long> employeeIds) {
        return departmentEmployeeMapper.selectByEmployeeIds(employeeIds);
    }
}
