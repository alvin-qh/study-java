package alvin.study.guava.cache.model;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * 用于测试的用户实体对象
 */
public record User(@NotNull Long id, @NotNull String name) implements Serializable {
}
