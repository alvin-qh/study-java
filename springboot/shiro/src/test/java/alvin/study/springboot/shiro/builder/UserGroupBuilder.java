package alvin.study.springboot.shiro.builder;

import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.springboot.shiro.infra.entity.UserGroup;
import alvin.study.springboot.shiro.infra.mapper.UserGroupMapper;

/**
 * 用户组关系实体构建器类
 */
public class UserGroupBuilder implements Builder<UserGroup> {
    @Autowired
    private UserGroupMapper userGroupMapper;

    private Long userId;
    private Long groupId;

    /**
     * 设置用户 id
     */
    public UserGroupBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    /**
     * 设置组 id
     */
    public UserGroupBuilder withGroupId(Long groupId) {
        this.groupId = groupId;
        return this;
    }

    @Override
    public UserGroup build() {
        var userGroup = new UserGroup();
        userGroup.setUserId(userId);
        userGroup.setGroupId(groupId);
        return userGroup;
    }

    @Override
    public UserGroup create() {
        var userGroup = build();
        userGroupMapper.insert(userGroup);
        return userGroup;
    }
}
