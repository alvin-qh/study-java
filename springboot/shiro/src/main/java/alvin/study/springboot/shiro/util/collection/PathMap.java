package alvin.study.springboot.shiro.util.collection;

import com.google.common.base.Splitter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 具备按 Path 找到对应值的 Map 类型
 *
 * <p>
 * 反序列化的 JSON 字符串会产生一个嵌套的 Map 类型对象, 即 Map 的 Value 对应一个简单值或另一个 Map,
 * 该类型可以比较容易的通过一个 Path 字符串在 Map 中跨多个嵌套层级获取对应的值
 * </p>
 */
public class PathMap extends LinkedHashMap<String, Object> {
    // path 每个段解析的正则表达式
    private static final Pattern ARRAY_PATTERN = Pattern.compile("([A-Za-z0-9_$]+)(\\[(\\d+)\\])?");

    /**
     * 根据一个 {@code path} 获取对应的值
     *
     * <p>
     * {@code path} 指的是类似 {@code "a.b.c[1].d"} 的字符串, 其中 {@code c[1]} 表示 {@code "c"}
     * 对应的是一个列表对象 (或数组对象)
     * </p>
     *
     * @param path 对应值的路径
     * @return 获取到的值
     */
    @SuppressWarnings("unchecked")
    public <T> T getByPath(String path) {
        // 将 path 按 '.' 分割为集合
        var parts = Splitter.on(".").omitEmptyStrings().splitToList(path);
        // 最后一个 path 部分, 作为哨兵对象
        var lastPart = parts.get(parts.size() - 1);

        var tree = (Object) this;
        try {
            // 遍历所有的 path 部分
            for (var p : parts) {
                // 通过正则匹配当前 path 部分
                var m = ARRAY_PATTERN.matcher(p);
                if (!m.matches()) {
                    break;
                }

                // 根据当前 path 部分从 Map 中获取内容
                tree = ((Map<String, Object>) tree).getOrDefault(m.group(1), null);

                // 判断当前 path 部分是否具备下标, null 表示无下标
                if (m.group(2) != null) {
                    // 将当前部分对应的值转为集合或数组, 根据下标取值
                    if (tree instanceof List) {
                        tree = ((List<Map<String, Object>>) tree).get(Integer.parseInt(m.group(3)));
                    } else if (tree.getClass().isArray()) {
                        tree = ((Map<String, Object>[]) tree)[Integer.parseInt(m.group(3))];
                    }
                }

                // 如果当前部分是 path 的最后一个部分, 则返回取到的值
                if (p == lastPart) {
                    return (T) tree;
                }

                // 如果当前 path 部分已经无法取到下一步的值, 则退出循环
                if (tree == null) {
                    break;
                }
            }
        } catch (ClassCastException ignore) { }
        return null;
    }

    /**
     * 根据 Key 获取指定类型的 Value
     *
     * @param <T> Value 的类型
     * @param key Key 值
     * @return 对应 Key 的 Value 值
     */
    @SuppressWarnings("unchecked")
    public <T> T getAs(Object key) {
        return (T) super.get(key);
    }
}
