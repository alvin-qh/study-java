package alvin.study.springboot.graphql.app.api.mutation;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import io.micrometer.common.lang.Nullable;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.model.MutationResult;
import alvin.study.springboot.graphql.app.service.OrgService;
import alvin.study.springboot.graphql.core.exception.ForbiddenException;
import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.entity.User;
import alvin.study.springboot.graphql.infra.entity.UserGroup;
import graphql.GraphQLContext;

@Controller
@RequiredArgsConstructor
public class OrgMutation {
    private final OrgService orgService;

    /**
     * 用户输入对象类型
     */
    static record OrgInput(String name) {
        public Org toEntity(OrgInput input, @Nullable Long id) {
            var org = new Org();
            org.setName(name);
            return org;
        }
    }

    private void checkUserPermission(GraphQLContext ctx) {
        // 获取当前登录用户
        var user = ctx.<User>get("user");
        if (user == null || user.getGroup() != UserGroup.ADMIN) {
            throw new ForbiddenException("need_admin_user");
        }
    }

    @MutationMapping
    public MutationResult<Org> createOrg(@Argument OrgInput input, GraphQLContext ctx) {
        checkUserPermission(ctx);

        var org = input.toEntity(input, null);
        orgService.create(org);
        return MutationResult.of(org);
    }

    @MutationMapping
    public MutationResult<Org> updateOrg(@Argument Long id, @Argument OrgInput input, GraphQLContext ctx) {
        checkUserPermission(ctx);

        var org = input.toEntity(input, id);
        orgService.update(org);
        return MutationResult.of(org);
    }

    @MutationMapping
    public boolean deleteOrg(@Argument Long id, GraphQLContext ctx) {
        checkUserPermission(ctx);

        return orgService.delete(id);
    }
}
