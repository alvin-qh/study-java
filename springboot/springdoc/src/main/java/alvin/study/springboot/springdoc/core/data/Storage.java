package alvin.study.springboot.springdoc.core.data;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 实体对象存储类型
 *
 * @param <T> 实体对象类型
 */
public class Storage<T> {
    // 该存储名称
    private final String name;

    // 存储实体对象的 Map 对象
    private final Map<String, T> storage = new ConcurrentHashMap<>();

    /**
     * 通过存储名称初始化对象
     *
     * @param name 当前存储的名称
     */
    public Storage(String name) {
        this.name = name;
    }

    /**
     * 通过存储名称初始化对象和实体对象的 {@link Map} 集合初始化对象
     *
     * @param name           当前存储的名称
     * @param initialDataSet 当前存储中初始化的数据字典
     */
    public Storage(String name, Map<String, T> initialDataSet) {
        this.name = name;
        this.storage.putAll(initialDataSet);
    }

    /**
     * 根据一个 {@code key} 值查询对应实体对象
     *
     * @param key          存储的 {@code key} 值
     * @param defaultValue 如果实体对象不存在时, 返回的缺省值
     * @return 查询结果
     */
    public T get(String key, T defaultValue) {
        return storage.getOrDefault(key, defaultValue);
    }

    /**
     * 根据一个 {@code key} 值查询对应实体对象
     *
     * @param key 存储的 {@code key} 值
     * @return 查询结果的 {@link Optional} 包装对象
     */
    public Optional<T> get(String key) {
        return Optional.ofNullable(storage.get(key));
    }

    /**
     * 存储一个实体对象
     *
     * @param key  实体对象对应的 {@code key} 值
     * @param data 实体对象
     */
    public void put(String key, T data) {
        storage.put(key, data);
    }

    /**
     * 存储一个实体对象字典中的所有键值对
     *
     * @param dataSet 实体对象键值对集合
     */
    public void putAll(Map<String, T> dataSet) {
        storage.putAll(dataSet);
    }

    /**
     * 当实体对象的 {@code key} 不存在时, 执行计算并存储计算结果
     *
     * @param key      实体对象对应的 {@code key} 值
     * @param computed 计算新实体对象的回调函数
     * @return {@code computed} 参数返回的结果
     */
    public T putIfAbsent(String key, Function<String, ? extends T> computed) {
        return storage.computeIfAbsent(key, computed);
    }

    /**
     * 返回存储的全部实体对象的 {@link Stream} 对象
     */
    public Stream<T> asStream() {
        return storage.values().stream();
    }

    /**
     * 获取当前存储的名称
     */
    public String getName() { return name; }
}
