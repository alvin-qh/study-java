package alvin.study.springboot.jpa.builder;

import alvin.study.springboot.jpa.infra.entity.Org;

/**
 * 租户实体构建器类
 */
public class OrgBuilder extends Builder<Org> {
    private String name = FAKER.company().name();

    /**
     * 设置租户名称
     */
    public OrgBuilder name(String name) {
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
