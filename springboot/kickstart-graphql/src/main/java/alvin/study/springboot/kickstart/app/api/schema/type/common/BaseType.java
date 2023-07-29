package alvin.study.springboot.kickstart.app.api.schema.type.common;

import alvin.study.springboot.kickstart.infra.entity.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 所有具备主键 id 的 Type 类型超类
 *
 * <p>
 * 对应 {@link BaseEntity BaseEntity} 类型
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseType implements Serializable {
    // 主键 id
    protected Long id;
}
