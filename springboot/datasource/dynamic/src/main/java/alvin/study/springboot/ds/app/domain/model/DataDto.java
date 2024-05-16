package alvin.study.springboot.ds.app.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * 数据 Dto 类型
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class DataDto implements Serializable {
    private Long id;
    private String name;
    private String value;
    private Instant updatedAt;
    private Instant createdAt;
}
