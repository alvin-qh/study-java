package alvin.study.springboot.ds.core.data;

import alvin.study.springboot.ds.conf.DataSourceConfig;

/**
 * 数据源标识枚举
 *
 * <p>
 * 数据源标识和数据源的对应关系参考
 * {@link DataSourceConfig#dynamicDataSource(javax.sql.DataSource, javax.sql.DataSource)
 * DataSourceConfig.dynamicDataSource(DataSource, DataSource)} 方法
 * </p>
 */
public enum DataSourceTarget {
    /**
     * 对应 {@code db1DataSource} 数据源
     */
    db1,
    /**
     * 对应 {@code db2DataSource} 数据源
     */
    db2;
}
