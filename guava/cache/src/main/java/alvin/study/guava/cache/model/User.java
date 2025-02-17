package alvin.study.guava.cache.model;

import java.io.Serializable;

import jakarta.annotation.Nonnull;

/**
 * 用于测试的用户实体对象
 */
public record User(@Nonnull Long id, @Nonnull String name) implements Serializable {}
