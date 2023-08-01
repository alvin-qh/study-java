package alvin.study.springcloud.eureka.client.endpoint.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Controller 返回结果的 DTO 类型
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class HelloDto {
    private String applicationName;
    private String content;
}
