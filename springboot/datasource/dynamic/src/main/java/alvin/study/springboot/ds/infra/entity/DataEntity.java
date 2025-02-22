package alvin.study.springboot.ds.infra.entity;

import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataEntity implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 数据名称
     */
    private String name;

    /**
     * 数据值
     */
    private String value;

    /**
     * 更新时间
     */
    private Instant updatedAt;

    /**
     * 创建时间
     */
    private Instant createdAt;
}
