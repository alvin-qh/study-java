package alvin.study.infra.repository.common;

import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.core.data.DataSource;
import alvin.study.core.data.Storage;

/**
 * 所有持久化类型的超类
 */
public abstract class BaseRepository<T> {
    // 注入数据源对象
    @Autowired
    private DataSource dataSource;

    /**
     * 根据名称获取存储对象
     *
     * @param name 存储对象的名称
     * @return 存储对象
     */
    public Storage<T> getStorage(String name) {
        return dataSource.getStorage(name);
    }
}
