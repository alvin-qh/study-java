package alvin.study.springboot.kickstart.app.api.schema.type;

import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.modelmapper.ModelMapper;

import alvin.study.springboot.kickstart.app.api.schema.loader.DepartmentLoaderProvider;
import alvin.study.springboot.kickstart.app.api.schema.type.common.AuditedResolver;
import alvin.study.springboot.kickstart.app.api.schema.type.common.TenantedResolver;
import alvin.study.springboot.kickstart.app.service.DepartmentService;
import alvin.study.springboot.kickstart.app.service.EmployeeService;
import alvin.study.springboot.kickstart.core.graphql.annotation.Resolver;
import alvin.study.springboot.kickstart.core.graphql.relay.Connection;
import alvin.study.springboot.kickstart.core.graphql.relay.ConnectionBuilder;
import alvin.study.springboot.kickstart.core.graphql.relay.Pagination;
import alvin.study.springboot.kickstart.infra.entity.Department;
import alvin.study.springboot.kickstart.infra.entity.Employee;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;

/**
 * 对 {@link DepartmentType} 类型补充 {@code parent}, {@code children},
 * {@code createdByUser}, {@code updatedByUser} 以及 {@code org} 字段
 *
 * <p>
 * 参考 {@code classpath:graphql/department.graphqls} 中的 schema 定义
 * </p>
 */
@Resolver
@RequiredArgsConstructor
public class DepartmentTypeResolver implements AuditedResolver<DepartmentType>, TenantedResolver<DepartmentType> {
    // 注入部门服务类对象
    private final DepartmentService departmentService;

    // 注入雇员服务类对象
    private final EmployeeService employeeService;

    // 注入分页类
    private final Pagination pagination;

    /**
     * 补充 Department schema 的 {@code parent} 查询字段
     *
     * @param instance 继承 {@link DepartmentType} 类型的对象
     * @param env      {@link DataFetchingEnvironment} 对象, 用于获取指定的
     *                 {@link DataLoader} 对象
     * @return 一个异步函数, 将通过每个 id 获取对象的处理延时执行, 转化为批量处理
     * @see DepartmentLoaderProvider
     */
    public CompletableFuture<@Nullable DepartmentType> getParent(DepartmentType instance, DataFetchingEnvironment env) {
        if (instance.getParentId() == null) {
            return CompletableFuture.completedFuture(null);
        }

        var mapper = (ModelMapper) env.getGraphQlContext().get(ModelMapper.class);

        DataLoader<Long, Department> loader = env.getDataLoaderRegistry().getDataLoader(DepartmentLoaderProvider.NAME);
        var future = loader.load(instance.getParentId());
        return future.thenApply(d -> mapper.map(d, DepartmentType.class));
    }

    /**
     * 补充 Department schema 的 {@code children} 查询字段
     *
     * @param instance 继承 {@link DepartmentType} 类型的对象
     * @param first    起始索引位置
     * @param after    查询数量
     * @param env      {@link DataFetchingEnvironment} 对象, 用于获取指定的
     *                 {@link ModelMapper} 对象
     * @return 一个异步函数, 将通过每个 id 获取对象的处理延时执行, 转化为批量处理
     * @see Pagination
     * @see alvin.study.core.graphql.relay.Page
     * @see ConnectionBuilder
     */
    public Connection<@NotNull DepartmentType> getChildren(
        DepartmentType instance, String first, Integer after, DataFetchingEnvironment env) {
        var mapper = (ModelMapper) env.getGraphQlContext().get(ModelMapper.class);

        // 构建分页对象
        var page = pagination.<Department>newBuilder().withQueryParams(env.getArguments()).build();
        // 查询子部门
        page = departmentService.listChildren(instance.getId(), page);

        // 结果转为 Connection 对象返回
        return ConnectionBuilder.build(page).mapTo(d -> mapper.map(d, DepartmentType.class));
    }

    /**
     * 补充 Department schema 的 {@code employees} 查询字段
     *
     * @param instance 继承 {@link DepartmentType} 类型的对象
     * @param first    起始索引位置
     * @param after    查询数量
     * @param env      {@link DataFetchingEnvironment} 对象, 用于获取指定的
     *                 {@link ModelMapper} 对象
     * @return 一个异步函数, 将通过每个 id 获取对象的处理延时执行, 转化为批量处理
     * @see Pagination
     * @see alvin.study.core.graphql.relay.Page
     * @see ConnectionBuilder
     */
    public Connection<@NotNull EmployeeType> getEmployees(
        DepartmentType instance, String first, Integer after, DataFetchingEnvironment env) {
        var mapper = (ModelMapper) env.getGraphQlContext().get(ModelMapper.class);

        // 构建分页对象
        var page = pagination.<Employee>newBuilder().withQueryParams(env.getArguments()).build();
        // 查询部门下的雇员
        page = employeeService.listByDepartmentId(page, instance.getId());
        // 结果转为 Connection 对象返回
        return ConnectionBuilder.build(page).mapTo(e -> mapper.map(e, EmployeeType.class));
    }
}
