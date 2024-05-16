package alvin.study.guice.module.bean;

import lombok.Data;

/**
 * 用于演示注入的类型
 */
@Data
public class ModuleDemo {
    private final String value;

    public ModuleDemo(String value) {
        this.value = value;
    }
}
