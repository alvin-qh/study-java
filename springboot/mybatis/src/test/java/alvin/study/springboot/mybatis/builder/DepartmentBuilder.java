package alvin.study.springboot.mybatis.builder;

import alvin.study.springboot.mybatis.infra.entity.Department;
import alvin.study.springboot.mybatis.infra.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 部门实体构建器类
 */
public class DepartmentBuilder extends Builder<Department> {
    private final static AtomicInteger SEQUENCE = new AtomicInteger();

    @Autowired
    private DepartmentMapper mapper;

    // 部门名称
    private String name = "Department" + SEQUENCE.incrementAndGet();

    // 部门的上级部门
    private Department parent = null;

    /**
     * 设置部门名称
     *
     * @param name 部门名称
     * @return 当前对象
     */
    public DepartmentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置上级部门
     *
     * @param parent 表示上级部门的 {@link Department} 对象
     * @return 当前对象
     */
    public DepartmentBuilder withParent(Department parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public Department build() {
        var department = new Department();
        department.setName(name);
        department.setParent(parent);
        return fillOrgId(department);
    }

    @Override
    public Department create() {
        var department = build();
        mapper.insert(department);
        return department;
    }
}
