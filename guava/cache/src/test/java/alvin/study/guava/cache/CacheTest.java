package alvin.study.guava.cache;

import alvin.study.guava.cache.model.User;
import alvin.study.guava.cache.observer.CacheObserver;
import alvin.study.guava.cache.repository.UserRepository;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.hash.BloomFilter;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * 测试 Guava 的缓存类
 */
class CacheTest {
    // 存储数据的持久化对象
    private final UserRepository repository = new UserRepository();

    /**
     * 将 {@link Cache#stats()} 返回的 {@link com.google.common.cache.CacheStats CacheStats} 对象转为字符串
     *
     * @param cache {@link Cache} 类对象
     * @return 包含 {@link com.google.common.cache.CacheStats CacheStats} 对象转为的字符串
     */
    private static String formatCacheStat(Cache<?, ?> cache) {
        var stat = cache.stats();

        // 将缓存命中率, 数据源读取成功次数以及缓存项淘汰个数三个指标项格式化为字符串返回
        return String.format(
            "hit rate = %.1f%%, load success count = %d, eviction count = %d",
            stat.hitRate() * 100,
            stat.loadSuccessCount(),
            stat.evictionCount());
    }

    /**
     * 测试 {@link Cache} 类型对象, 对指定类型的键值对进行缓存
     *
     * <p>
     * 通过 {@link CacheBuilder#build()} 方法可以创建一个 {@link Cache} 类型对象, 表示一个简单缓存
     * </p>
     *
     * <p>
     * {@link Cache#put(Object, Object) Cache.put(K, V)} 方法用于在缓存中存储一个键值对
     * </p>
     *
     * <p>
     * {@link Cache#getIfPresent(Object)} 方法用于通过 Key 从缓存中获取对应的 Value. 需要注意的是, 这里的 key 并未被泛型, 是
     * {@code Object} 类型, 所以错误的类型会导致缓存查询失败. 例如: 如果对 {@code Long} 类型的 Key 错误的使用了 {@code Integer} 类型,
     * 就会导致查询失败
     * </p>
     *
     * <p>
     * {@link Cache#get(Object, java.util.concurrent.Callable) Cache.get(Object, Callable)} 方法用于通过 Key
     * 从缓存中获取对应的 Value, 如果查询失败, 则会调用 {@code Callable} 参数得到一个对象, 并对该对象进行缓存后返回, 或抛出
     * {@link ExecutionException} 或 {@link UncheckedExecutionException} 类型的异常
     * <ul>
     * <li>
     * {@code Callable} 参数不能返回 {@code null} 值来表示对应的 Key 未取到对应的对象, 应该抛出异常
     * </li>
     * <li>
     * 若 {@code Callable} 抛出 {@link Exception} 类型异常, 则会包装为 {@link ExecutionException} 类型异常并通过 {@code get}
     * 方法抛出
     * </li>
     * <li>
     * 若 {@code Callable} 抛出 {@link RuntimeException} 类型, 则会包装为 {@link UncheckedExecutionException} 类型异常并通过
     * {@code get} 方法抛出
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void builder_shouldBuildCache() {
        // 构建一个缓存对象
        var cache = CacheBuilder.newBuilder().<Long, User>build();
        // 确认此时缓存为空
        then(cache.size()).isEqualTo(0);

        // 实例化两个将要被缓存的对象
        var user1 = new User(1L, "Alvin");
        var user2 = new User(2L, "Emma");

        // 缓存一个对象
        cache.put(user1.id(), user1);
        // 确认此时缓存的大小为 1
        then(cache.size()).isEqualTo(1);

        // 确认可以根据 Key 获取对应的缓存对象
        var user = cache.getIfPresent(user1.id());
        then(user).isSameAs(user1);

        // 确认根据无效的 Key 获取的结果为 null 值
        user = cache.getIfPresent(user2.id());
        then(user).isNull();

        // 确认根据一系列 Key 批量获取缓存的对象, 只有存在的 Key 对应的缓存对象被返回
        var users = cache.getAllPresent(Objects.requireNonNull(ImmutableList.of(1L, 2L)));
        then(users).containsExactly(entry(user1.id(), user1));

        // 清空所有缓存对象
        cache.invalidateAll();
        then(cache.size()).isEqualTo(0);

        // 确认批量缓存了键值对
        cache.putAll(Objects.requireNonNull(ImmutableMap.of(
            user1.id(), user1,
            user2.id(), user2)));
        then(cache.size()).isEqualTo(2);

        // 确认根据批量的 Key 获取缓存对象集合
        users = cache.getAllPresent(Objects.requireNonNull(ImmutableList.of(user1.id(), user2.id())));
        then(users).containsExactly(
            entry(user1.id(), user1),
            entry(user2.id(), user2));

        try {
            // 确认通过回调函数, 对不存在的 Key 产生新的缓存对象
            user = cache.get(3L, () -> new User(3L, "Lucy"));
            then(user).extracting("id", "name").contains(3L, "Lucy");
            // 确认新产的的对象也已被缓存
            then(cache.size()).isEqualTo(3);
        } catch (ExecutionException e) {
            fail();
        }
    }

    /**
     * 测试 {@link com.google.common.cache.LoadingCache LoadingCache} 类型对象, 将自动从数据源加载数据并进行缓存
     *
     * <p>
     * 通过 {@link CacheBuilder#build(CacheLoader)} 方法可以创建一个 {@link com.google.common.cache.LoadingCache
     * LoadingCache} 类型对象, 表示一个缓存对象, 可以自动从数据源加载被缓存对象
     * </p>
     *
     * <p>
     * 当指定的 Key 对应的对象尚未被缓存时, 会通过 {@link CacheLoader#load(Object) CacheLoader.load(K)} 方法根据 Key
     * 返回从数据源获取待缓存的对象 (或新建待缓存的对象). 注意, 该方法不允许返回 {@code null} 值来表示对应 Key 的数据不存在,
     * 这种情况应该抛出异常, 包括:
     * <ul>
     * <li>
     * 如果 {@link CacheLoader#load(Object) CacheLoader.load(K)} 方法抛出的异常为 {@link Exception} 类型, 则会包装为
     * {@link ExecutionException} 类型异常抛出
     * </li>
     * <li>
     * 如果 {@link CacheLoader#load(Object) CacheLoader.load(K)} 方法抛出的异常为 {@link RuntimeException} 类型, 则会包装为
     * {@link UncheckedExecutionException} 类型异常抛出
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * {@link com.google.common.cache.LoadingCache#get(Object) LoadingCache.get(K)} 方法用于根据 Key 获取缓存的对象,
     * 如果指定的 Key 不存在, 则会通过之前定义的 {@link CacheLoader} 对象从数据源加载 (或新产生) 一个对象并返回, 或者抛出
     * {@link ExecutionException} 或 {@link UncheckedExecutionException} 类型的异常
     * </p>
     *
     * <p>
     * 对于其它如
     * {@link com.google.common.cache.LoadingCache#getIfPresent(Object) LoadingCache.getIfPresent(Object)},
     * {@link com.google.common.cache.LoadingCache#put(Object, Object) LoadingCache.put(K, V)},
     * {@link com.google.common.cache.LoadingCache#getAllPresent(Iterable) LoadingCache.getAllPresent(Iterable)} 以及
     * {@link com.google.common.cache.LoadingCache#putAll(java.util.Map) LoadingCache.putAll(Map)} 等方法,
     * 则和 {@link Cache} 类型中对应方法功能一致
     * </p>
     */
    @Test
    void builder_shouldBuildLoadingCache() {
        // 在数据源中存储两个对象
        repository.insertUser(new User(1L, "Alvin"));
        repository.insertUser(new User(2L, "Emma"));

        // 构建缓存对象, 并指定 load 方法如何从数据源读取对象
        var cache = CacheBuilder.newBuilder().build(new CacheLoader<Long, User>() {
            @Override
            public User load(Long key) {
                // 从数据源读取对象, 并在无法读取结果时抛出异常
                return repository.findUserById(key).orElseThrow();
            }
        });
        // 确认此时缓存内容为空
        then(cache.size()).isEqualTo(0);

        try {
            // 通过 Key 获取缓存对象, 确认此时会自动从数据源读取缓存对象
            var user = cache.get(1L);
            then(user).extracting("id", "name").contains(1L, "Alvin");
            // 确认此时缓存中包含了一个被缓存对象
            then(cache.size()).isEqualTo(1);
        } catch (Exception e) {
            fail();
        }

        try {
            // 再次通过另一个 Key 获取缓存对象
            var user = cache.get(2L);
            then(user).extracting("id", "name").contains(2L, "Emma");
            // 确认此时缓存中包含了两个被缓存对象
            then(cache.size()).isEqualTo(2);
        } catch (Exception e) {
            fail();
        }

        // 如果 Key 无法获取到指定数据, 确认会捕获到 UncheckedExecutionException 异常, 且是由于 NoSuchElementException 导致的异常
        thenThrownBy(() -> cache.get(3L))
                .isInstanceOf(UncheckedExecutionException.class)
                .hasCauseExactlyInstanceOf(NoSuchElementException.class);

        // 直接添加一个需缓存对象, 并确认可以正常从缓存中读取
        cache.put(3L, new User(3L, "Lucy"));
        then(cache.size()).isEqualTo(3);

        // 确认 getUnchecked 方法不会要求进行异常检查
        var user = cache.getUnchecked(3L);
        then(user).extracting("id", "name").contains(3L, "Lucy");
    }

    /**
     * 测试以缓存对象的数量为指标的淘汰策略
     *
     * <p>
     * {@link CacheBuilder#maximumSize(long)} 方法用于设置缓存存储对象的数量上限, 当超出该上限后, 每缓存一个新的对象,
     * 就会删除一个旧的缓存对象, 按缓存创建的时间从旧到新进行
     * </p>
     *
     * <p>
     * 本例通过 {@link Cache} 类型的缓存来进行演示, {@link com.google.common.cache.LoadingCache LoadingCache} 类型与其完全一致
     * </p>
     */
    @Test
    void size_shouldCacheElementEvictionBySize() {
        // 构建缓存对象, 并指定 load 方法如何从数据源读取对象
        // 设定最大的缓存对象数为 3
        var cache = CacheBuilder.newBuilder()
                .maximumSize(3)
                .build();
        // 确认此时缓存内容为空
        then(cache.size()).isEqualTo(0);

        // 向缓存中存入 3 个对象, 并确认缓存数量
        cache.put(1L, new User(1L, "Alvin"));
        cache.put(2L, new User(2L, "Emma"));
        cache.put(3L, new User(3L, "Lucy"));
        then(cache.size()).isEqualTo(3);

        // 继续向缓存中存入第 4 个对象, 确认缓存中仍只有 3 项
        cache.put(4L, new User(4L, "Arthur"));
        then(cache.size()).isEqualTo(3);

        // 确认最早存入的缓存对象已经失效
        then(cache.getIfPresent(1L)).isNull();
    }

    /**
     * 测试以缓存对象的权重和为指标的淘汰策略
     *
     * <p>
     * {@link CacheBuilder#weigher(com.google.common.cache.Weigher) CacheBuilder.weigher(Weigher)}
     * 方法用于设置被缓存对象的权重
     * </p>
     *
     * <p>
     * {@link CacheBuilder#maximumWeight(long)} 方法用于设置总权重的上限, 当被缓存对象的权重之和超过此上限, 则再缓存新对象时,
     * 会按照早到新的顺序, 从已缓存对象中删除足够的旧对象, 以保证添加新的缓存对象后, 所有被缓存对象的权重和不会超过上限
     * </p>
     *
     * <p>
     * 所以当缓存的权重和已达上限, 再添加缓存对象时, 清理已缓存对象的数量并不一定是 1 个, 而是按照时间顺序, 从最早缓存的对象开始, 逐一删除,
     * 直到缓存的权重满足上限要求
     * </p>
     */
    @Test
    void weight_shouldCacheElementEvictionByWeight() {
        // 构建缓存对象, 并指定 load 方法如何从数据源读取对象
        // 设定对象权重值为该对象的 id 属性值
        // 设定最大权重和为 8
        var cache = CacheBuilder.newBuilder()
                .maximumWeight(8L)
                .weigher((Long key, User val) -> val.id().intValue())
                .build();
        // 确认此时缓存内容为空
        then(cache.size()).isEqualTo(0);

        // 向缓存中存入 3 个对象, 并确认缓存数量, 此时权重和为 6, 未超过 8
        cache.put(1L, new User(1L, "Alvin"));
        cache.put(2L, new User(2L, "Emma"));
        cache.put(3L, new User(3L, "Lucy"));
        then(cache.size()).isEqualTo(3);

        // 继续向缓存中存入第 4 个对象, 此时权重和为 12, 超出最大值 8, 此时需要删除最早的 2 个对象, 以确保满足缓存权重和上限要求
        // 存储完毕后, 缓存的权重和为 7
        cache.put(4L, new User(4L, "Arthur"));
        then(cache.size()).isEqualTo(2);

        // 确认最早存入的缓存对象已经失效
        then(cache.getIfPresent(1L)).isNull();
        then(cache.getIfPresent(2L)).isNull();
    }

    /**
     * 测试以缓存对象的生存时间为指标的淘汰策略
     *
     * <p>
     * {@link CacheBuilder#expireAfterWrite(long, TimeUnit)} 方法用于设置被缓存对象的存活时间, 存活时间基于该缓存项的创建时间,
     * 存活时间超过设置时间的将被判定为无效缓存项目
     * </p>
     *
     * <p>
     * 缓存的有效时间判定为"懒加载"模式, 即并不会轮询被缓存对象以进行清理, 而是在添加新缓存项或者读取缓存项时, 对被缓存对象的有效时间进行判定:
     * <ul>
     * <li>
     * 再添加新缓存对象时, 判断最早的缓存项存在时间是否超过规定时间, 如果是则删除掉最早的那个缓存项, 并缓存新的缓存项, 否则只添加新的缓存项
     * </li>
     * <li>
     * 再读取某个缓存项的时候, 判断该缓存项的存在时间是否超过规定时间, 如果超过, 则从数据源重新加载此对象, 并作为新的缓存项存储
     * (或者删除此缓存项, 返回缓存对象不存在的结果)
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void time_shouldCacheElementEvictionByCreatedTime() throws Exception {
        // 构建缓存对象, 并指定 load 方法如何从数据源读取对象
        // 设定每个被缓存对象的过期时间为 2 秒
        var cache = CacheBuilder.newBuilder()
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .build();
        // 确认此时缓存内容为空
        then(cache.size()).isEqualTo(0);

        // 向缓存中存入 3 个对象
        cache.put(1L, new User(1L, "Alvin"));
        cache.put(2L, new User(2L, "Emma"));
        cache.put(3L, new User(3L, "Lucy"));
        then(cache.size()).isEqualTo(3);

        // 等待 2.1 秒钟, 令之前缓存的对象失效
        Thread.sleep(2100);

        // 添加一个新缓存项, 此时会从缓存中删除最早的一项过期项, 所以添加后缓存内对象个数仍为 3
        cache.put(4L, new User(4L, "Arthur"));
        then(cache.size()).isEqualTo(3);

        // 读取已过期的三个缓存项, 都返回 null 表示缓存不存在, 且读取完毕后, 缓存中只剩余 1 项未过期项
        then(cache.getIfPresent(1L)).isNull();
        then(cache.getIfPresent(2L)).isNull();
        then(cache.getIfPresent(3L)).isNull();
        then(cache.size()).isEqualTo(1);
    }

    /**
     * 测试以缓存对象的最后访问时间为指标的淘汰策略
     *
     * <p>
     * {@link CacheBuilder#expireAfterAccess(long, TimeUnit)} 方法用于设置被缓存对象的存活时间,
     * 存活时间基于最后一次访问该缓存项的时间, 存活时间超过设置时间的将被判定为无效缓存项目
     * </p>
     *
     * <p>
     * 和 {@link CacheBuilder#expireAfterWrite(long, TimeUnit)} 方法基本类似, 只是每次访问被缓存项后, 该缓存项的存活时间会重新计算,
     * 即淘汰冷数据, 保留热数据的意思
     * </p>
     */
    @Test
    void time_shouldCacheElementEvictionByAccessedTime() throws Exception {
        // 构建缓存对象, 并指定 load 方法如何从数据源读取对象
        // 设定每个被缓存对象的过期时间为 2 秒
        var cache = CacheBuilder.newBuilder()
                .expireAfterAccess(2, TimeUnit.SECONDS)
                .build();
        // 确认此时缓存内容为空
        then(cache.size()).isEqualTo(0);

        // 向缓存中存入 3 个对象
        cache.put(1L, new User(1L, "Alvin"));
        cache.put(2L, new User(2L, "Emma"));
        cache.put(3L, new User(3L, "Lucy"));
        then(cache.size()).isEqualTo(3);

        // 共等待 2.5 秒钟, 在等待过程中对部分缓存项持续访问, 以保证其不过期
        for (int i = 0; i < 5; i++) {
            // 保证 Key 为 1 的缓存对象不过期
            then(cache.getIfPresent(1L)).isNotNull();
            Thread.sleep(500);
        }

        // 添加一个新缓存项, 此时回淘汰掉未访问时间达到过期时间的最早的一项 (即 Key 为 2 的项), 所以添加后缓存内对象个数仍为 3
        cache.put(4L, new User(4L, "Arthur"));
        then(cache.size()).isEqualTo(3);

        // 读取之前存储的 3 个缓存项, Key 为 1 的项因为一直在访问, 所以继续存活, 其余两项都已经过期
        then(cache.getIfPresent(1L)).isNotNull();
        then(cache.getIfPresent(2L)).isNull();
        then(cache.getIfPresent(3L)).isNull();
        // 访问完毕后缓存中剩余未过期 2 项, 即 Key 为 1 和 4 的两项
        then(cache.size()).isEqualTo(2);
    }

    /**
     * 测试以 {@link Optional} 类型作为缓存对象类型
     *
     * <p>
     * 无论是 {@link CacheBuilder#build(CacheLoader)} 的参数 {@link CacheLoader} 回调, 还是
     * {@link Cache#get(Object, java.util.concurrent.Callable) Cache#get(Object, Callable)} 的参数
     * {@link java.util.concurrent.Callable Callable} 回调, 都不允许返回 {@link null} 值
     * </p>
     *
     * <p>
     * 在前面的例子 ({@link #builder_shouldBuildLoadingCache()}) 中, 若无法根据所给的 Key 值从数据源加载对象,
     * 需要通过抛出异常的方法告诉缓存调用方该对象不存在, 但这也导致了"不存在"这个结论并未被缓存, 如果频繁用这个 Key 来获取该"不存在"的对象,
     * 则每次都会从数据源尝试加载并抛出异常, 缓存在这个 Key 上不起任何作用 (俗称被穿透)
     * </p>
     *
     * <p>
     * 如果将缓存对象类型设置为 {@link Optional} 类型, 则可以部分解决上述问题, 通过返回"非空"和"为空"的 {@link Optional} 类型对象,
     * 一方面可以解决缓存返回结果必须判断 {@code null} 值的问题, 另一方面, 为空的 {@link Optional} 对象也会被缓存,
     * 从而解决缓存穿透的问题
     * </p>
     *
     * <p>
     * 本例部分解决了缓存穿透的问题, 但如果短时间内大量的无效缓存 Key 涌入, 会导致缓存中存储大量的 {@link Optional} 对象,
     * 令缓存有效值的缓存项被淘汰, 所以本例的方法适合于缓存 Key 均匀分布的清空
     * </p>
     */
    @Test
    void optional_shouldUseOptionalToHandleNullValue() {
        // 在数据源中存储对象
        repository.insertUser(new User(1L, "Alvin"));

        // 以 Optional 类型为缓存的对象类型, 从数据源返回 Optional 类型对象
        var cache = CacheBuilder.newBuilder()
                .build(new CacheLoader<Long, Optional<User>>() {
                    @Override
                    public Optional<User> load(Long key) {
                        return repository.findUserById(key);
                    }
                });

        try {
            // 通过有效的 Key 获取, 此时从数据源读取对象, 并缓存其 Optional 包装对象
            var mayUser = cache.get(1L);
            then(mayUser).isPresent().get().extracting("id", "name").contains(1L, "Alvin");
        } catch (ExecutionException e) {
            fail();
        }

        try {
            // 通过有效的 Key 获取, 此时从数据源读取到 null 值, 并缓存空 Optional 对象
            var mayUser = cache.get(2L);
            then(mayUser).isEmpty();
        } catch (ExecutionException e) {
            fail();
        }
    }

    /**
     * 在缓存中使用布隆过滤器
     *
     * <p>
     * 在 {@link #optional_shouldUseOptionalToHandleNullValue()} 范例中, 通过对不存在的对象缓存一个空 {@link Optional}
     * 对象来解决缓存穿透的问题, 但也存在存储过多无效缓存项的隐患
     * </p>
     *
     * <p>
     * 另一种方法是使用"布隆过滤器", 当缓存未命中时, 可以在布隆过滤器中查找对象标识, 如果不存在, 则返回对象不存在, 若存在, 则从数据源读取
     * </p>
     *
     * <p>
     * 布隆过滤器的优点是存储空间占用小, 查找快速, 对于新加入的对象可以立即进行标识; 缺点是无法删除对象标识.
     * 所以该方法可以有效的解决缓存穿透问题, 但只适合变化频率较低的数据
     * </p>
     */
    @Test
    void bloomFilter_shouldUseBloomFilterInCacheLoader() {
        // 在数据源中存储对象
        repository.insertUser(new User(1L, "Alvin"));
        repository.insertUser(new User(2L, "Emma"));

        // 构建缓存对象, 数据加载对象中使用布隆过滤器
        var cache = CacheBuilder.newBuilder()
                .build(new CacheLoader<Long, User>() {
                    // 创建布隆过滤器对象, 对全体 User 对象的 id 进行散列
                    private final BloomFilter<Long> bloomFilter = repository.toBloomFilter(1000);

                    @Override
                    public User load(Long key) {
                        // 通过布隆过滤器确认数据是否存在
                        if (!bloomFilter.mightContain(key)) {
                            // 数据不存在的清空
                            throw new NoSuchElementException();
                        }
                        // 数据存在的情况, 从数据源加载数据到缓存
                        return repository.findUserById(key).orElseThrow();
                    }
                });

        // 从缓存中读取数据
        try {
            var user = cache.get(1L);
            then(user).extracting("id", "name").contains(1L, "Alvin");
        } catch (ExecutionException e) {
            fail();
        }

        try {
            var user = cache.get(2L);
            then(user).extracting("id", "name").contains(2L, "Emma");
        } catch (ExecutionException e) {
            fail();
        }

        // 对于不存在的对象, 会被布隆过滤器过滤掉, 抛出异常
        thenThrownBy(() -> cache.get(3L))
                .isInstanceOf(UncheckedExecutionException.class)
                .hasCauseExactlyInstanceOf(NoSuchElementException.class);

        // 确认最终缓存了 2 个对象
        then(cache.size()).isEqualTo(2);
    }

    /**
     * 避免内存泄漏
     *
     * <p>
     * 通过 {@link CacheBuilder#weakKeys()}, {@link CacheBuilder#weakValues()} 以及 {@link CacheBuilder#softValues()}
     * 这几个方法, 可以指定使用特殊的引用方式存储缓存的键值
     * </p>
     *
     * <p>
     * 一般情况下, 缓存使用"强引用"来存储键值对, 如果缓存的淘汰机制设置不当, 缓存的对象无法得到释放, 缓存占用的内存会不断增长,
     * 从而导致类似于内存泄漏的后果
     * </p>
     *
     * <p>
     * 通过设置缓存的 Key 或 Value 为"软引用"或"弱引用", 可以解决这个问题. 对于软引用, 当虚拟机内存不足时, 会强制释放其引用的对象;
     * 对于弱引用, 每次 GC 执行时, 会强制释放其引用的对象. 这就保证了缓存的对象一直占用内存的情况
     * </p>
     *
     * <p>
     * 一般情况下, 不应该通过改变引用方式来保证缓存的内存占用, 而是应该合理的设置缓存数量上限以及淘汰策略, 防止因为 GC 导致大量缓存同时失效,
     * 从而引发缓存雪崩的问题, 但如果对于临时性的, 小规模使用的缓存 (例如在一个方法内, 对访问过的数据进行缓存, 以避免重复计算等),
     * 则可以通过这种方法避免内存泄漏问题
     * </p>
     *
     * <p>
     * 注意, 对于 {@link CacheBuilder#weakKeys()} 方法, 如果 Key 使用的是"字符串"或"数值"类型, 则有可能不起效,
     * 因为这类对象的存储位置比较特殊, 无法被 GC 释放掉
     * </p>
     */
    @Test
    void reference_shouldUseDifferentReferencesInCacheValue() {
        // 构建缓存对象, 其被缓存的对象是通过"弱引用"方式存储的
        var cache = CacheBuilder.newBuilder()
                .weakValues()
                .build();

        // 在缓存中存储 2 个对象
        cache.put(1L, new User(1L, "Alvin"));
        cache.put(2L, new User(2L, "Emma"));

        // 从缓存中获取对象
        var user = cache.getIfPresent(1L);
        then(user).extracting("id", "name").contains(1L, "Alvin");

        // 解除被缓存对象的引用, 并执行一次 GC
        // noinspection UnusedAssignment
        user = null;
        Runtime.getRuntime().gc();

        await().atMost(500, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            // 确认 GC 后, 无法再次获取到缓存对象, 已经被自动垃圾回收
            then(cache.getIfPresent(1L)).isNull();
            then(cache.getIfPresent(2L)).isNull();

            then(cache.size()).isEqualTo(0);

        });
    }

    /**
     * 测试按时间刷新缓存项
     *
     * <p>
     * 所谓的缓存刷新, 即对于已存在的缓存项, 通过其 Key 值将其 Value 从数据源重新进行加载, 以覆盖原有的缓存项 Value
     * </p>
     *
     * <p>
     * 通过 {@link CacheBuilder#refreshAfterWrite(long, TimeUnit)} 方法可以指定一个自动刷新的时间, 当缓存项存在时间超过该时间后,
     * 会自动对缓存项进行刷新
     * </p>
     */
    @Test
    void refresh_shouldReloadCacheByTimeAfterWrite() {
        repository.insertUser(new User(1L, "Alvin"));
        repository.insertUser(new User(2L, "Emma"));
        repository.insertUser(new User(3L, "Lucy"));

        // 构建缓存对象, 设置缓存项删除监听接口对象
        var cache = CacheBuilder.newBuilder()
                .refreshAfterWrite(2, TimeUnit.SECONDS)
                .build(new CacheLoader<Long, User>() {
                    @Override
                    public User load(Long key) {
                        return repository.findUserById(key).orElseThrow();
                    }
                });

        // 读取 3 次缓存, 这会导致缓存项超出设定大小, 会淘汰一项
        for (var key : ImmutableList.of(1L, 2L, 3L)) {
            try {
                cache.get(key);
            } catch (Exception ignore) {}
        }

        repository.updateUser(new User(2L, "Fiona"));

        await().atMost(3, TimeUnit.SECONDS).untilAsserted(
            () -> then(cache.get(2L)).extracting("id", "name").contains(2L, "Fiona"));
    }

    /**
     * 测试通过事件触发刷新缓存项
     *
     * <p>
     * 所谓的缓存刷新, 即对于已存在的缓存项, 通过其 Key 值将其 Value 从数据源重新进行加载, 以覆盖原有的缓存项 Value
     * </p>
     *
     * <p>
     * 通过 {@link com.google.common.cache.LoadingCache#refresh(Object) LoadingCache.refresh(K)} 方法可以刷新指定 Key
     * 的缓存项, 这种方法一般用于数据源更新后执行
     * </p>
     *
     * <p>
     * 为了解除实体对象操作和缓存操作的耦合性, 一般采用观察者模式, 以事件驱动的方式处理缓存, 本例中采用了 Guava 的消息总线订阅
     * </p>
     *
     * <p>
     * {@link CacheEventBus} 是转为缓存处理设计的消息总线类型, 通过 {@link CacheEventBus#getInstance()} 方法获取其单例对象, 再通过
     * {@link CacheEventBus#register(CacheObserver)} 方法注册在该消息总线上进行监听的观察者对象
     * </p>
     */
    @Test
    void refresh_shouldReloadCacheByEvent() throws Exception {
        repository.insertUser(new User(1L, "Alvin"));
        repository.insertUser(new User(2L, "Emma"));
        repository.insertUser(new User(3L, "Lucy"));

        // 构建缓存对象, 设置缓存项删除监听接口对象
        var cache = CacheBuilder.newBuilder()
                .build(new CacheLoader<Long, User>() {
                    @Override
                    public User load(Long key) {
                        return repository.findUserById(key).orElseThrow();
                    }
                });

        // 实例化观察者对象, 监听缓存消息总线
        CacheEventBus.getInstance().register(new CacheObserver(cache));

        // 读取 3 次缓存, 将实体对象全部加载到缓存中
        for (var key : ImmutableList.of(1L, 2L, 3L)) {
            try {
                cache.get(key);
            } catch (Exception ignore) {}
        }

        // 更新实体对象
        repository.updateUser(new User(2L, "Fiona"));
        // 确认对应的缓存也通过消息总线得以更新
        then(cache.get(2L)).extracting("id", "name").contains(2L, "Fiona");

        // 确认缓存中又 3 项
        then(cache.size()).isEqualTo(3);

        // 删除实体对象
        repository.deleteUser(2L);
        // 确认缓存中只剩余 2 项
        then(cache.size()).isEqualTo(2);
        then(cache.getIfPresent(2L)).isNull();
    }

    /**
     * 测试获取缓存使用指标
     *
     * <p>
     * {@link Cache#stats()} 方法用于获取缓存使用过程中的指标信息, 指标信息为一个 {@link com.google.common.cache.CacheStats
     * CacheStats} 类型对象
     * </p>
     *
     * <p>
     * 由于记录指标会降低缓存的性能, 所以默认情况下不记录这些指标, 如果需要对缓存参数调优, 则需要在构建缓存时手动开启指标记录. 通过
     * {@link CacheBuilder#recordStats()} 方法可以达成此目的
     * </p>
     */
    @Test
    void stat_shouldGetStatOfCache() {
        repository.insertUser(new User(1L, "Alvin"));
        repository.insertUser(new User(2L, "Emma"));
        repository.insertUser(new User(3L, "Lucy"));

        // 构建缓存对象, 开启缓存指标记录
        var cache = CacheBuilder.newBuilder()
                .maximumSize(2)
                .recordStats()
                .build(new CacheLoader<Long, User>() {
                    @Override
                    public User load(Long key) {
                        return repository.findUserById(key).orElseThrow();
                    }
                });

        // 查看初始状态指标, 此时缓存命中率为 1, 没有从数据源读取缓存数据, 也没有淘汰缓存数据
        then(formatCacheStat(cache)).isEqualTo("hit rate = 100.0%, load success count = 0, eviction count = 0");

        // 根据指定的缓存 Key 依次读取缓存数据
        for (var key : ImmutableList.of(1L, 1L, 1L, 2L, 2L, 3L, 4L)) {
            try {
                cache.get(key);
            } catch (Exception ignore) {}
        }

        // 总共从缓存中读取 7 次, 其中 3 次是直接从缓存中得到, 命中率为 42.9%
        // 读取数据源 4 次, 成功获取数据 3 次
        // 从缓存中淘汰 1 条数据
        then(formatCacheStat(cache)).isEqualTo("hit rate = 42.9%, load success count = 3, eviction count = 1");
    }

    /**
     * 监听缓存项删除操作
     *
     * <p>
     * 通过 {@link CacheBuilder#removalListener(RemovalListener)} 方法可以指定一个监听接口, 用于监听从缓存中删除项目的操作
     * </p>
     *
     * <p>
     * {@link RemovalListener#onRemoval(com.google.common.cache.RemovalNotification)
     * RemovalListener.onRemoval(RemovalNotification)} 方法通过 {@link com.google.common.cache.RemovalNotification
     * RemovalNotification} 类型参数表明被删除的缓存情况, 包括:
     * <ul>
     * <li>
     * {@link com.google.common.cache.RemovalNotification#getKey() RemovalNotification.getKey()} 和
     * {@link com.google.common.cache.RemovalNotification#getValue() RemovalNotification.getValue()}
     * 方法可以获取被删除缓存项的键值对
     * </li>
     * <li>
     * {@link com.google.common.cache.RemovalNotification#getCause() RemovalNotification.getCause()}
     * 方法可以获取该缓存项被删除的原因, 返回一个 {@link RemovalCause} 类型枚举
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * {@link RemovalCause} 枚举包括:
     * <ul>
     * <li>{@link RemovalCause#SIZE} 该缓存项是因为缓存的数量策略或权重策略被删除</li>
     * <li>{@link RemovalCause#EXPIRED} 该缓存项是因为缓存的过期策略被删除</li>
     * <li>{@link RemovalCause#COLLECTED} 该缓存项是因为使用了软引用或弱引用, 因垃圾回收被删除</li>
     * <li>{@link RemovalCause#REPLACED} 该缓存项是因为其值被更新 (缓存刷新或缓存 Value 被更改) 导致删除</li>
     * <li>{@link RemovalCause#EXPLICIT} 该缓存项是使用了 {@code invalidate} 系列方法被明确删除</li>
     * </ul>
     * </p>
     *
     * <p>
     * 如果系统架构中使用了多级缓存结构 (例如内存缓存作为一级缓存, Redis 作为二级缓存等), 监控缓存删除可以用于完成这类策略
     * </p>
     */
    @Test
    void removeListener_shouldWatchCacheElementHasBeenDeleted() {
        repository.insertUser(new User(1L, "Alvin"));
        repository.insertUser(new User(2L, "Emma"));
        repository.insertUser(new User(3L, "Lucy"));

        // 用于保存已删除缓存项的 Multimap 对象
        var removedItems = MultimapBuilder
                .hashKeys()
                .arrayListValues()
                .<RemovalCause, User>build();

        // 构建缓存对象, 设置缓存项删除监听接口对象
        var cache = CacheBuilder.newBuilder()
                .maximumSize(2)
                .removalListener((RemovalListener<Long, User>) notification -> {
                    // 确认被删除缓存项的键值对
                    then(notification.getKey()).isEqualTo(Objects.requireNonNull(notification.getValue()).id());
                    // 将被删除的缓存保存
                    removedItems.put(notification.getCause(), notification.getValue());
                })
                .build(new CacheLoader<Long, User>() {
                    @Override
                    public User load(Long key) {
                        return repository.findUserById(key).orElseThrow();
                    }
                });

        then(removedItems.size()).isEqualTo(0);

        // 读取 3 次缓存, 这会导致缓存项超出设定大小, 会淘汰一项
        for (var key : ImmutableList.of(1L, 2L, 3L)) {
            try {
                cache.get(key);
            } catch (Exception ignore) {}
        }
        // 确认删除了 1 项缓存
        then(removedItems.size()).isEqualTo(1);
        // 确定删除项是因为缓存 size 策略导致
        then(removedItems.get(RemovalCause.SIZE).get(0)).extracting("id", "name").contains(1L, "Alvin");

        // 显式删除一项
        cache.invalidate(2L);
        // 确认删除了 2 项缓存
        then(removedItems.size()).isEqualTo(2);
        // 确定删除项是因为缓存显式删除导致
        then(removedItems.get(RemovalCause.EXPLICIT).get(0)).extracting("id", "name").contains(2L, "Emma");
    }
}
