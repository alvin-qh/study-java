package alvin.study.testing.testcase.service;

import alvin.study.testing.testcase.model.User;

import java.util.List;
import java.util.Optional;

/**
 * 用于测试的用户服务接口
 */
public interface UserService {
    /**
     * 获取所有用户实体
     *
     * @return 用户实体对象列表
     */
    List<User> findAll();

    /**
     * 根据用户名称获取用户实体
     *
     * @param name 用户名称
     * @return 用户实体的 {@link Optional} 包装对象
     */
    Optional<User> findByName(String name);
}
