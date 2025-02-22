package alvin.study.springboot.graphql.app.api.query;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.service.OrgService;
import alvin.study.springboot.graphql.core.exception.InputException;
import alvin.study.springboot.graphql.infra.entity.Org;

/**
 * 组织查询类
 *
 * <p>
 * 对应 {@code classpath:graphql/org.graphqls} 中的定义
 * </p>
 */
@Controller
@RequiredArgsConstructor
public class OrgQuery {
    // 注入服务对象
    private final OrgService orgService;

    /**
     * 用户查询
     *
     * @param id 用户 id
     * @return 用户对象
     */
    @QueryMapping
    public Org org(@Argument String id) {
        // 查询组织
        return orgService.findById(Long.parseLong(id))
                .orElseThrow(() -> new InputException("Invalid org id"));
    }
}
