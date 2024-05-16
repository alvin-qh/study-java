package alvin.study.springboot.ds.infra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * 数据实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigEntity implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 组织代码
     */
    private String org;

    /**
     * 对应的数据库名称
     */
    private String dbName;

    /**
     * 本记录是否有效
     */
    private boolean valid;

    /**
     * 更新时间
     */
    private Instant updatedAt;

    /**
     * 创建时间
     */
    private Instant createdAt;
}
