package alvin.study.concurrent;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

/**
 * 演示队列的各类操作
 */
@SuppressWarnings("java:S2925")
class SpliteratorTest {
    @Test
    void trySplit_shouldSplitCollectionIntoSlice() throws InterruptedException {
        var list = IntStream.range(0, 10).boxed().toList();

        var part1 = list.stream().spliterator();
        then(part1.estimateSize()).isEqualTo(10);

        var part2 = part1.trySplit();
        then(part1.estimateSize()).isEqualTo(5);
        then(part2.estimateSize()).isEqualTo(5);

        then(toList(part1, part2)).containsExactlyInAnyOrder(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @SafeVarargs
    private static <T> List<T> toList(Spliterator<T>... spliterators) {
        var result = new ArrayList<T>();
        for (var sp : spliterators) {
            StreamSupport.stream(sp, false).forEach(result::add);
        }
        return result;
    }
}
