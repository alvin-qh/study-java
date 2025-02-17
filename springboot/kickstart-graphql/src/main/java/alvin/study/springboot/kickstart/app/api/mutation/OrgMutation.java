package alvin.study.springboot.kickstart.app.api.mutation;

import alvin.study.springboot.kickstart.app.api.common.BaseMutation;
import alvin.study.springboot.kickstart.app.api.schema.input.OrgInput;
import alvin.study.springboot.kickstart.app.api.schema.payload.CreateOrgPayload;
import alvin.study.springboot.kickstart.app.api.schema.payload.DeleteOrgPayload;
import alvin.study.springboot.kickstart.app.api.schema.payload.UpdateOrgPayload;
import alvin.study.springboot.kickstart.app.api.schema.type.OrgType;
import alvin.study.springboot.kickstart.app.service.OrgService;
import alvin.study.springboot.kickstart.core.exception.InputException;
import alvin.study.springboot.kickstart.core.graphql.annotation.Mutation;
import alvin.study.springboot.kickstart.infra.entity.Org;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * 用户实体变更类型
 *
 * <p>
 * Kickstart 框架定义的变更类需要包含如下几个条件:
 * <ul>
 * <li>
 * 实现 {@link graphql.kickstart.tools.GraphQLMutationResolver
 * GraphQLMutationResolver} 接口
 * </li>
 * <li>
 * 具备 {@link org.springframework.stereotype.Component @Component} 注解, 本例中用
 * {@link Mutation @Mutation} 注解替代
 * </li>
 * <li>
 * 在 {@code classpath:graphql/org.graphqls} 中定义查询的 Mutation schema
 * </li>
 * </ul>
 * </p>
 */
@Mutation
@Validated
@RequiredArgsConstructor
public class OrgMutation extends BaseMutation {
    // 注入组织服务类对象
    private final OrgService orgService;

    /**
     * 创建组织实体
     *
     * @param input 组织信息输入对象
     * @return 组织实体创建结果
     */
    public CreateOrgPayload createOrg(@Valid OrgInput input) {
        // OrgInput => Org
        var org = map(input, Org.class);

        // 存储用户实体
        orgService.create(org);

        // Org => CreateOrgPayload
        return new CreateOrgPayload(map(org, OrgType.class));
    }

    /**
     * 更新用户实体
     *
     * @param id    要更新的组织实体 id
     * @param input 组织信息输入对象
     * @return 组织实体更新结果
     */
    public UpdateOrgPayload updateOrg(String id, @Valid OrgInput input) {
        // OrgInput => Org
        var org = map(input, Org.class);

        // 更新组织实体
        return orgService.update(Long.parseLong(id), org)
                .map(u -> new UpdateOrgPayload(map(u, OrgType.class)))
                .orElseThrow(() -> new InputException("Update org not found"));
    }

    /**
     * 删除组织实体
     *
     * @param id 组织 ID
     * @return 组织实体删除结果
     */
    public DeleteOrgPayload deleteOrg(String id) {
        return new DeleteOrgPayload(orgService.delete(Long.parseLong(id)));
    }
}
