package alvin.study.springboot.jpa.infra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import alvin.study.springboot.jpa.infra.entity.Department;
import jakarta.persistence.OrderBy;

/**
 * 部门实体 {@link Department} 类型的存储操作接口类
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
 * 如果需要更复杂的查询 (例如动态查询), 则当前接口需要继承
 * {@link JpaSpecificationExecutor} 接口, 该接口提供了一系列方法进行复杂查询, 并提供了对应的分页和排序支持
 * </p>
 */
public interface DepartmentRepository extends CrudRepository<Department, Long>, JpaSpecificationExecutor<Department> {
    /**
     * 查询所有的部门实体
     *
     * <p>
     * {@link OrderBy @OrderBy} 注解表示结果的排序规则
     * </p>
     *
     * @return 返回查询结果集合
     */
    @Override
    @OrderBy("id asc")
    List<Department> findAll();
}
