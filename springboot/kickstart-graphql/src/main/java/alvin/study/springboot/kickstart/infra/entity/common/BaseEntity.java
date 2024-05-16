package alvin.study.springboot.kickstart.infra.entity.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 所有实体类超类
 *
 * <p>
 * 该类为所有子实体类定义了主键字段, 由 {@link TableId @TableId} 的 {@code value} 属性指定,
 * {@code type} 属性指定了主键产生的方式, 参考: {@link IdType}
 * </p>
 */
@Data
public abstract class BaseEntity implements Serializable {
    // id 字段
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
}
