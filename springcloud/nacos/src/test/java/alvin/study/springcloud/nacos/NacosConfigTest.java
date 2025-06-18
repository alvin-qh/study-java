package alvin.study.springcloud.nacos;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import com.alibaba.nacos.api.config.ConfigChangeEvent;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.listener.impl.AbstractConfigChangeListener;

import lombok.SneakyThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import alvin.study.springcloud.nacos.core.model.ApplicationConfig;
import alvin.study.springcloud.nacos.core.model.ResponseWrapper;
import alvin.study.springcloud.nacos.endpoint.model.ApplicationConfigDto;
import alvin.study.springcloud.nacos.util.NacosUtil;

/**
 * 测试 Nacos 配置中心
 */
class NacosConfigTest extends BaseTest {
    /**
     * 注入 Nacos 工具类
     *
     * <p>
     * 通过编码方式操作 Nacos 服务端, 进行配置发布, 删除和监听配置变化的操作
     * </p>
     *
     * <p>
     * 参见 {@link NacosUtil} 方法类型, 该类型对象在 {@code TestingConfig.nacosUtil()} 方法中进行实例化
     * </p>
     */
    @Autowired
    private NacosUtil nacosUtil;

    /**
     * 注入 {@link ApplicationConfig} 类型对象, 从 Nacos 获取的配置信息内容会注入到此对象字段中, 并在配置变化后自动进行刷新
     */
    @Autowired
    private ApplicationConfig applicationConfig;

    /**
     * 测试配置变更监听
     *
     * <p>
     * 变更 Nacos 配置内容后, 可以监听到配置变化, 并且 {@link ApplicationConfig} 对象的内容会自动刷新
     * </p>
     */
    @Test
    void listener_shouldListenedConfigChanged() throws Exception {
        // 发布一条配置信息
        assert nacosUtil.publishConfig(CONFIG_DATA_ID, CONFIG_GROUP, loadTestConfig(), ConfigType.YAML);

        // 确认配置对象被正确发布
        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> applicationConfig.getCommon().getSearchUrl(), equalTo("https://www.baidu.com"));

        var event = new Object();
        var changeItems = new ArrayList<>();

        // 修改配置信息
        assert nacosUtil.publishConfig(CONFIG_DATA_ID, CONFIG_GROUP, loadTestChangedConfig(), ConfigType.YAML);

        // 增加监听器, 监听配置改变事件回调
        nacosUtil.addConfigListener(CONFIG_DATA_ID, CONFIG_GROUP, new AbstractConfigChangeListener() {
            /**
             * 接收配置变更事件通知
             *
             * @param event 事件通知对象
             */
            @Override
            public void receiveConfigChange(ConfigChangeEvent event) {
                // 遍历发生变化的所有内容项
                for (var item : event.getChangeItems()) {
                    // 添加到变更列表中
                    changeItems.add(String.format("%s:%s", item.getKey(), item.getNewValue()));
                }

                // 发送线程通知, 表示回调结束
                synchronized (event) {
                    event.notify();
                }
            }
        });

        // 等待事件通知回调函数结束
        synchronized (event) {
            event.wait(5000);
        }

        // 确认收到了一条配置项更改通知
        then(changeItems).containsExactly("common.search_url:https://www.google.com");

        // 确认配置对象被正确刷新
        then(applicationConfig.getCommon().getSearchUrl()).isEqualTo("https://www.google.com");
    }

    /**
     * 测试 {@code ApplicationConfigController#getConfig()} 方法, 获取配置信息
     */
    @Test
    void getConfig_shouldReturn200Ok() throws NacosException {
        // 发布一条配置信息
        assert nacosUtil.publishConfig(CONFIG_DATA_ID, CONFIG_GROUP, loadTestConfig(), ConfigType.YAML);

        // 发起请求, 获取配置信息
        await().atMost(10, TimeUnit.SECONDS).until(
            () -> getJson("/api/config")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<ResponseWrapper<ApplicationConfigDto>>() {})
                    .returnResult()
                    .getResponseBody(),
            resp -> resp.getRetCode() == 0
                    && resp.getPayload().getCommon().getSearchUrl().equals("https://www.baidu.com"));
    }

    /**
     * 每次测试结束后调用
     */
    @AfterEach
    @SneakyThrows
    void afterEach() {
        // 从配置中心删除配置项
        assert nacosUtil.removeConfig(CONFIG_DATA_ID, CONFIG_GROUP);
    }
}
