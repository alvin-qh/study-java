package alvin.study.springboot.ds.core.http.common;

import alvin.study.springboot.ds.http.Servlets;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户端错误类型
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientError {
    /**
     * 请求路径
     */
    private String path;

    /**
     * 错误代码
     */
    private String errCode;

    /**
     * 错误信息
     */
    private String errMsg;

    /**
     * 创建客户端错误对象
     *
     * @param errCode 错误代码
     * @param errMsg  错误信息
     * @return 客户端错误对象
     */
    public static ClientError create(String errCode, String errMsg) {
        // 获取请求对象
        var request = Servlets.getHttpServletRequest();
        // 返回对象
        return new ClientError(request.getRequestURI(), errCode, errMsg);
    }
}
