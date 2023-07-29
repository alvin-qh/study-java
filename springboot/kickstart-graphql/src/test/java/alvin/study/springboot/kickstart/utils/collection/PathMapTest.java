package alvin.study.springboot.kickstart.utils.collection;

import alvin.study.springboot.kickstart.util.collection.PathMap;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link PathMap} 类型
 */
class PathMapTest {
    /**
     * 测试 {@link PathMap#getByPath(String)} 方法, 根据 Path 获取对应的值
     */
    @Test
    void getByPath_shouldGetMapValueByPath() {
        // 设置待查询的 Map
        var map = new PathMap();
        map.put("a", "A");
        map.put("b", Map.of("c", "C"));
        map.put("d", List.of(
                Map.of("e", "E"),
                Map.of("f", List.of(
                        Map.of("g", "G"),
                        Map.of("h", new Map[]{
                            Map.of("i", "I") }
                        )
                    )
                )
            )
        );

        // 确认通过 Path 获取 Map 对应的值
        then((Object) map.getByPath("a")).isEqualTo("A");
        then((Object) map.getByPath("b.c")).isEqualTo("C");
        then((Object) map.getByPath("d[0].e")).isEqualTo("E");
        then((Object) map.getByPath("d[1].f[0].g")).isEqualTo("G");
        then((Object) map.getByPath("d[1].f[1].h[0].i")).isEqualTo("I");
    }
}
