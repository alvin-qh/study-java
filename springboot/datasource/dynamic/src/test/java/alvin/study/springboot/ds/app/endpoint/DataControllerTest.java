package alvin.study.springboot.ds.app.endpoint;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import alvin.study.springboot.ds.IntegrationTest;
import alvin.study.springboot.ds.app.domain.model.DataDto;
import alvin.study.springboot.ds.app.domain.model.DataForm;
import alvin.study.springboot.ds.app.domain.service.ConfigService;
import alvin.study.springboot.ds.core.http.common.ResponseDto;

/**
 * 测试 {@link DataController} 类型
 */
class DataControllerTest extends IntegrationTest {
    // 定义响应数据返回类型
    private static final ParameterizedTypeReference<ResponseDto<DataDto>> RESP_TYPE
        = new ParameterizedTypeReference<>() {};

    // 注入数据服务对象
    @Autowired
    private ConfigService configService;

    /**
     * 测试 {@link DataController#createData(DataForm)} 方法, 创建一条数据
     */
    @Test
    void createData_getData_shouldGet200Response() {
        // 创建配置
        configService.createConfig("test-org-1");

        // 在指定配置对应的 org 下创建数据
        var resp = postJson("/api/data", "test-org-1")
                .bodyValue(new DataForm("db-name-1", "db-value-1"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(RESP_TYPE)
                .returnResult()
                .getResponseBody();

        // 确认响应信息正确
        then(resp.getStatus()).isZero();
        then(resp.getPayload().getId()).isNotNull();

        // 根据正确的 org 获取数据信息
        resp = getJson("/api/data/{0}", "test-org-1", resp.getPayload().getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(RESP_TYPE)
                .returnResult()
                .getResponseBody();

        // 确认响应信息正确
        then(resp.getPayload().getName()).isEqualTo("db-name-1");
        then(resp.getPayload().getValue()).isEqualTo("db-value-1");

        // 根据错误的 org 获取数据信息, 返回 Forbidden 状态
        getJson("/api/data/{0}", "test-org-2", resp.getPayload().getId())
                .exchange()
                .expectStatus().isForbidden();
    }
}
