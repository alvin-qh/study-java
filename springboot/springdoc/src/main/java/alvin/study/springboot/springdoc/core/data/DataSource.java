package alvin.study.springboot.springdoc.core.data;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源类型
 *
 * <p>
 * 数据源对象是 {@link Storage} 存储对象的集合, 通过 {@link Storage#getName()} 得到的存储名称为
 * {@code key} 进行存储
 * </p>
 */
public class DataSource {
    // 数据源中存储对象 Map 集合
    private final Map<String, Storage<?>> storages = new HashMap<>();

    /**
     * 添加一个存储对象
     *
     * @param storage 存储对象
     */
    public void addStorage(Storage<?> storage) {
        storages.put(storage.getName(), storage);
    }

    /**
     * 获取存储对象
     *
     * @param <T>  存储对象中存储的实体类型
     * @param name 存储对象的名称
     * @return 存储对象
     */
    @SuppressWarnings("unchecked")
    public <T> Storage<T> getStorage(String name) {
        return (Storage<T>) storages.get(name);
    }
}
