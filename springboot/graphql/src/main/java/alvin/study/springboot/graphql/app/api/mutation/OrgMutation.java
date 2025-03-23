package alvin.study.springboot.graphql.app.api.mutation;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.context.ContextKey;
import alvin.study.springboot.graphql.app.model.MutationResult;
import alvin.study.springboot.graphql.app.service.OrgService;
import alvin.study.springboot.graphql.core.context.ContextHolder;
import alvin.study.springboot.graphql.core.exception.ForbiddenException;
import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.entity.User;
import alvin.study.springboot.graphql.infra.entity.UserGroup;

@Controller
@RequiredArgsConstructor
public class OrgMutation {
    private final OrgService orgService;

    /**
     * 用户输入对象类型
     */
    static record OrgInput(String name) {
        Org toEntity(Long id) {
            var org = new Org();
            org.setId(id);
            org.setName(name);
            return org;
        }
    }

    private void checkUserPermission() {
        var ctx = ContextHolder.getValue();

        // 获取当前登录用户
        var user = ctx.<User>get(ContextKey.KEY_USER);
        if (user == null || user.getGroup() != UserGroup.ADMIN) {
            throw new ForbiddenException("Only admin user allowed");
        }
    }

    @MutationMapping
    public MutationResult<Org> createOrg(@Argument OrgInput input) {
        checkUserPermission();

        var org = input.toEntity(null);
        orgService.create(org);
        return MutationResult.of(org);
    }

    @MutationMapping
    public MutationResult<Org> updateOrg(@Argument Long id, @Argument OrgInput input) {
        checkUserPermission();

        var org = input.toEntity(id);
        return MutationResult.of(orgService.update(org));
    }

    @MutationMapping
    public boolean deleteOrg(@Argument Long id) {
        checkUserPermission();

        return orgService.delete(id);
    }
}
