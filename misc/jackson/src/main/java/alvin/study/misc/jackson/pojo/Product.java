package alvin.study.misc.jackson.pojo;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 本例演示了如何使用 {@link JsonCreator @JsonCreator} 注解设置反序列化入口.
 *
 * <p>
 * 如果不设置无参构造器, 则需要一个注解为 {@link JsonCreator @JsonCreator} 的构造器作为 JSON 反序列化时的入口. Jackson
 * 会根据该构造器以及构造器参数上的 {@link JsonProperty @JsonProperty} 注解, 对 JSON 字段和构造器参数进行映射, 从而对 JSON
 * 字符串进行反序列化. 否则就必须通过 {@link Staff} 类中提供的方式, 提供一个无参构造器
 * </p>
 */
@Getter
@ToString
@JsonRootName("product")
@EqualsAndHashCode(callSuper = false)
public class Product {
    private final Long id;
    private final String name;
    private final String serialNo;
    private final Instant date;

    /**
     * 标记为 {@link JsonCreator @JsonCreator} 的构造器, 用于 JSON 反序列化时指定 JSON 字段和构造器参数之间的关系
     */
    @JsonCreator
    public Product(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("serialNo") String serialNo,
            @JsonProperty("date") Instant date) {
        this.id = id;
        this.name = name;
        this.serialNo = serialNo;
        this.date = date;
    }
}
