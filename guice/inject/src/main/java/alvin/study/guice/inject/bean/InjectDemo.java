package alvin.study.guice.inject.bean;

import lombok.Data;

/**
 * 用于测试注入的类型
 */
@Data
public class InjectDemo {
    private final String value;

    /**
     * 无参构造器
     */
    public InjectDemo() {
        this.value = "defaultValue";
    }

    /**
     * 构造器, 设置一个 {@code value} 值
     *
     * @param value 字符串值
     */
    public InjectDemo(String value) {
        this.value = value;
    }
}
