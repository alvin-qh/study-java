package alvin.study.eventbus.repository;

import alvin.study.eventbus.EventBusManager;
import alvin.study.eventbus.event.Event.Action;
import alvin.study.eventbus.event.UserEvent;
import alvin.study.eventbus.model.User;
import com.google.common.eventbus.EventBus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link User} 类型对象持久化类
 *
 * <p>
 * {@link EventBus#post(Object)} 方法用于发布一个事件, 被发布的事件根据类型会分配给该类型订阅方法, 对该事件进行处理
 * </p>
 *
 * <p>
 * 在本例中, 发布的事件对象应该为一个实现 {@link alvin.study.eventbus.event.Event Event} 接口类型的对象
 * </p>
 */
public class UserRepository {
    // 获取指定的 EventBus 对象
    private final EventBus eventBus = EventBusManager.getInstance().getBus("REPO");

    // 存储 User 对象的 Map
    private final Map<Long, User> userMap = new ConcurrentHashMap<>();

    /**
     * 添加一个 {@link User} 对象
     *
     * @param user 要添加的 {@link User} 对象
     */
    public void insertUser(User user) {
        // 添加对象
        userMap.put(user.getId(), user);

        // 发布 UserEvent 类型事件
        eventBus.post(new UserEvent(user, Action.CREATE));
    }

    /**
     * 获取存储 {@link User} 对象的 {@link java.util.Map Map} 对象
     *
     * @return 存储 {@link User} 对象的 {@link java.util.Map Map} 对象
     */
    public Map<Long, User> getUserMap() {
        return userMap;
    }
}
