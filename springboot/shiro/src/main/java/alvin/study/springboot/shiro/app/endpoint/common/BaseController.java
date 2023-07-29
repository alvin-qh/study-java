package alvin.study.springboot.shiro.app.endpoint.common;

import alvin.study.springboot.shiro.infra.entity.User;
import org.apache.shiro.util.ThreadContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

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
     * 通过 {@link ThreadContext#getSubject()} 获取当前线程上下文存储的认证对象, 进一步通过
     * {@link org.apache.shiro.subject.Subject#getPrincipals()
     * Subject.getPrincipals()} 方法获取登录信息
     * </p>
     *
     * @return 当前登录的用户对象
     */
    protected static User currentUser() {
        var subject = ThreadContext.getSubject();
        if (subject == null) {
            return null;
        }
        return (User) subject.getPrincipals().getPrimaryPrincipal();
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
