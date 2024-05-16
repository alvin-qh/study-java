package alvin.study.springboot.ds.app.domain.service;

import alvin.study.springboot.ds.app.domain.model.ConfigDto;
import alvin.study.springboot.ds.app.domain.service.common.BaseService;
import alvin.study.springboot.ds.core.data.DataSourceContext;
import alvin.study.springboot.ds.core.data.DefaultDataSource;
import alvin.study.springboot.ds.core.data.DynamicDataSource;
import alvin.study.springboot.ds.core.flyway.Migration;
import alvin.study.springboot.ds.infra.entity.ConfigEntity;
import alvin.study.springboot.ds.infra.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * 数据库服务类
 *
 * <p>
 * 配置信息存储在 {@code default} 数据源对应的数据库中, 需要切换到 {@code default} 数据源进行操作
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService extends BaseService {
    // 注入数据库 Migration 对象
    private final Migration migration;

    // 注入 config 表持久化操作对象
    private final ConfigRepository configRepository;

    // 注入动态数据源对象
    private final DynamicDataSource dynamicDataSource;

    /**
     * 根据组织代码查询配置实体 {@link ConfigEntity} 对象
     *
     * @param org 组织代码
     * @return 配置对象
     */
    public ConfigDto findConfig(String org) {
        var entity = configRepository.selectByOrg(org).orElseThrow(() -> new ConfigNotExistException(org));
        return new ConfigDto(
            entity.getId(),
            entity.getOrg(),
            entity.getDbName(),
            entity.getUpdatedAt(),
            entity.getCreatedAt()
        );
    }

    /**
     * 根据所给的组织代码创建数据库
     *
     * <p>
     * {@link DefaultDataSource @DefaultDataSource} 注解表示在执行此方法时, 将数据源切换回默认数据源
     * </p>
     *
     * @param org 组织代码
     * @return 所创建的数据库的配置信息对象
     */
    @DefaultDataSource
    public ConfigDto createConfig(String org) {
        // 在配置表中查询给定组织的配置
        var entity = configRepository.selectByOrg(org).orElseGet(() -> {
                log.info("No database found for org=\"{}\", created it", org);

                // 配置不存在, 则创建对应的数据库, 切换数据源, 并进行初始化操作
                var dbName = "db_" + org;
                try (var s = DataSourceContext.switchTo(dbName)) {
                    var tx = beginTransaction();
                    try {
                        migration.migrateBusinessDB(dbName);
                        commit(tx);
                        log.info("Create database (name=\"{}\") for org=\"{}\"", dbName, org);
                    } catch (RuntimeException e) {
                        rollback(tx);
                        throw e;
                    }
                }

                var tx = beginTransaction();
                try {
                    // 增加对应的配置项
                    var configEntity = new ConfigEntity();
                    configEntity.setOrg(org);
                    configEntity.setDbName(dbName);
                    configEntity.setCreatedAt(Instant.now());
                    configEntity.setUpdatedAt(Instant.now());
                    configRepository.insert(configEntity);
                    commit(tx);

                    return configEntity;
                } catch (RuntimeException e) {
                    rollback(tx);
                    throw e;
                }
            }
        );

        return new ConfigDto(
            entity.getId(),
            entity.getOrg(),
            entity.getDbName(),
            entity.getUpdatedAt(),
            entity.getCreatedAt()
        );
    }

    /**
     * 删除配置
     *
     * @param org 组织代码
     */
    @DefaultDataSource
    public void deleteConfig(String org) {
        // 查询指定组织代码的配置
        configRepository.selectByOrg(org).ifPresent(entity -> {
            var tx = beginTransaction();
            try {
                // 设置该配置为无效配置
                entity.setValid(false);
                configRepository.update(entity);

                // 删除无效的数据源
                dynamicDataSource.delete(entity.getDbName());
                log.info("Remove config for org=\"{}\"", org);
                commit(tx);
            } catch (RuntimeException e) {
                rollback(tx);
                throw e;
            }
        });
    }
}
