package alvin.study.springboot.kickstart.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import alvin.study.springboot.kickstart.infra.entity.Department;
import alvin.study.springboot.kickstart.infra.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;

/**
 * 部门服务类
 */
@Component
@RequiredArgsConstructor
public class DepartmentService {
    /**
     * 注入 {@link DepartmentMapper} 类型
     */
    private final DepartmentMapper departmentMapper;

    /**
     * 根据组织 id 名查询部门信息
     *
     * @param id 部门 id
     * @return 部门实体的 {@link Optional} 包装对象
     */
    @Transactional(readOnly = true)
    public Optional<Department> findById(long id) {
        return Optional.ofNullable(departmentMapper.selectById(id));
    }

    /**
     * 创建一个 {@link Department} 实体对象
     *
     * @param department {@link Department} 对象
     */
    @Transactional
    public void create(Department department) {
        departmentMapper.insert(department);
    }

    /**
     * 更新一个 {@link Department} 实体对象
     *
     * @param id         部门 id
     * @param department {@link Department} 对象
     */
    @Transactional
    public Optional<Department> update(long id, Department department) {
        var originalDepartment = departmentMapper.selectById(id);
        if (originalDepartment == null) {
            return Optional.empty();
        }

        originalDepartment.setName(department.getName());
        originalDepartment.setParentId(department.getParentId());

        if (departmentMapper.updateById(originalDepartment) > 0) {
            return Optional.of(originalDepartment);
        }

        return Optional.empty();
    }

    /**
     * 根据部门 ID 查询相关子部门实体集合
     *
     * @param parentId 父一级部门 ID
     * @param page     Mybatis-Plus 分页对象
     * @return 子部门集合
     */
    @Transactional(readOnly = true)
    public IPage<Department> listChildren(long parentId, IPage<Department> page) {
        var query = Wrappers.lambdaQuery(Department.class).eq(Department::getParentId, parentId);
        return departmentMapper.selectPage(page, query);
    }

    /**
     * 删除一个部门实体
     *
     * @param id 部门 id
     * @return 是否删除
     */
    @Transactional
    public boolean delete(long id) {
        return departmentMapper.deleteById(id) > 0;
    }

    /**
     * 根据雇员 id 查询其所属的部门
     *
     * @param employeeId 雇员 id
     * @return 雇员所属部门
     */
    @Transactional(readOnly = true)
    public List<Department> listByEmployeeId(long employeeId) {
        return departmentMapper.selectByEmployeeId(employeeId);
    }
}
