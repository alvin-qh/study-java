package alvin.study.app.api.schema.type.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 所有具备主键 id 的 Type 类型超类
 *
 * <p>
 * 对应 {@link alvin.study.infra.entity.common.BaseEntity BaseEntity} 类型
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseType implements Serializable {
    // 主键 id
    protected Long id;
}
