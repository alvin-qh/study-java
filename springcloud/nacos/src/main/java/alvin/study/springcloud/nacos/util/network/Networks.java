package alvin.study.springcloud.nacos.util.network;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.net.NetworkInterface;
import java.util.HashSet;
import java.util.Set;

/**
 * 网络相关工具类
 */
public final class Networks {
    private Networks() {
    }

    /**
     * 获取当前主机的所有 IP 地址
     *
     * @return 当前主机 IP 地址集合
     */
    @SneakyThrows
    public static @NotNull Set<String> localHostIpAddresses() {
        // 保存 IP 地址的 Set 集合
        var ips = new HashSet<String>();

        // 获取当前机器的所有网络适配器对象
        var interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            var iface = interfaces.nextElement();
            if (iface.isLoopback() || !iface.isUp()) {
                // 如果适配器未开启或是本地回环地址 (即 lo 适配器), 则忽略
                continue;
            }

            // 获取适配器绑定的 IP 地址集合 (每个适配器可能绑定一个或多个 IP 地址)
            var addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                // 获取 IP 地址并保存
                ips.add(addresses.nextElement().getHostAddress());
            }
        }
        return ips;
    }
}
