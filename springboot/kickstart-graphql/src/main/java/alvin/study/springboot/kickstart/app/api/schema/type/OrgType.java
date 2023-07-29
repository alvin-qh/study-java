package alvin.study.springboot.kickstart.app.api.schema.type;

import alvin.study.springboot.kickstart.app.api.schema.type.common.BaseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 组织类型
 *
 * <p>
 * 参考 {@code classpath:graphql/org.graphqls} 中定义查询的 schema
 * </p>
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrgType extends BaseType {
    // 组织名称
    private String name;

    // 创建时间
    private OffsetDateTime createdAt;

    // 更新时间
    private OffsetDateTime updatedAt;
}
