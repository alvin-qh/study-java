package alvin.study.springboot.kickstart.app.api.schema.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 删除组织实体的返回结果
 *
 * <p>
 * 对应的 schema 参考 {@code classpath:graphql/org.graphqls} 文件内容
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteOrgPayload {
    private boolean deleted;
}
