package alvin.study.cache.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用于测试的用户实体对象
 */
@Getter
@RequiredArgsConstructor
public class User implements Serializable {
    private final Long id;
    private final String name;
}
