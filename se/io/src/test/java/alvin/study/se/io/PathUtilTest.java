package alvin.study.se.io;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link PathUtil} 路径工具类
 */
class PathUtilTest {
    /**
     * 测试 {@link PathUtil#combine(String, String...)} 方法, 组合多个路径
     */
    @Test
    void combine_shouldCombineMorePathsIntoOne() {
        var path = PathUtil.combine("/aaa", "bbb", "c.txt");
        then(path).isEqualTo("/aaa/bbb/c.txt");
    }
}
