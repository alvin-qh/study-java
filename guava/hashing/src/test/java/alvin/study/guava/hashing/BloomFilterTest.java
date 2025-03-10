package alvin.study.guava.hashing;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.BloomFilter;

import org.junit.jupiter.api.Test;

import alvin.study.guava.hashing.model.Person;

/**
 * 演示布隆过滤器的使用
 *
 * <p>
 * 布隆过滤器可以认为是通过二进制位标记, 来标识一条数据是否存在 (或不存在),
 * 例如经典的"缓存穿透"问题, 即数据库中不存在的值因不被缓存,
 * 从而导致缓存无法命中不断查询数据库. 此时可以将记录的标识存入布隆过滤器,
 * 当要查询不存在的数据时, 布隆过滤器就可以给出"不存在"这个结果,
 * 而无需进一步查询数据库
 * </p>
 *
 * <p>
 * 除过"缓存穿透"场景, 布隆过滤器还可以进行: 黑名单过滤; 防止重复访问过滤等场景
 * </p>
 *
 * <p>
 * Bloom Filter (布隆过滤器) 是一个很长的二进制向量和一系列随机映射函数,
 * 用于检索一个元素是否在一个集合中, 具有很好的空间和时间效率,
 * 其缺点在于有一定的误识别率, 并且无法删除元素. 所以布隆过滤器适合数据量较大,
 * 查询速度要求高, 且数据本身变化较小的情况
 * </p>
 *
 * <p>
 * 要将一条数据标记在布隆过滤器中, 需要通过"无偏散列函数"多次迭代计算,
 * 对这条数据计算出多个 Hash 值, 并在 Hash 值指定的二进制位标记数据, 例如:
 * 在一个具备 {@code 10000} 个二进制位的空间中, 标记 {@code A} 记录存在,
 * 共 {@code 3} 个无偏散列函数, 计算结果为 {@code 2, 20, 113},
 * 则在二进制空间中, 将第 {@code 2} 位, {@code 20} 位和 {@code 113}
 * 位标记为 {@code 1}; 当需要确认记录是否存在时,
 * 只需要用同样的散列函数对要查询的数据做一次计算, 并确认对应计算结果的位置是否为
 * {@code 1} 即可
 * </p>
 *
 * <p>
 * 由于散列函数碰撞的可能性, 如果布隆过滤器判断一个记录存在,
 * 则有一定几率该记录其实并不存在, 仍需要进一步查询数据源, 以确定数据是否真实存在.
 * 通过增加存储标记的二进制位数和增加无偏散列函数的计算迭代次数,
 * 可以降低布隆过滤器的错误率
 * </p>
 *
 * <p>
 * 但如果布隆过滤器判断一个记录不存在, 则该记录一定不存在,
 * 这也是布隆过滤器可以用于处理缓存击穿的基本特性
 * </p>
 */
class BloomFilterTest {
    /**
     * 测试布隆过滤器标记记录和判断记录是否存在
     *
     * <p>
     * 通过 {@link BloomFilter#create(com.google.common.hash.Funnel, long, double)
     * BloomFilter.create(Funnel, long, double)} 方法可以创建布隆过滤器对象, 其参数包括:
     * <ul>
     * <li>
     * 参数1: {@link com.google.common.hash.Funnel Funnel} 类型参数表示如何计算一条记录的
     * hash 值的函数
     * </li>
     * <li>
     * 参数2: 表示可能要标记的记录数, 该参数和参数 3 共同计算要存储标记的二进制位数量
     * </li>
     * <li>
     * 参数3: 表示在设定的记录数范围内, 期望达到的正确率
     * </li>
     * <li>
     * 设参数 2 为 {@code n}, 参数 3 为 {@code p}, 则 bit 数 {@code m} 的计算公式为:
     * {@code m = -n * ln(p) / (ln(2) ^ 2)}; 散列计算迭代数 {@code k} 的计算公式为
     * {@code k = m / n * ln(2)}
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 通过 {@link BloomFilter#put(Object)} 方法可以标记已存在的记录
     * </p>
     *
     * <p>
     * 通过 {@link BloomFilter#mightContain(Object)} 方法可以检查记录是否已经标记为已存在
     * </p>
     */
    @Test
    void mightContain_checkIfRecordExist() {
        // 创建布隆过滤器对象, 预计存储 10000 条记录, 错误率 1/1000
        var filter = BloomFilter.create(
            Person.makeFunnel(),
            10000,
            0.001);

        // 标记已存在的记录
        filter.put(new Person(
            1L,
            "Alvin",
            LocalDate.of(1981, 3, 17),
            "Shanxi Xi'an"));
        filter.put(new Person(
            2L,
            "Emma",
            LocalDate.of(1985, 3, 29),
            "Shanxi Xi'an"));
        filter.put(new Person(
            3L,
            "Lucy",
            LocalDate.of(1992, 1, 8),
            "Guangdong Shenzhen"));

        // 检查指定的记录是否已存在
        then(filter.mightContain(new Person(
            2L,
            "Emma",
            LocalDate.of(1985, 3, 29),
            "Shanxi Xi'an"))).isTrue();
        then(filter.mightContain(new Person(
            3L,
            "Emma",
            LocalDate.of(1985, 3, 29),
            "Shanxi Xi'an"))).isFalse();
        then(filter.mightContain(new Person(
            2L,
            "Emma",
            LocalDate.of(1985, 3, 30),
            "Shanxi Xi'an"))).isFalse();
    }

    /**
     * 测试对布隆过滤器进行持久化
     *
     * <p>
     * 持久化可以将一个布隆过滤器进行存取,大致可以起到几个作用:
     * 1. 无需频繁从数据源构建布隆过滤器, 构建一次, 进行持久化, 后续可以从持久化存储中恢复该布隆过滤器;
     * 2. 在分布式系统中, 由主控节点生成布隆过滤器并序列化, 其它节点基于主节点持久化数据进行恢复,
     * 从而另所有节点具备一致性的布隆过滤器对象
     * </p>
     */
    @Test
    void persist_shouldSaveAndReadBloomFilterInIOStream() throws IOException {
        var filter = BloomFilter.create(Person.makeFunnel(), 10000, 0.001);

        filter.put(new Person(
            1L,
            "Alvin",
            LocalDate.of(1981, 3, 17),
            "Shanxi Xi'an"));
        filter.put(new Person(
            2L,
            "Emma",
            LocalDate.of(1985, 3, 29),
            "Shanxi Xi'an"));
        filter.put(new Person(
            3L,
            "Lucy",
            LocalDate.of(1992, 1, 8),
            "Guangdong Shenzhen"));

        byte[] data;
        // 序列化布隆过滤器, 将其存储到输出流中
        try (var output = new ByteArrayOutputStream()) {
            filter.writeTo(output);

            output.flush();
            data = output.toByteArray();
        }

        // 反序列化布隆过滤器, 从输入流恢复布隆过滤器
        try (var input = new ByteArrayInputStream(data)) {
            filter = BloomFilter.readFrom(input, Person.makeFunnel());
        }

        // 确认恢复的布隆过滤器正确
        then(filter.mightContain(new Person(
            2L,
            "Emma",
            LocalDate.of(1985, 3, 29),
            "Shanxi Xi'an"))).isTrue();
        then(filter.mightContain(new Person(
            3L,
            "Emma",
            LocalDate.of(1985, 3, 29),
            "Shanxi Xi'an"))).isFalse();
    }

    /**
     * 测试将 {@link java.util.stream.Stream Stream} 转为布隆过滤器对象
     *
     * <p>
     * 使用 {@link BloomFilter#toBloomFilter(com.google.common.hash.Funnel, long, double)
     * BloomFilter.toBloomFilter(Funnel, long, double)} 方法可以通过
     * {@link java.util.stream.Stream#collect(java.util.stream.Collector) Stream.collect(Collector)} 方法将
     * {@code Stream} 转为布隆过滤器对象
     * </p>
     */
    @Test
    void toBloomFilter_shouldCollectStreamToBloomFilter() {
        // 产生一个目标对象的集合
        var persons = ImmutableList.of(
            new Person(
                1L,
                "Alvin",
                LocalDate.of(1981, 3, 17),
                "Shanxi Xi'an"),
            new Person(
                2L,
                "Emma",
                LocalDate.of(1985, 3, 29),
                "Shanxi Xi'an"),
            new Person(
                3L,
                "Lucy",
                LocalDate.of(1992, 1, 8),
                "Guangdong Shenzhen"));

        // 通过 Stream 将目标集合转为布隆过滤器对象
        var filter = persons.stream().collect(
            BloomFilter.toBloomFilter(Person.makeFunnel(), persons.size(), 0.001));

        // 确认转换的布隆过滤器正确
        then(filter.mightContain(new Person(
            2L,
            "Emma",
            LocalDate.of(1985, 3, 29),
            "Shanxi Xi'an"))).isTrue();
        then(filter.mightContain(new Person(
            3L,
            "Emma",
            LocalDate.of(1985, 3, 29),
            "Shanxi Xi'an"))).isFalse();
    }
}
