package alvin.study.guava.eventbus.handler;

import alvin.study.guava.eventbus.EventBusManager;
import alvin.study.guava.eventbus.event.Event.Action;
import alvin.study.guava.eventbus.event.UserEvent;
import alvin.study.guava.eventbus.model.User;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理 {@link User} 实体类型相关的时间订阅和事件处理
 */
public class UserHandler extends AbstractHandler {
    // 保存事件携带 User 对象的 Map 集合
    @Getter
    private final Map<Long, User> userMap = new ConcurrentHashMap<>();

    // 是否使用异步消息处理
    private final boolean asAsync;

    /**
     * 构造器
     *
     * <p>
     * {@code async} 参数为 {@code true} 会导致事件处理过程中模拟 IO 阻塞约 2 秒钟, 同时, 必须使用
     * {@link EventBusManager#registerAsyncEventBus(String)} 方法创建 {@code EventBus} 对象
     * </p>
     *
     * @param async 是否对消息进行异步处理
     */
    public UserHandler(boolean async) {
        this.asAsync = async;
    }

    @Override
    protected EventBus eventBus() {
        return EventBusManager.getInstance().getBus("REPO");
    }

    /**
     * 对事件进行处理的方法
     *
     * <p>
     * {@link Subscribe @Subscribe} 注解表示此方法为指定事件进行订阅
     * </p>
     *
     * <p>
     * 本方法会对 {@link UserEvent} 类型的事件进行处理, 即将 {@link UserEvent} 对象作为方法参数
     * </p>
     *
     * <p>
     * 所有通过 {@link EventBus#post(Object)} 方法, 且参数为 {@link UserEvent} 类型对象的调用, 都会执行到此方法中,
     * 从而完成了该方法和其它方法的解耦, 参考 {@link alvin.study.eventbus.repository.UserRepository#insertUser(User)
     * UserRepository.insertUser(User)} 方法中对事件进行发布
     * </p>
     *
     * <p>
     * 此方法有可能和发布方在一个线程中执行 (同步方式), 也有可能和发布方在不同线程执行 (异步方式), 主要看创建 {@link EventBus}
     * 对象的方式, 参考 {@link EventBusManager#registerEventBus(String)} 和
     * {@link EventBusManager#registerAsyncEventBus(String)} 方法
     * </p>
     *
     * <p>
     * 对于异步方式创建的 {@link EventBus} 对象, 且 {@link #asAsync} 字段为 {@code true} 时, 本方法会模拟 IO 阻塞约 2 秒,
     * 以体现异步消息处理的特征
     * </p>
     *
     * @param event {@link UserEvent} 类型事件对象, 表示当前方法处理的事件类型
     */
    @Subscribe
    @SneakyThrows
    public void onUserCreated(@NotNull UserEvent event) {
        var user = event.payload();
        // 为演示异常处理, 这里为 User.id 为 0 的情况抛出异常
        Preconditions.checkArgument(user.getId() > 0, "User.id property must great than 0");

        // 为异步处理方式模拟 IO 阻塞
        if (asAsync) {
            Thread.sleep(2000);
        }

        if (event.checkAction(Action.CREATE)) {
            userMap.put(user.getId(), user);
        }
    }
}
