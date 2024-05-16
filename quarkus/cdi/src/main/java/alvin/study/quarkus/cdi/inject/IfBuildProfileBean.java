package alvin.study.quarkus.cdi.inject;

import lombok.Getter;

@Getter
public class IfBuildProfileBean {
    private final String name;

    /**
     * 构造器
     *
     * @param name 属性参数
     */
    public IfBuildProfileBean(String name) {
        this.name = name;
    }
}
