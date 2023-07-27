package alvin.study.se.collection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link Pair} 类型
 */
class PairTest {
    /**
     * 测试创建 {@link Pair} 类型对象并访问其内容
     */
    @Test
    void create_shouldCreatePairObjectAndAccessValues() {
        var pair = Pair.of("A", 100);

        then(pair.getLeft()).isEqualTo("A");
        then(pair.getKey()).isEqualTo("A");

        then(pair.getRight()).isEqualTo(100);
        then(pair.getValue()).isEqualTo(100);
    }
}
