package alvin.study.springboot.graphql.builder;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.mapper.OrgMapper;

/**
 * 组织实体构建器类
 */
public class OrgBuilder extends Builder<Org> {
    private final static AtomicInteger SEQUENCE = new AtomicInteger();

    @Autowired
    private OrgMapper mapper;

    private String name = "Org" + SEQUENCE.incrementAndGet();

    /**
     * 设置组织名称
     *
     * @param name 组织名称
     * @return 当前对象
     */
    public OrgBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Org build() {
        var org = new Org();
        org.setName(name);
        return org;
    }

    @Override
    public Org create() {
        var org = build();
        mapper.insert(org);
        return org;
    }
}
