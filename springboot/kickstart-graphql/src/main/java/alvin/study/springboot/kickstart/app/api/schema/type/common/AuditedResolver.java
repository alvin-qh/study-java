package alvin.study.springboot.kickstart.app.api.schema.type.common;

import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;

import alvin.study.springboot.kickstart.app.api.schema.loader.UserLoaderProvider;
import alvin.study.springboot.kickstart.app.api.schema.type.UserType;
import alvin.study.springboot.kickstart.conf.GraphqlConfig;
import alvin.study.springboot.kickstart.infra.entity.User;
import graphql.kickstart.tools.GraphQLResolver;
import graphql.schema.DataFetchingEnvironment;

/**
 * 定义 Graphql Type 解析器, 解析 {@link AuditedType} 未包含的额外的字段值
 *
 * <p>
 * {@link AuditedType} 包含了 {@link AuditedType#getCreatedBy()} 和
 * {@link AuditedType#getUpdatedBy()} 两个方法, 返回的是
 * {@link User User} 实体对象的 id 值
 * </p>
 *
 * <p>
 * 本类型增加了 {@code createdByUser} 和 {@code updateByUser} 两个字段, 在 id 字段的基础上,
 * 查询到对应的 {@link User User} 实体并转为 {@link UserType} 类型
 * </p>
 *
 * <p>
 * 为了避免 n + 1 问题, 这里使用了 {@link DataLoader} 进行数据批量读取, 通过
 * {@link DataFetchingEnvironment#getDataLoaderRegistry()} 方法可以获取到所有
 * {@link DataLoader} 对象的注册器, 在通过名称获取指定的 {@link DataLoader} 对象
 * </p>
 *
 * <p>
 * 所有的 {@link DataLoader} 对象是通过
 * {@link GraphqlConfig#buildDataLoaders()
 * GraphqlConfig.buildDataLoaders()} 进行注册的
 * </p>
 */
public interface AuditedResolver<T extends AuditedType> extends GraphQLResolver<T> {
    /**
     * 补充 {@code createdByUser} 查询字段
     *
     * @param instance 继承 {@link AuditedType} 类型的对象
     * @param env      {@link DataFetchingEnvironment} 对象, 用于获取指定的
     *                 {@link DataLoader} 对象
     * @return 一个异步函数, 将通过每个 id 获取对象的处理延时执行, 转化为批量处理
     * @see UserLoaderProvider
     */
    default CompletableFuture<@NotNull UserType> getCreatedByUser(T instance, DataFetchingEnvironment env) {
        var mapper = (ModelMapper) env.getGraphQlContext().get(ModelMapper.class);

        DataLoader<Long, User> loader = env.getDataLoaderRegistry().getDataLoader(UserLoaderProvider.NAME);
        var future = loader.load(instance.getCreatedBy());
        return future.thenApply(u -> mapper.map(u, UserType.class));
    }

    /**
     * 补充 {@code updatedByUser} 查询字段
     *
     * @param instance 继承 {@link AuditedType} 类型的对象
     * @param env      {@link DataFetchingEnvironment} 对象, 用于获取指定的
     *                 {@link DataLoader} 对象
     * @return 一个异步函数, 将通过每个 id 获取对象的处理延时执行, 转化为批量处理
     * @see UserLoaderProvider
     */
    default CompletableFuture<@NotNull UserType> getUpdatedByUser(T instance, DataFetchingEnvironment env) {
        var mapper = (ModelMapper) env.getGraphQlContext().get(ModelMapper.class);

        DataLoader<Long, User> loader = env.getDataLoaderRegistry().getDataLoader(UserLoaderProvider.NAME);
        var future = loader.load(instance.getUpdatedBy());
        return future.thenApply(u -> mapper.map(u, UserType.class));
    }
}
