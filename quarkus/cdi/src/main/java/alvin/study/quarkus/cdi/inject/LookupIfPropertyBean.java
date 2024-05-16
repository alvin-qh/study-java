package alvin.study.quarkus.cdi.inject;

import lombok.Getter;

/**
 * 用于演示条件注入的 Bean 类型
 */
@Getter
public class LookupIfPropertyBean {
    private final String name;

    /**
     * 构造器
     *
     * @param name 属性参数
     */
    public LookupIfPropertyBean(String name) {
        this.name = name;
    }
}
