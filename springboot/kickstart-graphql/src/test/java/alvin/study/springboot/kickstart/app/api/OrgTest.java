package alvin.study.springboot.kickstart.app.api;

import alvin.study.springboot.kickstart.IntegrationTest;
import alvin.study.springboot.kickstart.app.api.mutation.OrgMutation;
import alvin.study.springboot.kickstart.app.api.query.OrgQuery;
import alvin.study.springboot.kickstart.app.api.schema.input.OrgInput;
import alvin.study.springboot.kickstart.builder.OrgBuilder;
import alvin.study.springboot.kickstart.infra.entity.Org;
import alvin.study.springboot.kickstart.util.collection.PathMap;
import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

/**
 * 测试 {@link OrgQuery OrgQuery} 和
 * {@link OrgMutation OrgMutation}
 * 类型
 *
 * <p>
 * 相关的查询 Graphql 语句在 {@code classpath:graphql/test-org.graphql} 文件中定义
 * </p>
 */
class OrgTest extends IntegrationTest {
    /**
     * 测试 {@link OrgQuery#org(String)
     * OrgQuery.org(String)} 方法, 根据 id 查询用户信息
     */
    @Test
    void org_shouldQueryById() throws IOException {
        // 创建测试数据
        Org org;
        try (var ignore = beginTx(false)) {
            org = newBuilder(OrgBuilder.class).create();
        }

        // 设置查询参数
        var vars = valueToTree("id", org.getId());

        // 确认查询解雇符合预期
        graphql("test-org", "queryOrg", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.org")
                .as(PathMap.class)
                .matches(v -> v.get("id").equals(org.getId().toString()), "id")
                // 由于 @uppercase 处理器的作用, 查询到的 name 属性值为大写
                .matches(v -> v.get("name").equals(org.getName().toUpperCase()), "name");
    }

    /**
     * 测试 {@link OrgMutation#createOrg(OrgInput)
     * UserMutation.createOrg(OrgInput)} 方法, 创建一个组织实体
     */
    @Test
    void createOrg_shouldMutationExecute() throws IOException {
        // 构建输入参数对象
        var input = OrgInput.builder()
                .name("alvin.edu")
                .build();

        // 构建变更参数
        var vars = valueToTree("input", input);

        // 执行变更操作并确认结果
        graphql("test-org", "createOrg", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.createOrg.org")
                .as(PathMap.class)
                .matches(v -> !Strings.isNullOrEmpty(v.getAs("id")), "id")
                // 由于 @uppercase 处理器的作用, 查询到的 name 属性值为大写
                .matches(v -> v.get("name").equals(input.getName().toUpperCase()), "name");
    }

    /**
     * 测试
     * {@link OrgMutation#updateOrg(String, OrgInput)
     * UserMutation.updateOrg(String, OrgInput)} 方法, 更新一个组织实体
     */
    @Test
    void updateOrg_shouldMutationExecute() throws IOException {
        // 创建待更新组织实体
        Org org;
        try (var ignore = beginTx(false)) {
            org = newBuilder(OrgBuilder.class).create();
        }

        // 构建变更输入对象
        var input = OrgInput.builder()
                .name("alvin.edu.update")
                .build();

        // 构建变更参数
        var vars = mapToTree(Map.of(
            "id", org.getId(),
            "input", input));

        // 执行变更操作并确认结果
        graphql("test-org", "updateOrg", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.updateOrg.org")
                .as(PathMap.class)
                .matches(v -> v.get("id").equals(org.getId().toString()), "id")
                // 由于 @uppercase 处理器的作用, 查询到的 name 属性值为大写
                .matches(v -> v.get("name").equals(input.getName().toUpperCase()), "name");
    }

    /**
     * 测试
     * {@link OrgMutation#deleteOrg(String)
     * OrgMutation.deleteOrg(String)} 方法, 删除一个组织实体
     */
    @Test
    void deleteOrg_shouldMutationExecute() throws IOException {
        // 创建待删除组织实体
        Org org;
        try (var ignore = beginTx(false)) {
            org = newBuilder(OrgBuilder.class).create();
        }

        // 构建删除参数
        var vars = valueToTree("id", org.getId());

        // @formatter:off
        // 执行变更操作并确认结果
        graphql("test-org", "deleteOrg", vars)
                .assertThatNoErrorsArePresent()
                .assertThatField("$.data.deleteOrg")
                .as(PathMap.class)
                .matches(v -> v.get("deleted").equals(true), "deleted");
        // @formatter:on
    }
}
