package alvin.study.springboot.springdoc.infra.entity;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 表示用户的实体对象
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class User implements Serializable {
    private final String username;
    private final String password;
}
