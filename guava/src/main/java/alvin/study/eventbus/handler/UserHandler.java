package alvin.study.eventbus.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import alvin.study.eventbus.EventBusManager;
import alvin.study.eventbus.event.Event.Action;
import alvin.study.eventbus.event.UserEvent;
import alvin.study.eventbus.model.User;

/**
 * 处理 {@link User} 实体类型相关的时间订阅和事件处理
 */
public class UserHandler extends AbstractHandler {
    // 保存事件携带 User 对象的 Map 集合
    private final Map<Long, User> userMap = new ConcurrentHashMap<>();

    @Override
    protected EventBus eventBus() {
        return EventBusManager.getInstance().getBus("REPO");
    }

    /**
     *
     * @param event
     */
    @Subscribe
    public void onUserCreated(UserEvent event) {
        var user = event.payload();
        // 为演示异常处理, 这里为 User.id 为 0 的情况抛出异常
        Preconditions.checkArgument(user.getId() > 0, "User.id property must great than 0");

        if (event.checkAction(Action.CREATE)) {
            userMap.put(user.getId(), user);
        }
    }

    /**
     * 获取保存 {@link User} 对象的 {@link java.util.Map Map} 对象
     *
     * @return 保存 {@link User} 对象的 {@link java.util.Map Map} 对象
     */
    public Map<Long, User> getUserMap() { return userMap; }
}
