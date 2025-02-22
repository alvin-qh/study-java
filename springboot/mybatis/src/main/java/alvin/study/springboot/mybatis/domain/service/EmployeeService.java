package alvin.study.springboot.mybatis.domain.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.mybatis.domain.model.EmployeeDto;
import alvin.study.springboot.mybatis.infra.entity.DepartmentEmployee;
import alvin.study.springboot.mybatis.infra.entity.Employee;
import alvin.study.springboot.mybatis.infra.mapper.DepartmentEmployeeMapper;
import alvin.study.springboot.mybatis.infra.mapper.DepartmentMapper;
import alvin.study.springboot.mybatis.infra.mapper.EmployeeMapper;

/**
 * 员工服务类
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {
    // 注入员工 mapper 对象
    private final EmployeeMapper employeeMapper;

    // 注入部门 mapper 对象
    private final DepartmentMapper departmentMapper;

    // 部门员工对应关系 mapper 对象
    private final DepartmentEmployeeMapper departmentEmployeeMapper;

    // 注入模型转换对象
    private final ModelMapper modelMapper;

    /**
     * 根据员工名称获取员工信息
     *
     * <p>
     * 本例中演示了如何通过
     * {@link com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
     * LambdaQueryWrapper} 对象组织 SQL 查询的方法
     * </p>
     *
     * <p>
     * 通过 {@link Wrappers#lambdaQuery(Class)} 方法可以产生一个
     * {@link com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
     * LambdaQueryWrapper} 对象, 通过该对象可以动态组织较为复杂的 SQL 语句
     * </p>
     *
     * <p>
     * 组织好的 {@link com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
     * LambdaQueryWrapper} 对象可以通过
     * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper#selectList(com.baomidou.mybatisplus.core.conditions.Wrapper)
     * BaseMapper.selectList(Wrapper)},
     * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper#selectMaps(com.baomidou.mybatisplus.core.conditions.Wrapper)
     * BaseMapper.selectMaps(Wrapper)},
     * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper#selectOne(com.baomidou.mybatisplus.core.conditions.Wrapper)
     * BaseMapper.selectOne(Wrapper)},
     * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper#selectObjs(com.baomidou.mybatisplus.core.conditions.Wrapper)
     * BaseMapper.selectObjs(Wrapper)},
     * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper#selectPage(com.baomidou.mybatisplus.core.metadata.IPage, com.baomidou.mybatisplus.core.conditions.Wrapper)
     * BaseMapper.selectObjs(IPage, Wrapper)} 等方法进行查询, 这几种方法的区别在于返回的数据类型不同
     * </p>
     *
     * @param name 员工名称
     * @return 员工信息
     */
    @Transactional(readOnly = true)
    public Optional<EmployeeDto> findEmployeeByName(String name) {
        // 查询对应的员工实体
        var employee = employeeMapper.selectOne(
            // 包装一个 LambdaQueryWrapper 对象组织 SQL 语句
            Wrappers.lambdaQuery(Employee.class)
                    // 查询全部字段, 相当于 select *
                    // .select(i -> true)
                    // where 条件, 相当于 where name = :name
                    .eq(Employee::getName, name));

        if (employee == null) {
            // 未查询到员工实体的情况
            return Optional.empty();
        }

        // 查询员工部门对应关系
        var departmentIds = departmentEmployeeMapper.selectObjs(
            // 包装一个 LambdaQueryWrapper 对象组织 SQL 语句
            Wrappers.lambdaQuery(DepartmentEmployee.class)
                    // 查询 department_id 字段
                    .select(DepartmentEmployee::getDepartmentId)
                    // 指定 where 条件, 相当于 where employee_id = :employeeId
                    .eq(DepartmentEmployee::getEmployeeId, employee.getId()))
                // 将返回的 List<Object> 转为 List<Long>
                .stream()
                .map(Long.class::cast)
                .toList();

        if (!departmentIds.isEmpty()) {
            // 将查询到的 Department 信息放入 Employee 相关字段
            employee.setDepartments(departmentMapper.selectByIds(departmentIds));
        }

        // 将 Employee 实体对象转为 DTO 对象
        return Optional.of(modelMapper.map(employee, EmployeeDto.class));
    }
}
