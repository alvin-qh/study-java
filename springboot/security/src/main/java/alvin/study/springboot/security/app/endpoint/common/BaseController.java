package alvin.study.springboot.security.app.endpoint.common;

import alvin.study.springboot.security.infra.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 所有 Controller 类型的超类
 */
public class BaseController {
    /**
     * 注入 {@link ModelMapper} 类型, 用于对象类型转换
     */
    @Autowired
    private ModelMapper modelMapper;

    /**
     * 获取当前登录的用户对象
     *
     * <p>
     * 通过 {@link SecurityContextHolder} 获取
     * {@link org.springframework.security.core.context.SecurityContext
     * SecurityContext} 上下文对象, 可以从中获取到
     * {@link org.springframework.security.core.Authentication Authentication} 对象,
     * 并进一步通过 {@link org.springframework.security.core.Authentication#getPrincipal()
     * Authentication.getPrincipal()} 方法获取到登录的用户对象
     * </p>
     *
     * @return 当前登录的用户对象
     */
    protected static User currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }

        return (User) auth.getPrincipal();
    }

    /**
     * 将原对象按照所给类型转为目标对象类型
     *
     * @param <T>        目标对象类型
     * @param src        原对象实例
     * @param targetType 目标对象类型
     * @return 目标类型对象
     */
    protected <T> T mapper(Object src, Class<T> targetType) {
        return modelMapper.map(src, targetType);
    }
}
