package alvin.study.springboot.shiro.app.endpoint.model;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 首页信息类型
 */
@Data
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class HomeDto implements Serializable {
    /**
     * 欢迎语字段
     */
    private String welcome;
}
