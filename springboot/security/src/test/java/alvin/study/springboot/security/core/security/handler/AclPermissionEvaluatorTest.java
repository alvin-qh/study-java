package alvin.study.springboot.security.core.security.handler;

import alvin.study.springboot.security.core.security.auth.NameAndPasswordAuthenticationToken;
import alvin.study.springboot.security.infra.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link AclPermissionEvaluator} 类型, 对用于具备的角色或权限进行匹配
 */
class AclPermissionEvaluatorTest {
    // 实例化对象
    private final AclPermissionEvaluator evaluator = new AclPermissionEvaluator();

    /**
     * 测试
     * {@link AclPermissionEvaluator#hasRole(org.springframework.security.core.Authentication, String)
     * AclPermissionEvaluator.hasRole(Authentication, String)} 方法, 匹配角色
     */
    @Test
    void hasRole_shouldCheckRole() {
        var token = new NameAndPasswordAuthenticationToken(
            new User(),
            "",
            List.of(
                new SimpleGrantedAuthority("ROLE_A"),
                new SimpleGrantedAuthority("ROLE_B")));

        var r = evaluator.hasRole(token, "A");
        then(r).isTrue();

        r = evaluator.hasRole(token, "B");
        then(r).isTrue();
    }

    /**
     * 测试
     * {@link AclPermissionEvaluator#hasPermission(org.springframework.security.core.Authentication, Object, Object)
     * AclPermissionEvaluator.hasPermission(Authentication, Object, Object)} 方法,
     * 匹配权限
     */
    @Test
    void hasPermission_shouldCheckPermissions() {
        var token = new NameAndPasswordAuthenticationToken(
            new User(),
            "",
            List.of(
                new SimpleGrantedAuthority("A:X:R"),
                new SimpleGrantedAuthority("A:Y:R"),
                new SimpleGrantedAuthority("A:Z:W"),
                new SimpleGrantedAuthority("B:X:W"),
                new SimpleGrantedAuthority("B:Y:W"),
                new SimpleGrantedAuthority("C:Z:W")));

        var r = evaluator.hasPermission(token, "A:X", "R");
        then(r).isTrue();

        r = evaluator.hasPermission(token, "*:*", "*");
        then(r).isTrue();

        r = evaluator.hasPermission(token, "*:*", "R");
        then(r).isTrue();

        r = evaluator.hasPermission(token, "A:*", "W");
        then(r).isTrue();

        r = evaluator.hasPermission(token, "*:X", "W");
        then(r).isTrue();

        r = evaluator.hasPermission(token, "*:Z", "R");
        then(r).isFalse();

        r = evaluator.hasPermission(token, "B:*", "R");
        then(r).isFalse();

        r = evaluator.hasPermission(token, "B:Y", "*");
        then(r).isTrue();
    }
}
