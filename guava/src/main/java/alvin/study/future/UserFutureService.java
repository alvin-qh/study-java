package alvin.study.future;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import alvin.study.future.model.User;
import lombok.SneakyThrows;

/**
 * 用于演示异步任务的用户相关服务类
 */
@SuppressWarnings("java:S2142")
public class UserFutureService {
    // 保存用户对象的 Map
    private final Map<Long, User> userMap = new ConcurrentHashMap<>();

    // 异步任务执行器对象
    private final ListeningExecutorService listeningDecorator;

    /**
     * 构造器, 设置异步任务执行器
     *
     * @param listeningDecorator 异步任务执行器对象
     */
    public UserFutureService(ListeningExecutorService listeningDecorator) {
        this.listeningDecorator = listeningDecorator;
    }

    /**
     * 暂停线程, 模拟 IO 延迟
     */
    @SneakyThrows
    private static void delay() {
        Thread.sleep(1000);
    }

    /**
     * 异步创建用户
     *
     * @param user 要创建的实体对象
     * @return 创建用户的异步任务对象
     */
    public ListenableFuture<User> createUser(User user) {
        return listeningDecorator.submit(() -> {
            delay();
            return createUserSync(user);
        });
    }

    /**
     * 同步创建用户
     *
     * @param user 要创建的实体对象
     * @return 创建用户的异步任务对象
     */
    @VisibleForTesting
    User createUserSync(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    /**
     * 异步查询用户
     *
     * @param id 要查询的用户 {@code id}
     * @return 查询用户异步任务
     */
    public ListenableFuture<Optional<User>> findUserById(long id) {
        return listeningDecorator.submit(() -> {
            delay();
            return Optional.ofNullable(userMap.get(id));
        });
    }

    /**
     * 删除用户
     *
     * @param id 用户 {@code id}
     * @return 被删除用户对象的 {@link Optional} 包装
     */
    public ListenableFuture<User> deleteUser(long id) {
        return Futures.transformAsync(
            findUserById(id),
            mayUser -> listeningDecorator.submit(() -> mayUser.map(user -> {
                delay();
                userMap.remove(user.getId());
                return user;
            }).orElseThrow()),
            MoreExecutors.directExecutor());
    }
}
