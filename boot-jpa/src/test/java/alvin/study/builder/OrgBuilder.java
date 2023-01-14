package alvin.study.builder;

import java.util.concurrent.atomic.AtomicInteger;

import alvin.study.infra.entity.Org;

/**
 * 租户实体构建器类
 */
public class OrgBuilder extends Builder<Org> {
    private final static AtomicInteger SEQUENCE = new AtomicInteger();

    private String name = "Org" + SEQUENCE.incrementAndGet();

    /**
     * 设置租户名称
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
}
