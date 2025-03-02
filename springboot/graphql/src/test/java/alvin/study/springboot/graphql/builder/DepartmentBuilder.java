package alvin.study.springboot.graphql.builder;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.mapper.DepartmentMapper;

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
    private Long parentId = null;

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
    public DepartmentBuilder withParent(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    @Override
    public Department build() {
        var department = new Department();
        department.setName(name);
        department.setParentId(parentId);
        return complete(department);
    }

    @Override
    public Department create() {
        var department = build();
        mapper.insert(department);
        return department;
    }
}
