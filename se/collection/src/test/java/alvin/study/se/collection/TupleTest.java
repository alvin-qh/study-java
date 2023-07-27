package alvin.study.se.collection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link Tuple} 类型
 */
class TupleTest {
    /**
     * 测试创建 {@link Tuple} 类型对象
     */
    @Test
    void tuple_shouldCreateTupleObject() {
        var numTuple = Tuple.of(1, 2, 3, 4, 5);
        then(numTuple).hasSize(5).containsExactly(1, 2, 3, 4, 5);

        var strTuple = Tuple.of(List.of("A", "B", "C", "D", "E"));
        then(strTuple).hasSize(5).containsExactly("A", "B", "C", "D", "E");
    }

    /**
     * 测试访问 {@link Tuple} 类型对象的元素
     */
    @Test
    void contains_shouldAccessElementsOfTupleObject() {
        var tuple = Tuple.of(1, 2, 3, 4, 5);

        then(tuple.get(2)).isEqualTo(3);
        then(tuple.containsAll(List.of(3, 4, 5))).isTrue();
    }
}
