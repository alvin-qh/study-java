package alvin.study.springboot.kickstart.app.api.schema.payload;

import alvin.study.springboot.kickstart.app.api.schema.type.OrgType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建组织实体的返回结果
 *
 * <p>
 * 该类型为一个包装类型, 实际返回的结果为 {@link OrgType} 类型对象
 * </p>
 *
 * <p>
 * 对应的 schema 参考 {@code classpath:graphql/org.graphqls} 文件内容
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrgPayload {
    /**
     * 所创建的组织实体对象
     */
    private OrgType org;
}
