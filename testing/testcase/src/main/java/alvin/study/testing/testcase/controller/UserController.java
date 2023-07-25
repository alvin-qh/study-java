package alvin.study.testing.testcase.controller;

import alvin.study.testing.testcase.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * 用于测试的 Controller 类型
 */
public class UserController {
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 注入服务类
    private final UserService userService;

    /**
     * 构造器, 注入服务类对象
     *
     * @param userService 服务类对象
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 根据用户姓名, 获取用户对象的 JSON 格式字符串
     *
     * @param name 用户姓名
     * @return 用户对象的 JSON 格式字符串
     */
    @SneakyThrows
    public String getUser(String name) {
        // 根据用户名获取用户对象
        var mayUser = userService.findByName(name);
        if (mayUser.isEmpty()) {
            return "";
        }

        // 将用户对象转换为 JSON 字符串
        return mayUser.map(user -> user.toJson(objectMapper)).orElse("");
    }
}
