package alvin.study.guava.future;

import alvin.study.guava.future.model.User;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于演示异步任务的用户相关服务类
 */
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
        userMap.put(user.id(), user);
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
     * @throws java.util.NoSuchElementException 要删除的 {@code id} 不存在
     */
    public ListenableFuture<User> deleteUser(long id) {
        // 通过 transformAsync 方法将查询和删除两个部分进行链式调用
        return Futures.transformAsync(
            // 查询任务
            findUserById(id),
            // 查询完成后的回调, 基于查询任务的结果, 创建删除用户任务
            mayUser -> listeningDecorator.submit(() -> mayUser.map(user -> {
                delay();
                userMap.remove(user.id());
                return user;
            }).orElseThrow()),
            MoreExecutors.directExecutor());
    }
}
