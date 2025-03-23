package alvin.study.springcloud.nacos.util;

import java.util.List;
import java.util.Properties;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;

import com.google.common.base.Strings;

/**
 * Nacos 工具类
 */
public final class NacosUtil {
    // 注入 Nacos 配置中心服务对象
    private final ConfigService configService;

    // 注入 Nacos 服务发现服务对象
    private final NamingService namingService;

    /**
     * 构造器
     *
     * @param username   Nacos 登录用户名
     * @param password   Nacos 登录密码
     * @param serverAddr Nacos 服务地址
     * @param namespace  Nacos 相关命名空间
     */
    public NacosUtil(
            String username,
            String password,
            String serverAddr,
            String configNamespace,
            String namingNamespace) throws NacosException {
        var props = new Properties();
        // 设置服务访问登录用户名
        props.setProperty(PropertyKeyConst.USERNAME, username);
        // 设置服务访问登录密码
        props.setProperty(PropertyKeyConst.PASSWORD, password);
        // 设置服务地址
        props.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);

        // 设置服务相关命名空间
        if (!Strings.isNullOrEmpty(configNamespace)) {
            props.setProperty(PropertyKeyConst.NAMESPACE, configNamespace);
        }
        // 创建配置中心服务对象
        configService = ConfigFactory.createConfigService(props);

        // 设置服务相关命名空间
        if (!Strings.isNullOrEmpty(configNamespace)) {
            props.setProperty(PropertyKeyConst.NAMESPACE, namingNamespace);
        }
        // 创建服务发现服务对象
        namingService = NamingFactory.createNamingService(props);
    }

    /**
     * 向注册中心加入一条配置
     *
     * @param dataId  配置相关的 {@code id}
     * @param group   配置所在的组名称
     * @param content 配置内容
     * @param type    配置内容的类型, 参见 {@link ConfigType} 枚举类型
     * @return {@code true} 表示成功创建配置
     */
    public boolean publishConfig(
            String dataId, String group, String content, ConfigType type) throws NacosException {
        return configService.publishConfig(dataId, group, content, type.getType());
    }

    /**
     * 从配置中心删除一条配置
     *
     * @param dataId 配置相关的 {@code id}
     * @param group  配置所在的组名称
     * @return {@code true} 表示成功删除
     */
    public boolean removeConfig(String dataId, String group) throws NacosException {
        return configService.removeConfig(dataId, group);
    }

    /**
     * 增加配置变更监听
     *
     * @param dataId   配置相关的 {@code id}
     * @param group    配置所在的组名称
     * @param listener 监听回调对象
     */
    public void addConfigListener(String dataId, String group, Listener listener) throws NacosException {
        configService.addListener(dataId, group, listener);
    }

    /**
     * 注册服务用于服务发现
     *
     * @param serviceName 服务名称
     * @param group       服务所在组名称
     * @param ipAddress   服务所在机器的 IP 地址
     * @param port        服务开放的网络端口
     */
    public void registerService(
            String serviceName,
            String group,
            String ipAddress,
            int port) throws NacosException {
        namingService.registerInstance(serviceName, group, ipAddress, port);
    }

    /**
     * 取消之前注册的服务
     *
     * @param serviceName 服务名称
     * @param group       服务所在组名称
     * @param ipAddress   服务所在机器的 IP 地址
     * @param port        服务开放的网络端口
     */
    public void deregisterInstance(
            String serviceName,
            String group,
            String ipAddress,
            int port) throws NacosException {
        namingService.deregisterInstance(serviceName, group, ipAddress, port);
    }

    /**
     * 获取已经注册的所有服务实例集合
     *
     * @param serviceName 服务名称
     * @param group       服务所在组名称
     * @return 服务实例集合
     */
    public List<Instance> getAllInstance(String serviceName, String group) throws NacosException {
        return namingService.getAllInstances(serviceName, group);
    }

    /**
     * 增加服务发现变更监听
     *
     * @param serviceName 服务名
     * @param group       服务监听相关的组名称
     * @param listener    监听回调对象
     */
    public void addNamingListener(String serviceName, String group, EventListener listener) throws NacosException {
        namingService.subscribe(serviceName, group, listener);
    }

    /**
     * 关闭到 Nacos 服务端的连接
     */
    public void shutdown() throws NacosException {
        configService.shutDown();
        namingService.shutDown();
    }
}
