package alvin.study.misc.jackson.pojo;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.common.base.Strings;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 本例演示了 JSON 字段过滤器, 即通过 {@link JsonFilter @JsonFilter} 注解指定过滤器,
 * 控制 POJO 类到 JSON 转换的字段.
 *
 * <p>
 * 要使用过滤器, 首先得注册一个过滤器, 参照
 * {@link tools.jackson.databind.json.JsonMapper.Builder#filterProvider(tools.jackson.databind.ser.FilterProvider)
 * JsonMapper.Builder.filterProvider(FilterProvider)} 方法, 注册过滤器后,
 * 即可通过 {@link JsonFilter @JsonFilter} 注解在 POJO 类上应用过滤器. 注意, 需要两边定义匹配的过滤器标识
 * </p>
 */
@Getter
@ToString
@JsonFilter("non-names")
@EqualsAndHashCode(callSuper = false)
public class Name {
    // 拆分 FullName 的正则表达式
    private static final Pattern NAME_PATTERN = Pattern.compile("(^\\w+)(\\W+)(\\w+$)");

    private final String firstName;
    private final String lastName;

    // 两个名字之间的分隔字符
    private final String middot;

    /**
     * 构造器, 可以通过 {@code fullName} 或者 {@code firstName}, {@code lastName} 以及
     * {@code middot} 参数构建对象
     *
     * @param fullName  可选, 如果无此参数, 则 {@code firstName}, {@code lastName} 必填
     * @param firstName 可选, 如无 {@code fullName} 参数, 则此参数必填
     * @param lastName  可选, 如无 {@code fullName} 参数, 则此参数必填
     * @param middot    可选, 默认为 {@code ·} 字符
     */
    @JsonCreator
    public Name(
            @JsonProperty("fullName") String fullName,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty(value = "middot", defaultValue = "·") String middot) {

        // 判断 fullName 参数是否有效
        if (Strings.isNullOrEmpty(fullName)) {
            // 无 fullName 参数情况
            this.firstName = firstName;
            this.lastName = lastName;
            this.middot = middot;
        } else {
            // 有 fullName 参数情况, 忽略其它参数
            // 分隔名称
            var r = splitFullName(fullName);
            this.firstName = r[0];
            this.lastName = r[2];
            this.middot = r[1];
        }
    }

    /**
     * 分隔一个 {@code fullName}
     *
     * @return 数组, 为 {@code [firstName, middot, lastName]} 组成
     */
    private static String[] splitFullName(String fullName) {
        var result = new String[] { "", "", "" };

        var m = NAME_PATTERN.matcher(fullName.trim());
        if (m.find()) {
            // 将分组结果依次放入数组, 作为最终结果
            result[0] = m.group(1);
            result[1] = m.group(2);
            result[2] = m.group(3);
        }
        return result;
    }

    /**
     * 额外增加 {@code fullName} 字段
     *
     * @return 通过 {@code firstName}, {@code middot} 以及 {@code lastName} 组合而成的全名
     */
    @JsonGetter("fullName")
    public String getFullName() { return String.format("%s%s%s", firstName, middot, lastName); }
}
