package alvin.study.guava.cache.model;

import java.io.Serializable;

/**
 * 用于测试的用户实体对象
 */
public record User(Long id, String name) implements Serializable {}
