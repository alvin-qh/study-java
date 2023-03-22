package alvin.study.concurrent;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 * 演示队列的各类操作
 */
@SuppressWarnings("java:S2925")
class SpliteratorTest {
    @Test
    void split_should() throws InterruptedException {
        var list = IntStream.range(0, 10).boxed().toList();

        var part1 = list.stream().spliterator();
        then(part1.estimateSize()).isEqualTo(10);

        var part2 = part1.trySplit();
        then(part2.estimateSize()).isEqualTo(part1.estimateSize()).isEqualTo(5);

        part1.tryAdvance(n -> then(n).isIn(1, 2, 3, 4, 5));
        part2.tryAdvance(n -> then(n).isIn(6, 7, 8, 9, 0));
    }
}
