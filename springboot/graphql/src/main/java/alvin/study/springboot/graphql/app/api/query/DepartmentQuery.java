package alvin.study.springboot.graphql.app.api.query;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.google.common.base.Functions;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.api.query.common.AuditedBaseQuery;
import alvin.study.springboot.graphql.app.service.DepartmentService;
import alvin.study.springboot.graphql.app.service.EmployeeService;
import alvin.study.springboot.graphql.core.graphql.relay.Connection;
import alvin.study.springboot.graphql.core.graphql.relay.ConnectionBuilder;
import alvin.study.springboot.graphql.core.graphql.relay.Pagination;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.entity.Employee;

/**
 * 对应 {@link Department} 类型的 GraphQL 查询对象
 *
 * <p>
 * 对应 {@code classpath:graphql/department.graphqls} 中的定义
 * </p>
 */
@Controller
@RequiredArgsConstructor
public class DepartmentQuery extends AuditedBaseQuery<Department> {
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;
    private final Pagination pagination;

    /**
     * 根据部门实体 {@code ID} 查询 {@link Department} 类型部门实体对象
     *
     * @param id 部门实体 {@code ID}
     * @return {@link Department} 类型部门实体对象
     */
    @QueryMapping
    public Department department(@Argument long id) {
        return departmentService.findById(id);
    }

    /**
     * 根据部门实体 {@code ID} 查询 {@link Department} 类型部门实体对象
     *
     * <p>
     * 如果批量查询 {@link Department} 类型实体对象时, 可以通过 {@link DataLoader} 类型对象进行优化,
     * {@link DataLoader} 类型查询的原理为:
     * <p>
     * {@link DataLoader} 类型对象通过异步方式执行查询, 当批量查询 {@link Department} 类型实体对象时,
     * 每个查询都会返回一个 {@link CompletableFuture} 类型对象, 当批量查询结束后,
     * 会将所有查询进行合并, 包括: 将查询条件合并为一个集合; 将查询给到
     * {@link alvin.study.springboot.graphql.core.graphql.dataloader.DepartmentLoader DepartmentLoader}
     * 类型对象, 批量查询结果后, 返回一个 {@link Map} 类型对象, 再根据 {@code Key} 值将查询结果进行分发
     * </p>
     *
     * <p>
     * 除了通过 {@link org.dataloader.DataLoader DataLoader} 类型对象进行优化外, 还可以通过在
     * {@code Controller} 类中通过 {@link org.springframework.graphql.data.method.annotation.BatchMapping BatchMapping}
     * 注解将多次单次查询合并为一次批量查询, 从而减少查询次数, 参见
     * {@link alvin.study.springboot.graphql.app.api.query.DepartmentQuery#children(alvin.study.springboot.graphql.infra.entity.Department, String, int)
     * DepartmentQuery.children(Department, String, int)} 方法
     * </p>
     * </p>
     *
     * @param entity 部门实体
     * @param loader 部门实体数据加载器
     * @return {@link Department} 类型部门实体对象, 表示上一级部门
     */
    // @formatter:off
    // @SchemaMapping
    // public CompletableFuture<Department> parent(Department entity, DataLoader<Long, Department> loader) {
    //     if (entity.getParentId() == null) {
    //         return CompletableFuture.completedFuture(null);
    //     }
    //     return loader.load(entity.getParentId());
    // }
    // @formatter:on

    /**
     * 根据部门实体 {@code ID} 查询 {@link Department} 类型部门实体对象
     *
     * <p>
     * 该方法是通过 {@link org.dataloader.DataLoader DataLoader} 批量加载实体对象的另一种形式,
     * 即 GraphQL 框架会将所有单次查询的实体对象合成一个集合, 该方法中根据实体集合批量查询出所需的结果集合,
     * 再将结果进行分发, 即返回一个 {@link Map} 类型对象, 其中 {@code Key} 值为实体对象,
     * {@code Value} 值为通过 {@code Key} 实体对象查询所得的其它对象或集合
     * </p>
     *
     * @param entities {@link Department} 类型部门实体集合
     * @return {@link Department} 类型部门实体对象 {@link Map} 集合, 表示对应部门的上级部门,
     *         其中 {@code Key} 值表示部门实体对象, {@code Value} 值表示 {@code Key} 值部门的上级部门
     */
    @BatchMapping
    public Map<Department, Department> parent(List<Department> entities) {
        var parentIds = entities.stream()
                .map(Department::getParentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (parentIds.isEmpty()) {
            return Map.of();
        }

        var parents = departmentService.listByIds(parentIds)
                .stream().collect(Collectors.toMap(Department::getId, Functions.identity()));
        return entities.stream()
                .collect(Collectors.toMap(Functions.identity(), e -> parents.get(e.getParentId())));
    }

    /**
     * 根据部门实体 {@code ID} 查询 {@link Department} 类型部门实体对象
     *
     * @param entity {@link Department} 类型部门实体对象
     * @param after  分页参数, 表示上一页结尾的游标值
     * @param first  分页参数, 表示每页记录数
     * @return {@link Department} 类型部门实体对象集合, 表示当前部门下一级部门
     */
    @SchemaMapping
    public Connection<Department> children(Department entity, @Argument String after, @Argument int first) {
        var page = pagination.<Department>newBuilder().withFirst(first).withAfter(after).build();
        page = departmentService.listChildren(page, entity.getId());
        return ConnectionBuilder.build(page);
    }

    @SchemaMapping
    public Connection<Employee> employees(Department entity, @Argument String after, @Argument int first) {
        var page = pagination.<Employee>newBuilder()
                .withFirst(first)
                .withAfter(after)
                .build();
        page = employeeService.listByDepartmentId(page, entity.getId());
        return ConnectionBuilder.build(page);
    }
}
