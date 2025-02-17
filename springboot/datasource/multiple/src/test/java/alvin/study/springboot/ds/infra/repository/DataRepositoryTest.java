package alvin.study.springboot.ds.infra.repository;

import alvin.study.springboot.ds.IntegrationTest;
import alvin.study.springboot.ds.core.data.DataSourceContext;
import alvin.study.springboot.ds.core.data.DataSourceTarget;
import alvin.study.springboot.ds.infra.entity.DataEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link DataRepository} 类型
 */
class DataRepositoryTest extends IntegrationTest {
    // 注入对象
    @Autowired
    private DataRepository repository;

    /**
     * 测试 {@link DataRepository#insert(DataEntity)} 方法和
     * {@link DataRepository#selectAll()} 方法
     *
     * <p>
     * 在执行过程中切换数据源, 以保证把数据插入到不同的数据库且从不同的数据库中读取
     * </p>
     *
     * <p>
     * 注意, 事务不能跨数据源使用, 即事务必须在完成数据源切换后启动和结束, 在一个事务中不能进行数据源切换
     * </p>
     */
    @Test
    void insert_shouldInsertDataIntoDifferentDB() {
        // 切换到 db1 数据源
        try (var s = DataSourceContext.switchTo(DataSourceTarget.db1)) {
            // 启动事务
            try (var tx = beginTx(false)) {
                // 插入数据
                var entity = new DataEntity();
                entity.setName("name_for_db1");
                entity.setValue("value_for_db1");
                repository.insert(entity);
            }
        }

        // 切换到 db2 数据源
        try (var s = DataSourceContext.switchTo(DataSourceTarget.db2)) {
            // 启动事务
            try (var tx = beginTx(false)) {
                // 插入数据
                var entity = new DataEntity();
                entity.setName("name_for_db2");
                entity.setValue("value_for_db2");
                repository.insert(entity);
            }
        }

        // 切换到 db1 数据源, 查询数据, 确保该 db1 数据库中仅包含指定数据
        try (var s = DataSourceContext.switchTo(DataSourceTarget.db1)) {
            var entities = repository.selectAll();
            then(entities)
                    .hasSize(1)
                    .singleElement()
                    .extracting("name", "value")
                    .containsExactly("name_for_db1", "value_for_db1");
        }

        // 切换到 db2 数据源, 查询数据, 确保该 db2 数据库中仅包含指定数据
        try (var s = DataSourceContext.switchTo(DataSourceTarget.db2)) {
            var entities = repository.selectAll();
            then(entities)
                    .hasSize(1)
                    .singleElement()
                    .extracting("name", "value")
                    .containsExactly("name_for_db2", "value_for_db2");
        }
    }
}
