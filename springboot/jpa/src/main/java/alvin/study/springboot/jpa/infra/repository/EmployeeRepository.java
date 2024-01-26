package alvin.study.springboot.jpa.infra.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import alvin.study.springboot.jpa.infra.entity.Employee;
import alvin.study.springboot.jpa.infra.repository.ext.EmployeeRepositoryExt;
import jakarta.persistence.OrderBy;

/**
 * 职员实体 {@link Employee} 类型的存储操作接口类
 *
 * <p>
 * JPA 可以根据接口方法名自动产生所需的 SQL, 需要令当前接口继承 {@link CrudRepository} 接口, 表示当前接口用于增删查改操作
 * </p>
 *
 * <p>
 * {@link CrudRepository} 接口的泛型参数表示要操作的实体类型和实体类型的主键类型
 * </p>
 *
 * <p>
 * {@link EmployeeRepositoryExt} 接口为当前接口补充了若干查询方法, 这些方法通过
 * {@link EmployeeRepositoryExt.EmployeeRepositoryExtImpl} 类型实现, JPA
 * 会根据接口定义的方法自动找寻实现类并执行方法, 这种方式可以扩充 JPA 根据 Repository 接口语义自动产生 JPQL 查询的不足,
 * 引入直接书写的 JPQL 甚至原生 SQL, 通过 {@link jakarta.persistence.EntityManager EntityManager} 来执行
 * </p>
 */
public interface EmployeeRepository extends CrudRepository<Employee, Long>, EmployeeRepositoryExt {
    /**
     * 查询所有的职员实体
     *
     * <p>
     * {@link OrderBy @OrderBy} 注解表示结果的排序规则
     * </p>
     *
     * @return {@link Employee} 实体集合
     */
    @Override
    @OrderBy("id asc")
    List<Employee> findAll();
}
