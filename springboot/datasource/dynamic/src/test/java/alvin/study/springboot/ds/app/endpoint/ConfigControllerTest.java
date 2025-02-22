package alvin.study.springboot.ds.app.endpoint;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import alvin.study.springboot.ds.IntegrationTest;
import alvin.study.springboot.ds.app.domain.model.ConfigDto;
import alvin.study.springboot.ds.app.domain.service.ConfigNotExistException;
import alvin.study.springboot.ds.app.domain.service.ConfigService;
import alvin.study.springboot.ds.core.data.DynamicDataSource;
import alvin.study.springboot.ds.core.http.common.ResponseDto;

/**
 * 测试 {@link ConfigController} 类型
 */
public class ConfigControllerTest extends IntegrationTest {
    // 定义响应数据返回类型
    private static final ParameterizedTypeReference<ResponseDto<ConfigDto>> RESP_TYPE
        = new ParameterizedTypeReference<>() {};

    // 注入配置服务对象
    @Autowired
    private ConfigService configService;

    // 注入动态数据源对象
    @Autowired
    private DynamicDataSource dynamicDataSource;

    /**
     * 测试 {@link ConfigController#createConfig(String)} 方法, 根据组织代码创建配置
     */
    @Test
    void createConfig_shouldCreateEntity() {
        // 创建任意一个 org 配置
        configService.createConfig("test-org-1");

        // 创建指定的 org 配置
        var resp = postJson("/api/config/{0}", "test-org-1", "test-org-2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(RESP_TYPE)
                .returnResult()
                .getResponseBody();

        // 确认返回响应结果正确
        then(resp.getStatus()).isZero();
        then(resp.getPayload().getOrg()).isEqualTo("test-org-2");
        then(resp.getPayload().getDbName()).isEqualTo("db_test-org-2");

        // 确认配置已经创建
        var config = configService.findConfig("test-org-2");
        then(config.getOrg()).isEqualTo("test-org-2");
        then(config.getDbName()).isEqualTo("db_test-org-2");
    }

    /**
     * 测试 {@link ConfigController#deleteConfig(String)} 方法, 根据组织代码删除配置和数据源
     */
    @Test
    void deleteConfig_shouldDeleteEntity() {
        // 创建指定 org 配置
        configService.createConfig("test-org-1");

        // 删除指定的 org 配置
        deleteJson("/api/config/{0}", "test-org-1", "test-org-1")
                .exchange()
                .expectStatus().isOk();

        // 确认返回响应结果正确
        thenThrownBy(() -> configService.findConfig("test-org-1")).isInstanceOf(ConfigNotExistException.class);

        // 确认数据源已被删除
        then(dynamicDataSource.getAllLookupKeys()).doesNotContain("test-org-1");
    }
}
