package alvin.study.app.api.schema.loader;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;

import com.google.common.base.Functions;

import alvin.study.core.context.Context;
import alvin.study.core.context.CustomRequestAttributes;
import alvin.study.core.graphql.annotation.DataLoaderCreator;
import alvin.study.core.graphql.dataloader.DataLoaderProvider;
import alvin.study.infra.entity.Org;
import alvin.study.infra.mapper.OrgMapper;
import lombok.RequiredArgsConstructor;

/**
 * 用户读取 {@link DataLoader} 提供器
 *
 * @see DataLoaderProvider
 */
@DataLoaderCreator(name = OrgLoaderProvider.NAME)
@RequiredArgsConstructor
public class OrgLoaderProvider implements DataLoaderProvider<Long, Org> {
    // DataLoader 的名称
    public static final String NAME = "OrgLoader";

    /**
     * 注入 {@link OrgMapper} 对象, 用于查询 {@link alvin.study.infra.entity.Org Org} 实体
     */
    private final OrgMapper orgMapper;

    @Override
    public DataLoader<Long, Org> get() {
        // 产生一个 Key/Value 对应关系的 DataLoader 对象
        return DataLoaderFactory.newMappedDataLoader(orgIds -> {
            // 获取上下文对象
            var context = Context.current();

            // 返回一个异步执行方法
            return CompletableFuture.supplyAsync(() -> {
                // 进入新线程, 重新注册 Context 对象
                try (var ignore = CustomRequestAttributes.scopedRegister(context)) {
                    // 根据 id 集合查询 Org 对象集合, 并转为 OrgType 对象
                    return orgMapper.selectBatchIds(orgIds).stream()
                            .collect(Collectors.toMap(Org::getId, Functions.identity()));
                }
            });
        });
    }
}
