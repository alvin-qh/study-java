package alvin.study.quarkus.web.persist;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Singleton;

/**
 * 数据源对象
 */
@Singleton
public class DataSource {
    // 对象存储
    private final Map<String, Object> data = new ConcurrentHashMap<>();

    /**
     * 存储对象
     *
     * @param key   对象的 Key
     * @param value 对象值
     */
    public void save(String key, Object value) {
        data.put(key, value);
    }

    /**
     * 读取对象
     *
     * @param <T> 对象类型
     * @param key 对象的 Key
     * @return 和 Key 对应的对象结果
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }
}
