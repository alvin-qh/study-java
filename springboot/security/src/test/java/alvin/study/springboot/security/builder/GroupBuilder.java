package alvin.study.springboot.security.builder;

import alvin.study.springboot.security.infra.entity.Group;
import alvin.study.springboot.security.infra.mapper.GroupMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户组实体构建器类
 */
public class GroupBuilder implements Builder<Group> {
    private final static AtomicInteger SEQUENCE = new AtomicInteger();

    @Autowired
    private GroupMapper groupMapper;

    private String name = "Group" + SEQUENCE.incrementAndGet();

    /**
     * 设置组名称
     */
    public GroupBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Group build() {
        var group = new Group();
        group.setName(name);
        return group;
    }

    @Override
    public Group create() {
        var group = build();
        groupMapper.insert(group);
        return group;
    }
}
