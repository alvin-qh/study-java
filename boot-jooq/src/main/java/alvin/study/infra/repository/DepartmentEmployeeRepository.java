package alvin.study.infra.repository;

import static alvin.study.infra.model.public_.tables.DepartmentEmployee.DEPARTMENT_EMPLOYEE;

import org.springframework.stereotype.Repository;

import alvin.study.infra.model.public_.tables.records.DepartmentEmployeeRecord;
import alvin.study.infra.repository.common.BaseRepository;

/**
 * 操作 {@link DEPARTMENT_EMPLOYEE} 表实体的 Repository 类型
 */
@Repository
public class DepartmentEmployeeRepository extends BaseRepository<DepartmentEmployeeRecord> {
    /**
     * 构造器, 指定当前实体对应的 {@link org.jooq.Table Table} 类型
     */
    public DepartmentEmployeeRepository() {
        super(DEPARTMENT_EMPLOYEE);
    }
}
