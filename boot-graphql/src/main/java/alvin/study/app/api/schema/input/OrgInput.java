package alvin.study.app.api.schema.input;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 变更组织信息的输入类
 *
 * <p>
 * 对应的 schema 参考 {@code classpath:graphql/org.graphqls} 文件内容
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgInput {
    /**
     * 组织名称
     */
    @NotBlank
    @Length(min = 3, max = 50)
    private String name;
}
