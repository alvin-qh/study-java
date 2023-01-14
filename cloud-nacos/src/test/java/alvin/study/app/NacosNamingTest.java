package alvin.study.app;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.databind.ObjectMapper;

import alvin.study.IntegrationTest;
import alvin.study.app.endpoint.model.ApplicationConfigDto;
import alvin.study.core.model.ResponseWrapper;
import alvin.study.core.nacos.NacosUtil;
import alvin.study.util.network.Networks;
import lombok.SneakyThrows;

/**
 * 测试 Nacos 服务发现
 */
class NacosNamingTest extends IntegrationTest {
    /**
     * 注入 Rest 请求模板对象
     *
     * <p>
     * 该对象已经开启了服务发现和负载均衡功能, 参考 {@link alvin.study.conf.NacosConfig#restTemplate()
     * NacosConfig.restTemplate()} 方法以及
     * {@link org.springframework.cloud.client.discovery.EnableDiscoveryClient @EnableDiscoveryClient}
     * 和
     * {@link org.springframework.cloud.client.loadbalancer.LoadBalanced @LoadBalanced}
     * 注解
     * </p>
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 注入 JSON 转换对象
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 注入 Nacos 工具对象
     */
    @Autowired
    private NacosUtil nacosUtil;

    /**
     * 注入应用程序名称, 作为服务注册名称
     */
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 注入测试启动时绑定的端口号, 作为服务注册端口号
     */
    @LocalServerPort
    private int serverPort;

    /**
     * 测试服务注册
     *
     * <p>
     * 通过服务名称和服务组, 获取当前已注册服务的服务实例
     * </p>
     */
    void getAllInstance_shouldGetRegisteredInstances() throws Exception {
        // 获取已注册的服务集合
        var instances = nacosUtil.getAllInstance(applicationName, NAMING_GROUP);

        // 确认当前服务已被注册
        then(instances)
                .hasSize(1)
                .singleElement()
                .matches(inst -> inst.getServiceName().equals(String.format("%s@@%s", NAMING_GROUP, applicationName))
                                 && Networks.localHostIpAddresses().contains(inst.getIp())
                                 && inst.getPort() == serverPort);
    }

    /**
     * 测试根据服务名访问目标服务
     */
    @Test
    @SuppressWarnings("unchecked")
    void discover_shouldVisitServiceByName() throws NacosException {
        // 向配置中心发布一个配置项
        assert nacosUtil.publishConfig(CONFIG_DATA_ID, CONFIG_GROUP, loadTestConfig(), ConfigType.YAML);

        // 通过服务名称访问目标服务
        await().atMost(2, TimeUnit.SECONDS).until(
            () -> (ResponseWrapper<Map<String, ?>>) restTemplate.getForObject(
                "http://alvin-study-spring-cloud-nacos/api/config", ResponseWrapper.class),
            resp -> {
                var config = objectMapper.convertValue(resp.getPayload(), ApplicationConfigDto.class);
                return resp.getRetCode() == 0
                       && config.getCommon().getSearchUrl().equals("https://www.baidu.com");
            });
    }

    /**
     * 每次测试结束后调用
     */
    @Override
    @AfterEach
    @SneakyThrows
    protected void afterEach() {
        super.afterEach();

        // 从配置中心删除配置项
        assert nacosUtil.removeConfig(CONFIG_DATA_ID, CONFIG_GROUP);
    }
}
