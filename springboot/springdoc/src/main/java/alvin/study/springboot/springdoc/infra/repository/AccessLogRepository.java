package alvin.study.springboot.springdoc.infra.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import alvin.study.springboot.springdoc.infra.entity.AccessLog;
import alvin.study.springboot.springdoc.infra.repository.common.BaseRepository;

/**
 * 处理访问记录实体的持久化类型
 */
@Repository
public class AccessLogRepository extends BaseRepository<List<AccessLog>> {
    private static final String NAME_ACCESS_LOGS = "access-logs";

    /**
     * 插入一条访问记录
     *
     * @param log 访问记录对象
     */
    public synchronized void insert(AccessLog log) {
        var storage = getStorage(NAME_ACCESS_LOGS);
        var logs = storage.putIfAbsent(log.getUsername(), _ -> new ArrayList<>());
        logs.add(log);
    }

    /**
     * 查询所有用户的访问日志对象
     *
     * @return 所有用户访问记录对象集合
     */
    public List<AccessLog> selectAll() {
        var storage = getStorage(NAME_ACCESS_LOGS);
        return storage.asStream()
                .flatMap(List::stream)
                .sorted((left, right) -> left.getLastAccessAt().compareTo(right.getLastAccessAt()))
                .collect(Collectors.toList());
    }

    /**
     * 查询特定用户的访问记录对象
     *
     * @param username 用户名
     * @return 指定用户的访问记录集合
     */
    public List<AccessLog> selectByUsername(String username) {
        var storage = getStorage(NAME_ACCESS_LOGS);
        return storage.get(username, List.of());
    }
}
