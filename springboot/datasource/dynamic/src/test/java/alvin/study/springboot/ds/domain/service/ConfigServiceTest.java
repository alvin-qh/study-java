package alvin.study.springboot.ds.domain.service;

import alvin.study.springboot.ds.IntegrationTest;
import alvin.study.springboot.ds.app.domain.service.ConfigService;
import alvin.study.springboot.ds.core.data.DataSourceContext;
import alvin.study.springboot.ds.core.data.DefaultDataSource;
import alvin.study.springboot.ds.infra.entity.DataEntity;
import alvin.study.springboot.ds.infra.repository.DataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

/**
 * 测试 {@link ConfigService} 类型
 */
class ConfigServiceTest extends IntegrationTest {
    // 注入配置服务对象
    @Autowired
    private ConfigService configService;

    // 注入数据持久化操作对象
    @Autowired
    private DataRepository dataRepository;

    /**
     * 测试 {@link ConfigService#createConfig(String)} 方法, 创建配置实体对象
     */
    @Test
    @DefaultDataSource
    void createConfig_shouldCreateNewEntity() {
        // 创建配置实体对象
        var config = configService.createConfig("test-org-1");

        // 将数据源切换到新 org 对应的数据源
        try (var s = DataSourceContext.switchTo(config.getDbName())) {
            try (var tx = beginTx(false)) {
                // 在指定数据源内创建 Data 实体对象
                var entity = new DataEntity();
                entity.setName("test-name-" + config.getDbName());
                entity.setValue("test-value-" + config.getDbName());
                dataRepository.insert(entity);
            }
        }
        // 切换回默认数据源, 确认无法访问 data 表, 因为默认数据源对应的数据库不包含该表
        thenThrownBy(() -> dataRepository.selectAll()).isInstanceOf(BadSqlGrammarException.class);

        // 切换到指定 org 对应的数据源
        try (var s = DataSourceContext.switchTo(config.getDbName())) {
            // 查询 data
            var datas = dataRepository.selectAll();

            // 确认查询结果
            then(datas)
                    .hasSize(1)
                    .singleElement()
                    .extracting("name", "value")
                    .contains("test-name-" + config.getDbName(), "test-value-" + config.getDbName());
        }
    }
}
