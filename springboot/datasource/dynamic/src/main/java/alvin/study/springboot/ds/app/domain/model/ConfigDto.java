package alvin.study.springboot.ds.app.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * 配置信息的 Dto 类型
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class ConfigDto implements Serializable {
    private Long id;
    private String org;
    private String dbName;
    private Instant updatedAt;
    private Instant createdAt;
}
