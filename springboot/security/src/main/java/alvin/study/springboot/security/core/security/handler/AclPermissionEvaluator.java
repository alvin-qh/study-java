package alvin.study.springboot.security.core.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import jakarta.annotation.Nonnull;

import java.io.Serializable;

/**
 * 进行权限匹配的处理类型
 *
 * <p>
 * 通过
 * {@link org.springframework.security.access.prepost.PreAuthorize @PreAuthorize}
 * 注解调用权限匹配, 例如 {@code @PreAuthorize("hasPermission('COMMON:MENU', 'READ')")}
 * </p>
 */
@Slf4j
public class AclPermissionEvaluator implements PermissionEvaluator {
    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * 检查是否具备指定的角色
     *
     * @param authentication {@link Authentication} 对象, 具备当前用户信息和当前用户的权限列表
     * @param role           角色名称
     * @return 是否匹配
     */
    public boolean hasRole(@Nonnull Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
            .anyMatch(it -> checkIfRoleMatch(role, it.getAuthority()));
    }

    /**
     * 匹配角色名称是否匹配
     *
     * @param given    所给的角色名称
     * @param expected 期待的角色名称, 以 {@code "ROLE_"} 开始
     * @return 是否匹配
     */
    private boolean checkIfRoleMatch(String given, @Nonnull String expected) {
        if (!expected.startsWith(ROLE_PREFIX)) {
            return false;
        }

        var startIndex = ROLE_PREFIX.length();
        var index = 0;
        while (startIndex < expected.length() && index < given.length()) {
            if (given.charAt(index) != expected.charAt(startIndex)) {
                return false;
            }

            startIndex++;
            index++;
        }

        return startIndex == expected.length() && index == given.length();
    }

    /**
     * 检查是否具备指定的权限
     *
     * <p>
     * 注意 {@link Authentication#getAuthorities()} 返回的权限列表需要依次按照 {@code name},
     * {@code resource} 和 {@code action} 进行排序
     * </p>
     *
     * @param authentication     {@link Authentication} 对象, 具备当前用户信息和当前用户的权限列表
     * @param targetDomainObject 要访问的资源目标
     * @param permission         对要访问资源的权限需求
     * @return 权限匹配则返回 {@code true}, 否则返回 {@code false}
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // 确认 targetDomainObject 参数为字符串类型
        if (!(targetDomainObject instanceof String)) {
            log.debug("Invalid type of targetDomainObject, expected String");
            return false;
        }

        // 确认 permission 参数为字符串类型
        if (!(permission instanceof String)) {
            log.debug("Invalid type of permission, expected String");
            return false;
        }

        // 将 targetDomainObject 字符串根据 ":" 分隔为两部分
        var domains = ((String) targetDomainObject).split(":", 2);
        if (domains.length != 2) {
            log.debug("Invalid format of targetDomainObject, expected \"<name>:<resource>\"");
            return false;
        }

        boolean isNameMatched = false;
        boolean isResourceMatched = false;

        // 遍历权限列表, 尝试匹配需求的权限
        for (var auth : authentication.getAuthorities()) {
            // 获取完整权限字符串
            var fullPermission = auth.getAuthority();

            // 检查权限名称是否匹配
            if (checkIsNameMatched(domains[0], fullPermission)) {
                isNameMatched = true;

                // 检查权限资源是否匹配
                if (checkIsResourceMatched(domains[0], domains[1], fullPermission)) {
                    isResourceMatched = true;

                    // 检查权限行为是否匹配
                    if (checkIsActionMatched(domains[0], domains[1], (String) permission, fullPermission)) {
                        return true;
                    }
                } else {
                    // 如果已经匹配权限资源, 则除非不匹配权限名称, 则之后不会再有可匹配的权限资源, 返回匹配失败
                    if (isResourceMatched && !(domains[0].equals("*"))) {
                        return false;
                    }
                }
            } else {
                // 如果已经匹配权限名称, 则之后不会再有可匹配的权限名称, 返回匹配失败
                if (isNameMatched) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 检查权限名称是否匹配
     *
     * @param name       权限名称
     * @param permission 完整的权限字符串
     * @return 是否匹配
     */
    private boolean checkIsNameMatched(@Nonnull String name, String permission) {
        if (name.equals("*")) {
            // 如果要求的权限名称为通配符, 则返回匹配
            return true;
        }

        // 比较权限名称是否被完整的权限字符串包含
        return checkIfContains(name, permission, 0);
    }

    /**
     * 检查权限资源是否匹配
     *
     * @param name       权限名称
     * @param resource   权限资源
     * @param permission 完整的权限字符串
     * @return 是否匹配
     */
    private boolean checkIsResourceMatched(String name, String resource, String permission) {
        if (resource.equals("*")) {
            return true;
        }

        // 比较权限资源是否被完整的权限字符串包含
        return checkIfContains(resource, permission, name.length() + 1);
    }

    /**
     * 检查权限行为是否匹配
     *
     * @param name       权限名称
     * @param resource   权限资源
     * @param action     权限行为
     * @param permission 完整的权限字符串
     * @return 是否匹配
     */
    private boolean checkIsActionMatched(String name, String resource, @Nonnull String action, String permission) {
        if (action.equals("*")) {
            return true;
        }

        // 比较权限行为是否被完整的权限字符串包含
        return checkIfContains(action, permission, name.length() + resource.length() + 2);
    }

    /**
     * 检查所给的字符串是否被完整的权限字符串包含
     *
     * @param given          所给的权限某个部分 (权限名称, 权限资源和权限行为)
     * @param fullPermission 完整的权限字符串 (权限名称:权限资源:权限行为)
     * @param fromIndex      从完整权限字符串开始匹配的位置
     * @return 是否匹配成功
     */
    private boolean checkIfContains(String given, @Nonnull String fullPermission, int fromIndex) {
        var n = 0;
        while (fromIndex < fullPermission.length() && n < given.length()) {
            if (given.charAt(n) != fullPermission.charAt(fromIndex)) {
                return false;
            }
            fromIndex++;
            n++;
        }
        return n == given.length() && (fromIndex == fullPermission.length() || fullPermission.charAt(fromIndex) == ':');
    }

    @Override
    public boolean hasPermission(
        Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new UnsupportedOperationException("Use hasPermission(String, String) instead of");
    }
}
