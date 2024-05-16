package alvin.study.misc.jackson.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 本例演示了在 POJO 类中, 通过 {@link JsonGetter @JsonGetter} 注解添加 JSON 字段.
 *
 * <p>
 * 本例中, 通过 {@link JsonIgnoreProperties @JsonIgnoreProperties} 注解忽略掉了指定的 POJO 对象属性不被序列化, 同时通过
 * {@link JsonGetter @JsonGetter} 注解指定一个 getter 方法作为非字段的 JSON 属性. 从而达到自定义 JSON 序列化和反序列化的目标
 * </p>
 *
 * <p>
 * 注意, 如果有一个 JSON 字段和 POJO 字段无法直接对应, 可以通过 {@link JsonGetter @JsonGetter} 指定一个 getter 方法来对应,
 * 同时需要提供对应的注解为 {@link JsonSetter @JsonSetter} 的 setter 方法或在 {@link JsonCreator @JsonCreator}
 * 构造器中处理额外的 JSON 字段对应情况
 * </p>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(value = { "firstName", "lastName" }, ignoreUnknown = true)
public class Person {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String gender;
    private final LocalDate birthday;

    /**
     * Jackson 将通过此构造器, 通过 JSON 字段和构造器参数的映射完成 JSON 到对象的转换
     */
    @JsonCreator
    public Person(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("gender") String gender,
        @JsonProperty("birthday") LocalDate birthday) {
        this.id = id;
        this.gender = gender;
        this.birthday = birthday;

        // 将输入的名字拆分为 firstName 和 lastName
        var r = splitName(name);
        this.firstName = r[0];
        this.lastName = r[1];
    }

    /**
     * 将输入的名字拆分为 {@code firstName} 和 {@code lastName}
     *
     * @param name 名称字符串
     * @return 分隔后的名称
     */
    private String[] splitName(String name) {
        var r = new String[]{ "", "" };
        // 将名字通过 "." 字符分割为两部分
        var s = name.split("\\.", 2);
        if (s != null) {
            if (s.length > 0) {
                r[0] = s[0]; // 设置 firstName 结果
            }
            if (s.length > 1) {
                r[1] = s[1]; // 设置 lastName 属性
            }
        }
        return r;
    }

    /**
     * 获取 {@code name} 属性
     *
     * <p>
     * 当前 POJO 类型禁止序列化 (和反序列化) {@code firstName} 和 {@code lastName} 属性, 用 {@code name}
     * 属性来替代. {@link JsonGetter @JsonGetter} 注解表示
     * </p>
     */
    @JsonGetter("name")
    public String getName() {
        return String.format("%s.%s", this.firstName, this.lastName);
    }
}
