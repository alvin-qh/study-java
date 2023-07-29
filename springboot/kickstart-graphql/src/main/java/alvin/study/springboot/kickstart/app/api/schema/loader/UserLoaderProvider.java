package alvin.study.springboot.kickstart.app.api.schema.loader;

import alvin.study.springboot.kickstart.core.context.Context;
import alvin.study.springboot.kickstart.core.context.CustomRequestAttributes;
import alvin.study.springboot.kickstart.core.graphql.annotation.DataLoaderCreator;
import alvin.study.springboot.kickstart.core.graphql.dataloader.DataLoaderProvider;
import alvin.study.springboot.kickstart.infra.entity.User;
import alvin.study.springboot.kickstart.infra.mapper.UserMapper;
import com.google.common.base.Functions;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 用户读取 {@link DataLoader} 提供器
 *
 * @see DataLoaderProvider
 */
@DataLoaderCreator(name = UserLoaderProvider.NAME)
@RequiredArgsConstructor
public class UserLoaderProvider implements DataLoaderProvider<Long, User> {
    // DataLoader 的名称
    public static final String NAME = "UserLoader";

    /**
     * 注入 {@link UserMapper} 对象, 用于查询 {@link User User} 实体
     */
    private final UserMapper userMapper;

    @Override
    public DataLoader<Long, User> get() {
        // 产生一个 Key/Value 对应关系的 DataLoader 对象
        return DataLoaderFactory.newMappedDataLoader(userIds -> {
            // 获取上下文对象
            var context = Context.current();

            // 返回一个异步执行方法
            return CompletableFuture.supplyAsync(() -> {
                // 进入新线程, 重新注册 Context 对象
                try (var ignore = CustomRequestAttributes.scopedRegister(context)) {
                    // 根据 id 集合查询 User 对象集合, 并转为 UserType 对象
                    return userMapper.selectBatchIds(userIds).stream()
                        .collect(Collectors.toMap(User::getId, Functions.identity()));
                }
            });
        });
    }
}
