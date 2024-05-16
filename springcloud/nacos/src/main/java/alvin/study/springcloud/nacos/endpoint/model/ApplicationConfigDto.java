package alvin.study.springcloud.nacos.endpoint.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用程序配置信息类型
 *
 * <p>
 * 用于 Controller 中返回对象
 * </p>
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class ApplicationConfigDto {
    /**
     * 配置的 {@code common} 部分
     */
    private Common common;

    /**
     * 应用程序配置 {@code common} 部分信息类型
     *
     * <p>
     * 即:
     *
     * <pre>
     * common:
     *   key1: value1
     *   key2: value2
     * </pre>
     * </p>
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    @AllArgsConstructor
    public static class Common {
        /**
         * 搜索引擎地址配置
         */
        private String searchUrl;
    }
}
