package alvin.study.springboot.ds.app.domain.service;

import lombok.Getter;

/**
 * 配置不存在的异常
 */
@Getter
public class DataNotExistException extends RuntimeException {
    // 相关的组织代码
    private final Long id;

    /**
     * 构造器
     *
     * @param org 组织代码
     */
    public DataNotExistException(Long id) {
        super(String.format("data=\"%d\" not exist", id));
        this.id = id;
    }
}
