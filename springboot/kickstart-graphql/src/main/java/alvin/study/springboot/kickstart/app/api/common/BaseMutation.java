package alvin.study.springboot.kickstart.app.api.common;

import graphql.kickstart.tools.GraphQLMutationResolver;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 所有变更 schema 类型的超类
 *
 * <p>
 * Kickstart 框架要求所有的变更 schema 类型要实现 {@link GraphQLMutationResolver} 接口
 * </p>
 */
public abstract class BaseMutation implements GraphQLMutationResolver {
    @Autowired
    private ModelMapper modelMapper;

    /**
     * 转换对象类型
     *
     * @param <R>        目标对象类型
     * @param src        源对象
     * @param targetType 目标对象类型 {@link Class} 对象
     * @return 目标对象类型
     */
    protected <R> R map(Object src, Class<R> targetType) {
        return modelMapper.map(src, targetType);
    }
}
