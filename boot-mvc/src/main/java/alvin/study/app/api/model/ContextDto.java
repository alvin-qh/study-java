package alvin.study.app.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * 向客户端返回数据的 DTO 对象
 */
@Getter
public class ContextDto implements Serializable {
    // 组织代码
    private final String orgCode;

    // 用户 ID
    private final Long userId;

    /**
     * 构造器
     *
     * @param userId 用户 ID
     */
    @JsonCreator
    public ContextDto(
            @JsonProperty("orgCode") String orgCode,
            @JsonProperty("userId") Long userId) {
        this.orgCode = orgCode;
        this.userId = userId;
    }
}
