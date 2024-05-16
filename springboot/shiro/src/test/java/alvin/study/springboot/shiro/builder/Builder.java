package alvin.study.springboot.shiro.builder;

/**
 * 实体构建器超类
 */
public interface Builder<T> {
    /**
     * 创建实体对象 (非持久化)
     *
     * @return 未进行持久化操作的实体对象
     */
    T build();

    /**
     * 创建实体对象 (持久化)
     *
     * @return 以进行持久化操作的实体对象
     */
    T create();
}
