package alvin.study.springboot.ds.app.domain.service;

import lombok.Getter;

/**
 * 配置不存在的异常
 */
@Getter
public class ConfigNotExistException extends RuntimeException {
    // 相关的组织代码
    private final String org;

    /**
     * 构造器
     *
     * @param org 组织代码
     */
    public ConfigNotExistException(String org) {
        super(String.format("org=\"%s\" not exist", org));
        this.org = org;
    }
}
