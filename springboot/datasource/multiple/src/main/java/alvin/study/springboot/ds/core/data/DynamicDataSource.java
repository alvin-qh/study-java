package alvin.study.springboot.ds.core.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源类型
 *
 * <p>
 * 所谓动态数据源, 即继承了 {@link AbstractRoutingDataSource} 类型的数据源类, 该类型是一个代理类型,
 * 代理实际的一个数据源对象
 * </p>
 *
 * <p>
 * 该类型通过 {@link #determineCurrentLookupKey()} 方法获取到一个标识, 并根据标识的值代理不同的数据源对象, 具体标识和被代理对象之间的关系
 * </p>
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        var target = DataSourceContext.current();

        log.info("Switch to datasource: {}", target);
        return target;
    }
}
