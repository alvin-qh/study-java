package alvin.study.springboot.ds.infra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 数据实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataEntity {
    private Long id;
    private String name;
    private String value;
    private Instant updatedAt;
    private Instant createdAt;
}
