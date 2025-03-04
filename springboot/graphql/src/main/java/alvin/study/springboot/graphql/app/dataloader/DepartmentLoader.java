package alvin.study.springboot.graphql.app.dataloader;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.dataloader.BatchLoaderEnvironment;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.google.common.base.Functions;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.context.ContextKey;
import alvin.study.springboot.graphql.core.context.ContextHolder;
import alvin.study.springboot.graphql.infra.entity.Department;
import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.mapper.DepartmentMapper;
import reactor.core.publisher.Mono;

/**
 * 通过 {@code id} 集合获取 {@link Department} 实例集合的数据加载器类型
 *
 * <p>
 * 如果批量查询 {@link Department} 类型实体对象时, 可以通过 {@link org.dataloader.DataLoader DataLoader} 类型对象进行优化,
 * {@link org.dataloader.DataLoader DataLoader} 类型查询的原理为:
 *
 * <p>
 * {@link org.dataloader.DataLoader DataLoader} 类型对象通过异步方式执行查询, 当批量查询 {@link Department} 类型实体对象时,
 * 每个查询都会返回一个 {@link java.util.concurrent.CompletableFuture CompletableFuture} 类型对象, 当批量查询结束后,
 * 会将所有查询进行合并, 包括: 将查询条件合并为一个集合; 将查询给到 {@link DepartmentLoader} 类型对象, 批量查询结果后,
 * 返回一个 {@link java.util.Map Map} 类型对象, 再根据 {@code Key} 值将查询结果进行分发
 * </p>
 *
 * <p>
 * 除了通过 {@link org.dataloader.DataLoader DataLoader} 类型对象进行优化外, 还可以通过在
 * {@code Controller} 类中通过 {@link org.springframework.graphql.data.method.annotation.BatchMapping BatchMapping}
 * 注解将多次单次查询合并为一次批量查询, 从而减少查询次数, 参见
 * {@link alvin.study.springboot.graphql.app.api.query.DepartmentQuery#children(alvin.study.springboot.graphql.infra.entity.Department, String, int)
 * DepartmentQuery.children(Department, String, int)} 方法
 * </p>
 *
 * </p>
 */
@Component
@RequiredArgsConstructor
public class DepartmentLoader implements BiFunction<Set<Long>, BatchLoaderEnvironment, Mono<Map<Long, Department>>> {
    private final DepartmentMapper departmentMapper;

    @Override
    public Mono<Map<Long, Department>> apply(Set<Long> ids, BatchLoaderEnvironment env) {
        var ctx = ContextHolder.getValue();
        var org = ctx.<Org>get(ContextKey.KEY_ORG);

        return Mono.just(
            departmentMapper.selectList(
                Wrappers.lambdaQuery(Department.class)
                        .eq(Department::getOrgId, org.getId())
                        .in(Department::getId, ids))
                    .stream()
                    .collect(Collectors.toMap(Department::getId, Functions.identity())));
    }
}
