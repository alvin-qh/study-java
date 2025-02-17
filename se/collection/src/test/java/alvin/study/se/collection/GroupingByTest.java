package alvin.study.se.collection;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.api.Condition;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

import lombok.RequiredArgsConstructor;

/**
 * 博客帖子分类枚举
 */
enum BlogPostType {
    NEWS,
    REVIEW,
    GUIDE
}

/**
 * 博客帖子类
 */
record BlogPost(
        String title,
        String author,
        BlogPostType type,
        int likes,
        List<String> comments) {
    /**
     * 保存联合分组属性的记录类型
     *
     * @param type  帖子类型
     * @param likes 点赞数
     */
    record TypeAndLikeKey(BlogPostType type, int likes) {}

    /**
     * 保存多聚合结果的记录属性
     *
     * @param postCount 帖子数量
     * @param titles    标题集合
     * @param likeStats 点赞数统计信息
     */
    record PostCountTitlesLikesStats(long postCount, String titles, IntSummaryStatistics likeStats) {}

    /**
     * 保存标题和点赞数聚合结果
     *
     * @param postCount         帖子总数
     * @param titles            帖子标题聚合
     * @param boundedSumOfLikes 点赞数聚合
     */
    record TitlesBoundedSumOfLikes(long postCount, String titles, int boundedSumOfLikes) {}
}

/**
 * 本测试演示了 {@code Collectors} 类的 {@code groupingBy} 系列方法的使用
 *
 * <p>
 * {@code groupingBy} 的主要作用是对一个 {@link java.util.stream.Stream Stream} 对象进行分组,
 * 返回一个 {@link Map} 对象,
 * 其中 {@code Key} 是分组值, {@code Value} 是分组结果
 * </p>
 */
class GroupingByTest {
    /**
     * 生成测试数据
     *
     * <p>
     * 本例中通过 {@code javafaker} 库 的 {@link Faker} 类型产生随机测试数据
     * </p>
     *
     * @param requiredCounts 一个 {@link Map} 对象, 设置要生成每个 {@link BlogPostType}
     *                       的帖子生成的数量
     * @return 伪造的 {@link BlogPost} 对象集合
     */
    private static List<BlogPost> makeTestingData(Map<BlogPostType, Integer> requiredCounts) {
        // 记录 likes 数值的辅助类
        // 为了让测试数据中的点赞数符合预期, 需要通过本类对象获取生成点赞数, 生成规则是: 按顺序返回 {@code [1..3)} 之间的点赞数
        class LikeHolder {
            // 点赞数
            private int likes;

            /**
             * 构造器, 初始化点赞数
             */
            LikeHolder() {
                this.likes = 1;
            }

            /**
             * 获取点赞数
             *
             * <p>
             * 本方法令获取的点赞数在 {@code [1..3)} 之间按顺序变化
             * </p>
             *
             * @return 点赞数值
             */
            public int getLikes() {
                var likes = this.likes;
                if (++this.likes == 3) {
                    this.likes = 1;
                }
                return likes;
            }
        }

        // 数据伪造对象
        var faker = new Faker(Locale.CHINA);
        // 点赞数产生对象
        var likeHolder = new LikeHolder();

        // 按每个类型要求的帖子数量产生测试数据
        return requiredCounts.entrySet()
                .stream()
                .flatMap(e -> IntStream.range(0, e.getValue())
                        .mapToObj(n -> new BlogPost(
                            faker.funnyName().name(),
                            faker.name().fullName(),
                            e.getKey(),
                            likeHolder.getLikes(),
                            IntStream.range(0, n + 1) // 第几个帖子就有几条回复
                                    .mapToObj(ignore -> faker.regexify("[A-Za-z\\-,. ]{20,50}"))
                                    .toList())))
                .toList();
    }

    /**
     * 测试 {@link Collectors#groupingBy(java.util.function.Function)
     * Collectors.groupingBy(Function)}
     * 方法, 对集合数据进行简单分组
     *
     * <p>
     * 根据一个 {@link java.util.function.Function Function} 对象产生分组值, 进行分组, 返回一个
     * {@link Map} 结果, 其中
     * {@code Key} 为分组值, {@code Value} 为相关的组, 是一个 {@link List} 集合
     * </p>
     *
     * <p>
     * 本例中的分组结果为 {@code Map<BlogPostType, List<BlogPost>>} 类型
     * </p>
     */
    @Test
    void classifier_simpleGroupingByASingleColumn() {
        // 生成测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        // 将测试数据以 Map<BlogPostType, List<BlogPost>> 类型进行分组
        var results = data.stream().collect(Collectors.groupingBy(BlogPost::type));

        // 确认分组数量
        then(results).hasSize(3);

        // 确认各分组数据
        then(results).extractingByKey(BlogPostType.NEWS).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(10);
        then(results).extractingByKey(BlogPostType.REVIEW).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(5);
        then(results).extractingByKey(BlogPostType.GUIDE).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(8);
    }

    /**
     * 测试通过一个复合类型作为分组的 {@code Key} 值
     *
     * <p>
     * 根据一个 {@link java.util.function.Function Function} 对象产生分组值, 进行分组, 返回一个
     * {@link Map} 结果, 其中
     * {@code Key} 为分组值, {@code Value} 为相关的组, 是一个 {@link List} 集合
     * </p>
     *
     * <p>
     * 本例中通过一个 {@code Record} 类型产生一个联合 {@code Key} 作为分组依据, 参考
     * {@link BlogPost.TypeAndLikeKey} 类型
     * </p>
     *
     * <p>
     * 本例中的分组结果为 {@code Map<BlogPost.TypeAndLikeKey, List<BlogPost>>} 类型
     * </p>
     */
    @Test
    void classifier_groupingByWithAComplexMapKeyType() {
        // 产生测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        // 将测试数据以 Map<BlogPost.TypeAndLikeKey, List<BlogPost>> 类型进行分组
        var results = data.stream().collect(
            Collectors.groupingBy(blog -> new BlogPost.TypeAndLikeKey(
                blog.type(),
                blog.likes())));

        // 确认分组数量
        // 因为每个帖子类型都有点赞数为 1 和 2 的记录, 所以产生 6 个分组 (帖子类型分类 x 点赞数分类 = 3 * 2 = 6)
        then(results).hasSize(6);

        // 确认各分组数据
        then(results).containsKey(new BlogPost.TypeAndLikeKey(BlogPostType.NEWS, 1));
        then(results).containsKey(new BlogPost.TypeAndLikeKey(BlogPostType.NEWS, 2));

        then(results).containsKey(new BlogPost.TypeAndLikeKey(BlogPostType.REVIEW, 1));
        then(results).containsKey(new BlogPost.TypeAndLikeKey(BlogPostType.REVIEW, 2));

        then(results).containsKey(new BlogPost.TypeAndLikeKey(BlogPostType.GUIDE, 1));
        then(results).containsKey(new BlogPost.TypeAndLikeKey(BlogPostType.GUIDE, 2));
    }

    /**
     * 测试指定存储分组的集合类型
     *
     * <p>
     * 默认的 {@link Collectors#groupingBy(java.util.function.Function)
     * Collectors.groupingBy(Function)} 方法将统一分组的 内容存放在一个 {@link List} 集合中,
     * 若希望把分组存放在其它类型集合中,
     * 则可以通过 {@link java.util.stream.Collector Collector} 参数来指定
     * </p>
     *
     * <p>
     * 本例中将分组内容最终存放在 {@link Set} 集合中, 所以为 {@code groupingBy} 方法的第二个
     * {@link java.util.stream.Collector Collector} 参数传入 {@link Collectors#toSet()}
     * 参数
     * </p>
     *
     * <p>
     * 本例中的分组结果为 {@code Map<BlogPostType, Set<BlogPost>>} 类型
     * </p>
     */
    @Test
    void classifier_modifyingTheReturnedMapValueType() {
        // 产生测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        // 将测试数据以 Map<BlogPostType, Set<BlogPost>> 类型进行分组
        var results = data.stream().collect(
            Collectors.groupingBy(BlogPost::type, Collectors.toSet()));

        // 确认分组数量
        then(results).hasSize(3);

        // 确认每个分组都以 Set 集合存储
        then(results)
                .extractingByKey(BlogPostType.NEWS)
                .asInstanceOf(InstanceOfAssertFactories.collection(Set.class)).hasSize(10);
        then(results)
                .extractingByKey(BlogPostType.REVIEW)
                .asInstanceOf(InstanceOfAssertFactories.collection(Set.class)).hasSize(5);
        then(results)
                .extractingByKey(BlogPostType.GUIDE)
                .asInstanceOf(InstanceOfAssertFactories.collection(Set.class)).hasSize(8);
    }

    /**
     * 测试在一次分组的基础上再次进行分组
     *
     * <p>
     * 要在一次分组的基础上在此进行分组, 只需要在 {@code groupingBy} 方法的第二个
     * {@link java.util.stream.Collector Collector}
     * 参数传递另一个 {@code groupingBy} 方法即可
     * </p>
     *
     * <p>
     * 本例中的分组结果为 {@code Map<BlogPostType, Map<Integer, List<BlogPost>>>} 类型, 即先以
     * {@code BlogPostType} 进行分组,
     * 在每个分组内再以"点赞数"进行二次分组
     * </p>
     */
    @Test
    void classifier_groupingByMultipleFields() {
        // 产生测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        // 将测试数据以 Map<BlogPostType, Map<Integer, List<BlogPost>>> 类型进行分组
        var results = data.stream().collect(
            Collectors.groupingBy(BlogPost::type, Collectors.groupingBy(BlogPost::likes)));

        // 确认第一级分组数量
        then(results).hasSize(3);

        // 确认每个分组下仍是一个分组的 Map 集合
        then(results)
                .extractingByKey(BlogPostType.NEWS)
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .containsOnlyKeys(1, 2);
        then(results)
                .extractingByKey(BlogPostType.REVIEW)
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .containsOnlyKeys(1, 2);
        then(results)
                .extractingByKey(BlogPostType.GUIDE)
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .containsOnlyKeys(1, 2);
    }

    /**
     * <p>
     * 测试
     * {@link Collectors#averagingInt(java.util.function.ToIntFunction)
     * Collectors.averagingInt(ToIntFunction)} 算子, 对
     * 分组结果求平均值
     * </p>
     *
     * <p>
     * 如果用于计算平均值的数据是浮点类型, 则可通过
     * {@link Collectors#averagingDouble(java.util.function.ToDoubleFunction)
     * Collectors.averagingInt(ToDoubleFunction)}
     * 方法进行
     * </p>
     *
     * <p>
     * 本例中的分组结果为 {@code Map<BlogPostType, Double>} 类型, 即根据帖子类型分组后, 求每个分组点赞数的平均值
     * </p>
     */
    @Test
    void classifier_gettingTheAverageFromGroupedResults() {
        // 产生测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        // 将测试数据以 Map<BlogPostType, Double> 类型进行分组
        var results = data.stream().collect(
            Collectors.groupingBy(BlogPost::type, Collectors.averagingInt(BlogPost::likes)));

        // 确认分组数量
        then(results).hasSize(3);

        // 确认每个分组平均值结果
        then(results).extractingByKey(BlogPostType.NEWS).isEqualTo(1.5);
        then(results).extractingByKey(BlogPostType.REVIEW).isEqualTo(1.4);
        then(results).extractingByKey(BlogPostType.GUIDE).isEqualTo(1.5);
    }

    /**
     * 测试 {@link Collectors#summingInt(java.util.function.ToIntFunction)
     * Collectors.summingInt(ToIntFunction)}
     * 算子, 对分组结果进行求和计算
     *
     * <p>
     * 如果用于求和的数据是浮点类型, 则可以使用
     * {@link Collectors#summingDouble(java.util.function.ToDoubleFunction)
     * Collectors.summingDouble(ToDoubleFunction)}
     * 方法进行
     * </p>
     *
     * <p>
     * 本例中的分组结果为 {@code Map<BlogPostType, Integer>} 类型, 即根据帖子类型分组后, 求每个分组点赞数的总和
     * </p>
     */
    @Test
    void classifier_gettingTheSumFromGroupedResults() {
        // 产生测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        // 将测试数据以 Map<BlogPostType, Integer> 类型进行分组
        var results = data.stream().collect(
            Collectors.groupingBy(BlogPost::type, Collectors.summingInt(BlogPost::likes)));

        // 确认分组数量
        then(results).hasSize(3);

        // 确认每个分组点赞数总和
        then(results).extractingByKey(BlogPostType.NEWS).isEqualTo(15);
        then(results).extractingByKey(BlogPostType.REVIEW).isEqualTo(7);
        then(results).extractingByKey(BlogPostType.GUIDE).isEqualTo(12);
    }

    /**
     * 测试 {@link Collectors#maxBy(Comparator)}/{@link Collectors#minBy(Comparator)}
     * 算子, 对分组结果求最大/最小值
     *
     * <p>
     * {@link Comparator} 参数传递的是一个比较器对象, 用于对指定数据进行大小比较
     * </p>
     *
     * <p>
     * 本例中的分组结果为 {@code Map<BlogPostType, Optional<BlogPost>>} 类型, 即点赞数最多或最少的
     * {@link BlogPost} 对象,
     * 之所以通过 {@link java.util.Optional Optional} 对象返回, 是因为分组为"空"的那一组数据, 将返回
     * {@link java.util.Optional#empty() Optional.empty()} 结果
     * </p>
     */
    @Test
    void classifier_gettingTheMaximumOrMinimumFromGroupedResults() {
        // 产生测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        // 将测试数据以 Map<BlogPostType, Optional<BlogPost>> 类型进行分组, 每组返回点赞数最大的那个对象
        var results = data.stream().collect(
            Collectors.groupingBy(BlogPost::type, Collectors.maxBy(Comparator.comparingInt(BlogPost::likes))));

        // 确认分组数量
        then(results).hasSize(3);

        // 确认每组的对象均为点赞数最大的那个
        then(results.get(BlogPostType.NEWS)).isPresent().get().extracting("likes").isEqualTo(2);
        then(results.get(BlogPostType.REVIEW)).isPresent().get().extracting("likes").isEqualTo(2);
        then(results.get(BlogPostType.GUIDE)).isPresent().get().extracting("likes").isEqualTo(2);

        // 将测试数据以 Map<BlogPostType, Optional<BlogPost>> 类型进行分组, 每组返回点赞数最小的那个对象
        results = data.stream().collect(
            Collectors.groupingBy(BlogPost::type, Collectors.minBy(Comparator.comparingInt(BlogPost::likes))));

        // 确认分组数量
        then(results).hasSize(3);

        // 确认每组的对象均为点赞数最小的那个
        then(results.get(BlogPostType.NEWS)).isPresent().get().extracting("likes").isEqualTo(1);
        then(results.get(BlogPostType.REVIEW)).isPresent().get().extracting("likes").isEqualTo(1);
        then(results.get(BlogPostType.GUIDE)).isPresent().get().extracting("likes").isEqualTo(1);
    }

    /**
     * 测试 {@link Collectors#summarizingInt(java.util.function.ToIntFunction)
     * Collectors.summarizingInt(ToIntFunction)} 方法, 求指定数据的各类统计指标
     *
     * <p>
     * {@link IntSummaryStatistics} 类型可以提供一个由整数计算得到的统计指标对象, 包括:
     * <ul>
     * <li>
     * {@link IntSummaryStatistics#getCount()}, 获取总数
     * </li>
     * <li>
     * {@link IntSummaryStatistics#getSum()}, 获取总和
     * </li>
     * <li>
     * {@link IntSummaryStatistics#getAverage()}, 获取平均值
     * </li>
     * <li>
     * {@link IntSummaryStatistics#getMax()}, 获取最大值
     * </li>
     * <li>
     * {@link IntSummaryStatistics#getMin()}, 获取最小值
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 如果统计各项指标值需要通过浮点数进行运算, 则应使用
     * {@link Collectors#summarizingDouble(java.util.function.ToDoubleFunction)
     * Collectors.summarizingDouble(ToDoubleFunction)} 方法
     * </p>
     *
     * <p>
     * 本例中的分组结果为 {@code Map<BlogPostType, IntSummaryStatistics>} 类型,
     * 即根据帖子分类统计的点赞数各项指标
     * </p>
     */
    @Test
    void classifier_gettingASummaryForAnAttributeOfGroupedResults() {
        // 产生测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        // 将测试数据以 Map<BlogPostType, IntSummaryStatistics> 类型进行分组, 每组返回点赞数的统计指标对象
        var results = data.stream().collect(
            Collectors.groupingBy(BlogPost::type, Collectors.summarizingInt(BlogPost::likes)));

        // 确认分组数量
        then(results).hasSize(3);

        // 定义 {@link Condition} 类型用于对统计结果进行断言
        @RequiredArgsConstructor
        class SummaryCondition extends Condition<IntSummaryStatistics> {
            private final long count;
            private final double avg;
            private final long sum;
            private final int max;
            private final int min;

            @Override
            public boolean matches(IntSummaryStatistics value) {
                return value.getCount() == count
                       && value.getAverage() == avg
                       && value.getSum() == sum
                       && value.getMax() == max
                       && value.getMin() == min;
            }
        }

        // 确认每个分组得到的统计指标
        then(results).extractingByKey(BlogPostType.NEWS).has(new SummaryCondition(10, 1.5, 15, 2, 1));
        then(results).extractingByKey(BlogPostType.REVIEW).has(new SummaryCondition(5, 1.4, 7, 2, 1));
        then(results).extractingByKey(BlogPostType.GUIDE).has(new SummaryCondition(8, 1.5, 12, 2, 1));
    }

    /**
     * 测试
     * {@link Collectors#collectingAndThen(java.util.stream.Collector, java.util.function.Function)
     * Collectors.collectingAndThen(Collector, Function)} 方法, 在分组的基础上进行计算
     *
     * <p>
     * 本例中的分组结果为 {@code Map<BlogPostType, BlogPost.PostCountTitlesLikesStats>} 类型,
     * 即根据帖子分类后产生的统计结果
     * </p>
     */
    @Test
    void classifier_aggregatingMultipleAttributesOfAGroupedResult() {
        // 产生测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        // 将测试数据以 Map<BlogPostType, BlogPost.PostCountTitlesLikesStats> 类型进行分组,
        // 每组返回所需统计数据
        var results = data.stream().collect(
            Collectors.groupingBy(BlogPost::type, Collectors.collectingAndThen(
                // 分组结果存储在 List 集合中
                Collectors.toList(),
                // 对分组结果进行进一步处理
                list -> {
                    // 将分组中所有帖子的标题连接成一个
                    var titles = list.stream()
                            .map(BlogPost::title)
                            .collect(Collectors.joining(":"));

                    // 将分组中所有帖子的点赞数进行统计
                    var summary = list.stream().collect(Collectors.summarizingInt(BlogPost::likes));

                    // 将结果返回成 BlogPost.PostCountTitlesLikesStats 类型对象
                    return new BlogPost.PostCountTitlesLikesStats(list.size(), titles, summary);
                })));

        // 确认分组数量
        then(results).hasSize(3);

        // 确认每个分组得到的统计指标
        then(results)
                .extractingByKey(BlogPostType.NEWS)
                .matches(stats -> stats.postCount() == 10L && stats.likeStats().getAverage() == 1.5);
        then(results)
                .extractingByKey(BlogPostType.REVIEW)
                .matches(stats -> stats.postCount() == 5L && stats.likeStats().getAverage() == 1.4);
        then(results)
                .extractingByKey(BlogPostType.GUIDE)
                .matches(stats -> stats.postCount() == 8L && stats.likeStats().getAverage() == 1.5);
    }

    /**
     * 通过
     * {@link Collectors#toMap(java.util.function.Function, java.util.function.Function, java.util.function.BinaryOperator)
     * Collectors.toMap(Function, Function, BinaryOperator)} 方法进行分组统计
     *
     * <p>
     * 对于计算分组统计而言, 除了通过 {@code groupingBy} 方法外, 还可以直接通过 {@code toMap} 方法, 其关键在于
     * {@link java.util.function.BinaryOperator BinaryOperator} 类型参数,
     * 其作用是当集合中的两个元素得到同一个 {@code Key} 值时,
     * 如何合并这两个元素
     * </p>
     *
     * <p>
     * 本例的分组结果为 {@code Map<BlogPostType, BlogPost.TitlesBoundedSumOfLikes>},
     * 表示按帖子类型分组后, 各组的统计值
     * </p>
     */
    @Test
    void classifier_aggregatingMultipleAttributesByToMapMethod() {
        // 产生测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        // 将测试数据以 Map<BlogPostType, BlogPost.TitlesBoundedSumOfLikes> 类型进行分组, 每组返回所需统计数据
        var results = data.stream().collect(Collectors.toMap(
            // Map 的 Key 值
            BlogPost::type,
            // Map 的 Value 值
            post -> new BlogPost.TitlesBoundedSumOfLikes(1, post.title(), post.likes()),
            // Key 重复的时候, 解决冲突的方法, 这里可以用于进行分组统计计算
            (o, n) -> new BlogPost.TitlesBoundedSumOfLikes(
                // 合并贴子总数
                o.postCount() + n.postCount(),
                // 合并标题内容
                String.format("%s:%s", o.titles(), n.titles()),
                // 合并点赞数
                o.boundedSumOfLikes() + n.boundedSumOfLikes())));

        // 确认分组数量
        then(results).hasSize(3);

        // 确认每个分组得到的统计指标
        then(results)
                .extractingByKey(BlogPostType.NEWS)
                .matches(stats -> stats.postCount() == 10L && stats.boundedSumOfLikes() == 15);
        then(results)
                .extractingByKey(BlogPostType.REVIEW)
                .matches(stats -> stats.postCount() == 5L && stats.boundedSumOfLikes() == 7);
        then(results)
                .extractingByKey(BlogPostType.GUIDE)
                .matches(stats -> stats.postCount() == 8L && stats.boundedSumOfLikes() == 12);
    }

    /**
     * 测试
     * {@link Collectors#mapping(java.util.function.Function, java.util.stream.Collector)
     * Collectors.mapping(Function, Collector)} 方法, 对分组结果进行类型转换
     *
     * <p>
     * 本例的分组结果为 {@code Map<BlogPostType, String>}, 表示按帖子类型分组后, 将分组结果转为
     * {@link String} 类型
     * </p>
     */
    @Test
    void classifier_mappingGroupedResultsToDifferentType() {
        // 产生测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        // 将测试数据以 Map<BlogPostType, String> 类型进行分组, 将每个分组转为字符串类型
        var results = data.stream().collect(Collectors.groupingBy(
            // 指定分组 Key
            BlogPost::type,
            // 将分组结果进行转换
            Collectors.mapping(
                BlogPost::title, // 获取分组结果每个元素的帖子标题, 并将结果进行字符串连接
                Collectors.joining(", ", "Post Titles: [", "]"))));

        // 确认分组数量
        then(results).hasSize(3);

        // 确认每个分组得到的字符串连接结构
        then(results)
                .extractingByKey(BlogPostType.NEWS)
                .asString()
                .matches("^Post Titles: \\[([\\w\\s.']+,?){10}]$");
        then(results)
                .extractingByKey(BlogPostType.REVIEW)
                .asString()
                .matches("^Post Titles: \\[([\\w\\s.']+,?){5}]$");
        then(results)
                .extractingByKey(BlogPostType.GUIDE)
                .asString()
                .matches("^Post Titles: \\[([\\w\\s.']+,?){8}]$");
    }

    /**
     * 测试指定返回结果的 {@link Map} 类型
     *
     * <p>
     * 对于分组操作来说, 需要返回一个 {@link Map} 类型结果, 可以指定这个 {@link Map} 结果的具体类型
     * </p>
     *
     * <p>
     * 在本例中, 如果通过 {@link BlogPost#type()} 来作为分组 {@code Key}, 则对于这种以枚举为 {@code Key}
     * 的情况,
     * {@link EnumMap} 是一种效率更高的 {@link Map} 实现
     * </p>
     */
    @Test
    void classifier_modifyingTheReturnMapType() {
        // 产生测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        // 将测试数据以 EnumMap<BlogPostType, List<BlogPost>> 类型进行分组
        var results = data.stream().collect(Collectors.groupingBy(
            // 获取分组 Key
            BlogPost::type,
            // 设置返回 Map 对象
            () -> new EnumMap<>(BlogPostType.class),
            // 设置分组集合
            Collectors.toList()));

        // 确认分组数量
        then(results).hasSize(3);

        // 确认分组结果
        then(results).extractingByKey(BlogPostType.NEWS).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(10);
        then(results).extractingByKey(BlogPostType.REVIEW).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(5);
        then(results).extractingByKey(BlogPostType.GUIDE).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(8);
    }

    /**
     * 测试
     * {@link Collectors#filtering(java.util.function.Predicate, java.util.stream.Collector)
     * Collectors.filtering(Predicate, Collector)} 方法, 对分组结果进行过滤
     */
    @Test
    void classifier_filteringCollector() {
        // 产生测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        var results = data.stream().collect(Collectors.groupingBy(
            // 获取分组 Key
            BlogPost::type,
            // 对分组元素的每一项进行过滤
            Collectors.filtering(blog -> blog.likes() > 1, Collectors.toList())));

        // 确认分组数量
        then(results).hasSize(3);

        // 确认过滤后的分组结果
        then(results).extractingByKey(BlogPostType.NEWS).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(5);
        then(results).extractingByKey(BlogPostType.REVIEW).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(2);
        then(results).extractingByKey(BlogPostType.GUIDE).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(4);
    }

    /**
     * 测试
     * {@link Collectors#flatMapping(java.util.function.Function, java.util.stream.Collector)
     * Collectors.flatMapping(function, Collector)} 方法, 对分组结果中的集合进行平铺处理
     *
     * <p>
     * 本例中, 对 {@link BlogPost#comments()} 属性的结果进行平铺, 将一个分组下的所有帖子回复都整合到一个集合中
     * </p>
     */
    @Test
    void classifier_flatMappingCollector() {
        // 产生测试数据
        var data = makeTestingData(Map.of(
            BlogPostType.NEWS, 10,
            BlogPostType.REVIEW, 5,
            BlogPostType.GUIDE, 8));
        then(data).hasSize(23);

        var results = data.stream().collect(Collectors.groupingBy(
            // 获取分组 Key
            BlogPost::type,
            // 对每个分组中的回复进行平铺处理
            Collectors.flatMapping(blog -> blog.comments().stream(), Collectors.toList())));

        // 确认分组数量
        then(results).hasSize(3);

        // 确认平铺后后的分组结果
        then(results).extractingByKey(BlogPostType.NEWS).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(55); // ((10
                                                                                                                   // +
                                                                                                                   // 1)
                                                                                                                   // *
                                                                                                                   // 10)
                                                                                                                   // /
                                                                                                                   // 2
        then(results).extractingByKey(BlogPostType.REVIEW).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(15); // ((5
                                                                                                                     // +
                                                                                                                     // 1)
                                                                                                                     // *
                                                                                                                     // 5)
                                                                                                                     // /
                                                                                                                     // 2
        then(results).extractingByKey(BlogPostType.GUIDE).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(36); // ((8
                                                                                                                    // +
                                                                                                                    // 1)
                                                                                                                    // *
                                                                                                                    // 8)
                                                                                                                    // /
                                                                                                                    // 2
    }
}
