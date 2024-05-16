package alvin.study.springcloud.gateway.client.endpoint.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 应用程序信息 DTO 对象
 *
 * <p>
 * 用于 {@link alvin.study.springcloud.gateway.client.endpoint.BackendController
 * BackendController} 返回数据
 * </p>
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class AppInfoDto implements Serializable {
    /**
     * 本地应用程序监听端口号
     */
    private int localPort;

    /**
     * 当前应用程序名称
     */
    private String applicationName;
}
