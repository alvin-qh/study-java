package alvin.study.springboot.graphql.app.api.mutation;

import alvin.study.springboot.graphql.app.api.query.UserGroup;

/**
 * 用户输入对象
 */
public record UserInput(String account, String password, UserGroup group) {}
