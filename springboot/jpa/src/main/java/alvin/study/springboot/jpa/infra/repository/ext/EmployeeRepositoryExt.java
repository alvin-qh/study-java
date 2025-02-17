package alvin.study.springboot.jpa.infra.repository.ext;

import java.util.List;

import alvin.study.springboot.jpa.infra.entity.Department;
import alvin.study.springboot.jpa.infra.entity.Employee;
import alvin.study.springboot.jpa.infra.repository.EmployeeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;

/**
 * 定义一个接口来扩充 {@link EmployeeRepository
 * EmployeeRepository} 接口的方法
 *
 * <p>
 * 与 {@link EmployeeRepository EmployeeRepository}
 * 接口不同, 本接口不会根据方法名自动产生 JPQL 语句, 而需要实现定义的方法
 * </p>
 */
public interface EmployeeRepositoryExt {
    /**
     * 根据所给的 {@link Department} 对象查询相关的 {@link Employee} 对象
     *
     * <p>
     * 该接口方法由
     * {@link EmployeeRepositoryExtImpl#findEmployeesByDepartment(Department)} 方法实现,
     * 通过 {@link EntityManager#createQuery(String)} 方法和 JPQL 实现
     * </p>
     *
     * @param department {@link Department} 对象
     * @return 相关的 {@link Employee} 对象
     */
    List<Employee> findEmployeesByDepartment(Department department);

    /**
     * 根据所给的 {@link Department} 对象查询相关的 {@link Employee} 对象
     *
     * <p>
     * 该接口方法由
     * {@link EmployeeRepositoryExtImpl#findEmployeesByDepartment(Department)} 方法实现,
     * 通过 {@link EntityManager#createNativeQuery(String)} 方法和原生 SQL 实现
     * </p>
     *
     * @param department {@link Department} 对象
     * @return 相关的 {@link Employee} 对象
     */
    List<Employee> findEmployeesByDepartmentNative(Department department);

    /**
     * 实现 {@link EmployeeRepositoryExt} 接口
     */
    class EmployeeRepositoryExtImpl implements EmployeeRepositoryExt {
        // 注入 EntityManager 对象
        @PersistenceContext
        private EntityManager em;

        @Override
        public List<@NotNull Employee> findEmployeesByDepartment(Department department) {
            // 通过 JPQL 实现查询
            return em.createQuery("""
                select e
                from DepartmentEmployee de
                join de.employee e
                where de.department=:department
                """, Employee.class)
                    .setParameter("department", department)
                    .getResultList();
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Employee> findEmployeesByDepartmentNative(Department department) {
            // 通过原生 SQL 实现查询
            var sql = """
                select e.id, e.name, e.email, e.title, e.org_id, e.created_by, e.created_at, e.updated_by, e.updated_at
                from department_employee de
                join department d on de.department_id = d.id
                join employee e on de.employee_id = e.id
                where de.department_id = :department_id
                """;

            return em.createNativeQuery(sql, Employee.class)
                    .setParameter("department_id", department.getId())
                    .getResultList();
        }
    }
}
