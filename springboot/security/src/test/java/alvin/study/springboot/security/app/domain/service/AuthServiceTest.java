package alvin.study.springboot.security.app.domain.service;

import alvin.study.springboot.security.IntegrationTest;
import alvin.study.springboot.security.builder.PermissionBuilder;
import alvin.study.springboot.security.builder.RoleBuilder;
import alvin.study.springboot.security.builder.RoleGrantBuilder;
import alvin.study.springboot.security.builder.RolePermissionBuilder;
import alvin.study.springboot.security.infra.entity.RoleGrantType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link AuthService} 类型
 */
class AuthServiceTest extends IntegrationTest {
    /**
     * 注入服务类对象
     */
    @Autowired
    private AuthService authService;

    /**
     * 测试 {@link AuthService#findPermissionsByUserId(Long)} 方法, 根据用户 ID
     * 获取该用户相关的角色和权限
     *
     * <p>
     * 获取的权限包括: 1. 该用户对应的角色相关的权限; 2. 该用户所在的组对应角色相关的权限
     * </p>
     */
    @Test
    @Transactional
    void findPermissionsByUserId_shouldSelectResult() {
        // 构建测试数据
        var roleAdmin = newBuilder(RoleBuilder.class).withName("A").create();
        var roleDba = newBuilder(RoleBuilder.class).withName("B").create();
        for (var resource : List.of("X1", "X2")) {
            for (var action : List.of("READ", "WRITE", "DELETE")) {
                var permission = newBuilder(PermissionBuilder.class)
                        .withName("X")
                        .withResource(resource)
                        .withAction(action)
                        .create();

                newBuilder(RolePermissionBuilder.class)
                        .withPermissionId(permission.getId())
                        .withRoleId(roleAdmin.getId())
                        .create();

                newBuilder(RolePermissionBuilder.class)
                        .withPermissionId(permission.getId())
                        .withRoleId(roleDba.getId())
                        .create();
            }
        }

        var roleNormal = newBuilder(RoleBuilder.class).withName("C").create();
        for (var resource : List.of("Y1", "Y2")) {
            for (var action : List.of("READ", "WRITE", "DELETE")) {
                var permission = newBuilder(PermissionBuilder.class)
                        .withName("Y")
                        .withResource(resource)
                        .withAction(action)
                        .create();

                newBuilder(RolePermissionBuilder.class)
                        .withPermissionId(permission.getId())
                        .withRoleId(roleAdmin.getId())
                        .create();
            }
        }

        for (var role : List.of(roleAdmin, roleDba, roleNormal)) {
            newBuilder(RoleGrantBuilder.class)
                    .withRoleId(role.getId())
                    .withUserOrGroupId(currentUser().getId())
                    .withType(RoleGrantType.USER)
                    .create();
        }

        clearSessionCache();

        // 查询权限
        var permissions = authService.findPermissionsByUserId(currentUser().getId());
        then(permissions).hasSize(15);

        // 确认当前用户有三个角色
        then(permissions)
                .filteredOn(p -> p.getAuthority().startsWith("ROLE_"))
                .extracting("authority")
                .contains("ROLE_A", "ROLE_B", "ROLE_C");

        // 确认用户的权限情况
        then(permissions)
                .filteredOn(p -> p.getAuthority().startsWith("X:"))
                .extracting("authority")
                .containsExactlyInAnyOrder(
                    "X:X1:READ",
                    "X:X1:WRITE",
                    "X:X1:DELETE",
                    "X:X2:READ",
                    "X:X2:WRITE",
                    "X:X2:DELETE");

        then(permissions)
                .filteredOn(p -> p.getAuthority().startsWith("Y:"))
                .extracting("authority")
                .containsExactlyInAnyOrder(
                    "Y:Y1:READ",
                    "Y:Y1:WRITE",
                    "Y:Y1:DELETE",
                    "Y:Y2:READ",
                    "Y:Y2:WRITE",
                    "Y:Y2:DELETE");
    }
}
