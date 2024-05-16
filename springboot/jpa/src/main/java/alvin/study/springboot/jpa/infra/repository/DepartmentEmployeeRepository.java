package alvin.study.springboot.jpa.infra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import alvin.study.springboot.jpa.infra.entity.Department;
import alvin.study.springboot.jpa.infra.entity.DepartmentEmployee;
import alvin.study.springboot.jpa.infra.entity.Employee;
import jakarta.persistence.OrderBy;

/**
 * 部门员工关系实体 {@link DepartmentEmployee} 的存储操作接口类
 *
 * <p>
 * JPA 可以根据接口方法名自动产生所需的 SQL, 需要令当前接口继承 {@link CrudRepository} 接口, 表示当前接口用于增删查改操作
 * </p>
 *
 * <p>
 * {@link CrudRepository} 接口的泛型参数表示要操作的实体类型和实体类型的主键类型
 * </p>
 */
public interface DepartmentEmployeeRepository extends CrudRepository<DepartmentEmployee, Long> {
    /**
     * 查询所有的部门员工关系
     *
     * <p>
     * {@link OrderBy @OrderBy} 注解表示结果的排序规则
     * </p>
     */
    @Override
    @OrderBy("id asc")
    List<DepartmentEmployee> findAll();

    /**
     * 查询指定部门下的所有雇员实体
     *
     * @param department 部门实体对象
     * @return 该部门下的所有职员实体对象集合
     */
    @Query("select e from DepartmentEmployee de join de.employee e where de.department=?1")
    @OrderBy("id asc")
    List<Employee> findEmployeesByDepartment(Department department);
}
