package alvin.study.misc.jackson.pojo;

import alvin.study.misc.jackson.pojo.common.SimpleDate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 本例演示了最基本的 JSON 序列化和反序列
 *
 * <p>
 * 序列化 JSON 时, Jackson 会按照 POJO 类型的字段生成 JSON 字段. 可以通过 {@link JsonProperty @JsonProperty}
 * 注解设置 JSON 字段的名称；可以通过 {@link JsonIgnore @JsonIgnore} 注解标记某个类字段不参与 JSON 序列化和反序列化
 * </p>
 *
 * <p>
 * 反序列化 JSON 时, Jackson 有两种方式:
 * <ol>
 * <li>
 * 通过一个无参构造器产生 pojo 对象后, 根据对象属性和 JSON 字段的对应情况反序列化对象
 * </li>
 * <li>
 * 通过 {@link com.fasterxml.jackson.annotation.JsonCreator @JsonCreator} 注解指定一个用于 JSON 反序列化的构造器,
 * 并通过 {@link JsonProperty @JsonProperty} 注解在构造器中指定参数和 JSON 字段的对应关系
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * 另外, {@link JsonRootName @JsonRootName} 注解表示序列化和反序列化的 JSON, 必须包含一个 {@code root} 字段
 *
 * <pre>
 * {
 *   "root": {
 *     ...
 *   }
 * }
 * </pre>
 * </p>
 * <p>
 * 必须在配置 {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper} 对象时, 指定
 * {@code mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true)} 和
 * {@code mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE)}, 才能正确的序列化和反序列化
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class Staff {
    private Long id;

    // 指定特殊的属性名称
    @JsonProperty("staffName")
    private String name;

    private String gender;
    private SimpleDate birthday;

    // 指定该字段不参与 JSON 序列化和反序列化
    @JsonIgnore
    private Boolean valid = Boolean.TRUE;

    /**
     * 无参构造器, 让 Jackson 可以构造出 POJO 对象.
     *
     * <p>
     * 由于 Jackson 是通过反射方式调用该构造器, 所以该构造器可以设置为任意访问修饰符
     * </p>
     */
    Staff() {
    }

    /**
     * 该构造器用于正常途径构造一个 POJO 对象
     *
     * @param id       {@code id} 属性
     * @param name     名称属性
     * @param gender   性别属性
     * @param birthday 生日属性
     */
    public Staff(Long id, String name, String gender, SimpleDate birthday, boolean valid) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.valid = valid;
    }
}
