package alvin.study.springboot.mybatis.builder;

import alvin.study.springboot.mybatis.infra.entity.Org;
import alvin.study.springboot.mybatis.infra.mapper.OrgMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;

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
