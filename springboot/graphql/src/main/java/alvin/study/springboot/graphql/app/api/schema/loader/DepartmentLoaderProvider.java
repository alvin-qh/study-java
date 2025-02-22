package alvin.study.springboot.kickstart.app.api.schema.loader;

import alvin.study.springboot.kickstart.core.context.Context;
import alvin.study.springboot.kickstart.core.context.CustomRequestAttributes;
import alvin.study.springboot.kickstart.core.graphql.annotation.DataLoaderCreator;
import alvin.study.springboot.kickstart.core.graphql.dataloader.DataLoaderProvider;
import alvin.study.springboot.kickstart.infra.entity.Department;
import alvin.study.springboot.kickstart.infra.mapper.DepartmentMapper;
import com.google.common.base.Functions;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 部门读取 {@link DataLoader} 提供器
 *
 * @see DataLoaderProvider
 */
@DataLoaderCreator(name = DepartmentLoaderProvider.NAME)
@RequiredArgsConstructor
public class DepartmentLoaderProvider implements DataLoaderProvider<Long, Department> {
    // DataLoader 的名称
    public static final String NAME = "DepartmentLoader";

    /**
     * 注入 {@link DepartmentMapper} 对象, 用于查询
     * {@link Department Department} 实体
     */
    private final DepartmentMapper departmentMapper;

    @Override
    public DataLoader<Long, Department> get() {
        // 产生一个 Key/Value 对应关系的 DataLoader 对象
        return DataLoaderFactory.newMappedDataLoader(departmentIds -> {
            // 获取上下文对象
            var context = Context.current();

            // 返回一个异步执行方法
            return CompletableFuture.supplyAsync(() -> {
                // 进入新线程, 重新注册 Context 对象
                try (var ignore = CustomRequestAttributes.scopedRegister(context)) {
                    // 根据 id 集合查询 Org 对象集合, 并转为 OrgType 对象
                    return departmentMapper.selectByIds(departmentIds).stream()
                            .collect(Collectors.toMap(Department::getId, Functions.identity()));
                }
            });
        });
    }
}
