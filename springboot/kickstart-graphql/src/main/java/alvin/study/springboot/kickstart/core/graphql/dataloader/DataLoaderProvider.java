package alvin.study.springboot.kickstart.core.graphql.dataloader;

import alvin.study.springboot.kickstart.conf.GraphqlConfig;
import alvin.study.springboot.kickstart.core.graphql.annotation.DataLoaderCreator;
import org.dataloader.DataLoader;

/**
 * {@link DataLoader} 类型对象提供器
 *
 * <p>
 * 该类型对象会统一注入到 {@link GraphqlConfig#dataLoaderProviders
 * GraphqlConfig.dataLoaderProviders} 集合字段中, 在
 * {@link GraphqlConfig#build(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
 * GraphqlConfig.build(HttpServletRequest, HttpServletResponse)} 等方法中统一调用处理
 * </p>
 */
public interface DataLoaderProvider<K, V> {
    /**
     * 获取 {@link DataLoader} 的名称
     *
     * @return {@link DataLoader} 名称
     */
    default String name() {
        return this.getClass().getAnnotation(DataLoaderCreator.class).name();
    }

    /**
     * 获取一个 {@link DataLoader} 对象
     *
     * @return {@link DataLoader} 对象
     */
    DataLoader<K, V> get();
}
