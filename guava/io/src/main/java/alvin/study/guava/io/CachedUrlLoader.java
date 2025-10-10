package alvin.study.guava.io;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;
import com.google.common.io.Resources;

import lombok.Getter;

/**
 * 演示通过 {@link ByteSource} 从网络或者缓存文件中读取数据
 *
 * <p>
 * 本例中利用了 {@link ByteSource} 的抽象性, 即将文件资源或者网络资源统一包装为
 * {@link ByteSource} 类型对象, 对于下游读取数据的操作来说,
 * 就无需区分到底使用了何种数据源来读取数据
 * </p>
 *
 * @see Resources#asByteSource(java.net.URL) Resources.asByteSource(URL)
 * @see MoreFiles#asByteSource(Path, java.nio.file.OpenOption...)
 *      MoreFiles.asByteSource(Path, OpenOption...)
 */
public class CachedUrlLoader implements AutoCloseable {
    // 定义一个当前用户可读写的文件权限集
    private static final FileAttribute<Set<PosixFilePermission>> FILE_ATTR_RW
        = PosixFilePermissions
                .asFileAttribute(PosixFilePermissions.fromString("rw-------"));

    // 定义更新缓存使用的线程池
    private final Executor executor = new ThreadPoolExecutor(
        1,
        10,
        60L,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(100),
        new ThreadPoolExecutor.AbortPolicy());

    // 定义保存缓存对象的 Map
    private Map<URI, CacheInfo> cacheMap = new ConcurrentHashMap<>();

    /**
     * 从网络或缓存中读取数据
     *
     * @param uri 资源 {@link URI} 对象
     * @return 从网络资源 (或缓存中) 获取到的数据
     */
    private byte[] loadResource(URI uri) throws IOException {
        final ByteSource res;

        // 获取缓存对象
        var cache = cacheMap.get(uri);

        // 如果缓存对象不存在或已过期, 则从网络资源上读取数据
        if (cache == null || cache.isExpired(30, TimeUnit.MINUTES)) {
            res = Resources.asByteSource(uri.toURL());
        } else {
            // 缓存正常, 从缓存中读取数据
            res = MoreFiles.asByteSource(cache.getPath());
        }

        // 从 ByteSource 对象中读取数据
        return res.read();
    }

    /**
     * 更新缓存
     *
     * @param uri  资源 {@link URI} 对象
     * @param data 从网络资源读取到的数据
     */
    private void updateCache(URI uri, byte[] data) {
        // 异步更新缓存
        executor.execute(() -> {
            // 获取缓存对象
            var cache = cacheMap.get(uri);
            // 如果缓存对象不存在或者已经过期, 则更新缓存对象
            if (cache == null || cache.isExpired(30, TimeUnit.MINUTES)) {
                try {
                    // 创建缓存文件并将缓存数据写入文件
                    var cacheFile = Files.createTempFile(
                        "guava-url-cache", ".tmp", FILE_ATTR_RW);

                    Files.write(
                        cacheFile,
                        data,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING);

                    // 更新缓存对象
                    cacheMap.put(uri, new CacheInfo(cacheFile));

                    // 删除原有的缓存文件
                    if (cache != null) {
                        Files.delete(cache.getPath());
                    }
                } catch (IOException ignored) {}
            }
        });
    }

    /**
     * 读取 HTML 数据
     *
     * @param uri HTML 所在网络资源地址对象
     * @return HTML 数据
     */
    public byte[] loadHTML(URI uri) throws IOException {
        Preconditions.checkState(cacheMap != null, "Current object was closed");

        // 读取资源
        var data = loadResource(uri);
        // 更新缓存
        updateCache(uri, data);

        return data;
    }

    /**
     * 读取 HTML 数据
     *
     * @param url HTML 所在网络资源地址字符串
     * @return HTML 数据
     */
    public byte[] loadHTML(String url) throws IOException {
        return loadHTML(URI.create(url));
    }

    /**
     * 关闭当前对象
     *
     * <p>
     * 关闭当前对象会清理缓存数据, 删除缓存文件
     * </p>
     */
    @Override
    public void close() {
        Map<URI, CacheInfo> localCacheMap = null;
        synchronized (this) {
            if (this.cacheMap != null) {
                // 释放缓存 Map 对象
                localCacheMap = this.cacheMap;
                this.cacheMap = null;
            }
        }

        // 释放缓存 Map 对象
        if (localCacheMap != null) {
            // 删除已有的缓存文件
            localCacheMap.forEach((_, info) -> {
                try {
                    Files.delete(info.getPath());
                } catch (IOException ignored) {}
            });
            // 情况缓存 Map 对象
            localCacheMap.clear();
        }
    }

    /**
     * 获取缓存信息
     *
     * <p>
     * 用于测试统计缓存情况使用
     * </p>
     *
     * @param uri 网络资源地址对象
     * @return 缓存对象的 {@link Optional} 对象, 如果缓存不存在则返回空对象
     */
    @VisibleForTesting
    Optional<CacheInfo> cacheInfo(URI uri) {
        return Optional.ofNullable(cacheMap.get(uri));
    }

    /**
     * 获取缓存信息
     *
     * <p>
     * 用于测试统计缓存情况使用
     * </p>
     *
     * @param url 网络资源地址字符串
     * @return 缓存对象的 {@link Optional} 对象, 如果缓存不存在则返回空对象
     */
    @VisibleForTesting
    Optional<CacheInfo> cacheInfo(String url) {
        return Optional.ofNullable(cacheMap.get(URI.create(url)));
    }

    /**
     * 保存缓存信息的类型
     */
    @Getter
    static class CacheInfo {
        // 缓存文件的路径名
        private final Path path;

        // 缓存创建时间
        private final Instant createdAt;

        /**
         * 构造器, 通过缓存文件路径构建缓存对象
         *
         * @param path 缓存文件路径
         */
        public CacheInfo(Path path) {
            this.path = path;
            this.createdAt = Instant.now();
        }

        /**
         * 查看缓存是否过期
         *
         * @param val  缓存有效时间值
         * @param unit 缓存有效时间单位
         * @return {@code false} 表示缓存已过期
         */
        public boolean isExpired(long val, TimeUnit unit) {
            return Instant.now().isAfter(createdAt.plus(val, unit.toChronoUnit()));
        }
    }
}
