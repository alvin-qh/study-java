package alvin.study.springboot.shiro.app.endpoint;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;

import org.assertj.core.api.InstanceOfAssertFactories;

import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.shiro.IntegrationTest;
import alvin.study.springboot.shiro.app.endpoint.model.HomeDto;
import alvin.study.springboot.shiro.builder.MenuBuilder;
import alvin.study.springboot.shiro.builder.PermissionBuilder;
import alvin.study.springboot.shiro.builder.RoleBuilder;
import alvin.study.springboot.shiro.builder.RoleGrantBuilder;
import alvin.study.springboot.shiro.builder.RolePermissionBuilder;
import alvin.study.springboot.shiro.infra.entity.RoleGrantType;
import alvin.study.springboot.shiro.util.collection.PathMap;

/**
 * 测试 {@link HomeController} 控制器类型
 */
class HomeControllerTest extends IntegrationTest {
    /**
     * 测试 {@link HomeController#getHome()} 方法, 且设置 {@code Authorization} HTTP 头属性
     *
     * <p>
     * 由于 {@link IntegrationTest#getJson(String, Object...)} 方法已经设置了当前登录用户用户的 JWT
     * token, 所以请求可以正常被处理并返回
     * </p>
     */
    @Test
    void getHome_shouldReturn200Ok() {
        try (var ignore = beginTx(false)) {
            // 添加正确的角色和权限
            addPermissions(currentUser(), "ANY_ONE", List.of());
        }

        // 访问 HOME 资源并设置 Authorization HTTP 头
        var resp = getJson("/")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(HomeDto.class).returnResult()
                .getResponseBody();

        then(resp).isNotNull()
                .extracting(HomeDto::getWelcome)
                .isEqualTo("Welcome " + currentUser().getAccount());
    }

    /**
     * 测试 {@link HomeController#getHome()} 方法, 但不设置 {@code Authorization} HTTP 头
     *
     * <p>
     * 此时无法通过 Spring Security 设置的登陆验证, 所以会返回 401 错误
     * </p>
     */
    @Test
    @Transactional
    void getHome_shouldReturn401IfNoAuthorizationHeader() {
        try (var ignore = beginTx(false)) {
            // 添加正确的角色和权限
            addPermissions(currentUser(), "ANY_ONE", List.of());
        }

        // 访问 HOME 资源, 但不设置 Authorization HTTP 头
        client().get()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /**
     * 测试 {@link HomeController#getHome()} 方法, 不设置 {@code Authorization} HTTP 头
     *
     * <p>
     * 此时无法通过角色权限要求, 所以会返回 403 错误
     * </p>
     */
    @Test
    void getHome_shouldReturn401IfRoleNotMatch() {
        try (var ignore = beginTx(false)) {
            // 添加正确的角色和权限
            addPermissions(currentUser(), "ADMIN", List.of());
        }

        // 访问 HOME 资源并设置 Authorization HTTP 头, 但因为角色不匹配, 会返回 403 错误
        getJson("/")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /**
     * 测试 {@link HomeController#getMenu()} 方法
     *
     * <p>
     * 测试假设当前用户具备 "USER" 角色以及 "COMMON:MENU:READ" 和 "MENU:I:READ" 权限
     * </p>
     *
     * <p>
     * 整个菜单的结构如下:
     *
     * <pre>
     * root
     *  ├─ I                    &lt;role: USER&gt;
     *  │  ├─ I-I               &lt;role: USER&gt;
     *  │  ├─ I-II              &lt;permission: MENU:I:READ&gt;
     *  │  └─ I-III             &lt;permission: MENU:I:READ&gt;
     *  └─ II                   &lt;role: USER&gt;
     *     ├─ II-I              &lt;no-control&gt;
     *     ├─ II-II             &lt;no-control&gt;
     *     │   ├─ II-II-I       &lt;permission: MENU:II:READ&gt;
     *     │   ├─ II-II-II      &lt;permission: MENU:I:READ&gt;
     *     │   └─ II-II-III     &lt;permission: MENU:II:READ&gt;
     *     └─ II-III            &lt;permission: MENU:II:READ&gt;
     * </pre>
     * <p>
     * 经过权限控制后, 用户得到的菜单如下:
     *
     * <pre>
     * root
     *  ├─ I                    &lt;role: USER&gt;
     *  │  ├─ I-I               &lt;role: USER&gt;
     *  │  ├─ I-II              &lt;permission: MENU:I:READ&gt;
     *  │  └─ I-III             &lt;permission: MENU:I:READ&gt;
     *  └─ II                   &lt;role: USER&gt;
     *     ├─ II-I              &lt;no-control&gt;
     *     └─ II-II             &lt;no-control&gt;
     *         └─ II-II-II      &lt;permission: MENU:I:READ&gt;
     * </pre>
     *
     * </p>
     */
    @Test
    void getMenu_shouldReturn200Ok() {
        // 构建测试数据
        try (var ignore = beginTx(false)) {
            var role = newBuilder(RoleBuilder.class).withName("USER").create();

            // 随机将角色授予用户或用户所在的组
            if ((int) (Math.random() * 2) == 0) {
                newBuilder(RoleGrantBuilder.class)
                        .withRoleId(role.getId())
                        .withType(RoleGrantType.USER)
                        .withUserOrGroupId(currentUser().getId())
                        .create();
            } else {
                newBuilder(RoleGrantBuilder.class)
                        .withRoleId(role.getId())
                        .withType(RoleGrantType.GROUP)
                        .withUserOrGroupId(adminGroup().getId())
                        .create();
            }

            // 为角色设置权限
            var permission = newBuilder(PermissionBuilder.class)
                    .withName("COMMON")
                    .withResource("MENU")
                    .withAction("READ")
                    .create();

            newBuilder(RolePermissionBuilder.class)
                    .withPermissionId(permission.getId())
                    .withRoleId(role.getId())
                    .create();

            var permissionI = newBuilder(PermissionBuilder.class)
                    .withName("MENU")
                    .withResource("I")
                    .withAction("READ")
                    .create();

            newBuilder(RolePermissionBuilder.class)
                    .withPermissionId(permissionI.getId())
                    .withRoleId(role.getId())
                    .create();

            var permissionII = newBuilder(PermissionBuilder.class)
                    .withName("MENU")
                    .withResource("II")
                    .withAction("READ")
                    .create();

            // 构建菜单数据并为每个菜单设置角色和权限情况
            var menu1 = newBuilder(MenuBuilder.class).withText("I").withRoleId(role.getId()).create();
            newBuilder(MenuBuilder.class)
                    .withText("I-I")
                    .withParentId(menu1.getId())
                    .create();
            newBuilder(MenuBuilder.class).withText("I-II")
                    .withParentId(menu1.getId())
                    .withPermissionId(permissionI.getId())
                    .create();
            newBuilder(MenuBuilder.class).withText("I-III")
                    .withParentId(menu1.getId())
                    .withPermissionId(permissionI.getId())
                    .create();

            var menu2 = newBuilder(MenuBuilder.class)
                    .withText("II")
                    .withRoleId(role.getId())
                    .create();
            newBuilder(MenuBuilder.class)
                    .withText("II-I")
                    .withParentId(menu2.getId())
                    .create();

            var menu2_2 = newBuilder(MenuBuilder.class)
                    .withText("II-II")
                    .withParentId(menu2.getId())
                    .create();
            newBuilder(MenuBuilder.class)
                    .withText("II-II-I")
                    .withParentId(menu2_2.getId())
                    .withPermissionId(permissionII.getId())
                    .create();
            newBuilder(MenuBuilder.class)
                    .withText("II-II-II")
                    .withParentId(menu2_2.getId())
                    .withPermissionId(permissionI.getId())
                    .create();
            newBuilder(MenuBuilder.class)
                    .withText("II-II-III")
                    .withParentId(menu2_2.getId())
                    .withPermissionId(permissionII.getId())
                    .create();

            newBuilder(MenuBuilder.class)
                    .withText("II-III")
                    .withParentId(menu2.getId())
                    .withPermissionId(permissionII.getId())
                    .create();
        }

        // 访问 3 次以查看缓存的使用情况
        for (var i = 0; i < 3; i++) {
            // 访问服务器获取菜单, 并确认该用户智能获取到角色权限允许的那部分菜单项
            var json = getJson("/menu")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(PathMap.class).returnResult()
                    .getResponseBody();

            then(json).isNotNull();

            then((Object) json.getByPath("items")).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(2);
            then((Object) json.getByPath("items[0].text")).isEqualTo("I");
            then((Object) json.getByPath("items[0].items")).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(3);
            then((Object) json.getByPath("items[0].items[0].text")).isEqualTo("I-I");
            then((Object) json.getByPath("items[0].items[1].text")).isEqualTo("I-II");
            then((Object) json.getByPath("items[0].items[2].text")).isEqualTo("I-III");

            then((Object) json.getByPath("items[1].text")).isEqualTo("II");
            then((Object) json.getByPath("items[1].items")).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(2);
            then((Object) json.getByPath("items[1].items[0].text")).isEqualTo("II-I");
            then((Object) json.getByPath("items[1].items[1].text")).isEqualTo("II-II");

            then((Object) json.getByPath("items[1].items[1].items")).asInstanceOf(InstanceOfAssertFactories.LIST)
                    .hasSize(1);
            then((Object) json.getByPath("items[1].items[1].items[0].text")).isEqualTo("II-II-II");
        }
    }
}
