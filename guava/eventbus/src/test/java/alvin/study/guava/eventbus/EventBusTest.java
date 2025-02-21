package alvin.study.guava.eventbus;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;

import alvin.study.guava.eventbus.event.Event.Action;
import alvin.study.guava.eventbus.event.UserEvent;
import alvin.study.guava.eventbus.handler.UserHandler;
import alvin.study.guava.eventbus.model.User;
import alvin.study.guava.eventbus.repository.UserRepository;

/**
 * 测试通过 {@link com.google.common.eventbus.EventBus EventBus} 通过观察者模式进行事件处理
 */
class EventBusTest {
    /**
     * 在每次测试后执行, 释放之前产生的 {@link com.google.common.eventbus.EventBus EventBus} 对象
     */
    @AfterEach
    void afterEach() {
        // 通过管理器对象释放名为 REPO 的 EventBus 对象
        EventBusManager.getInstance().releaseEventBus("REPO");
    }

    /**
     * 测试订阅和事件处理
     *
     * <p>
     * 本例中发送的事件类型为 {@link UserEvent} 类型对象
     * </p>
     *
     * <p>
     * 事件订阅在 {@link UserHandler} 类对象中进行, 由 {@link UserHandler#onUserCreated(UserEvent)} 方法处理事件
     * </p>
     *
     * <p>
     * 事件发布在 {@link UserRepository#insertUser(User)} 方法中进行
     * </p>
     */
    @Test
    void subscribe_shouldEventCanBePublishedAndSubscribed() {
        // 注册名为 REPO 的 EventBus 对象
        EventBusManager.getInstance().registerEventBus("REPO");

        // 实例化事件发送方对象
        var repository = new UserRepository();
        // 实例化事件订阅方对象
        var handler = new UserHandler(false);

        // 执行 insertUser 方法, 该方法内部会发送 UserEvent 类型事件对象
        var user = new User(1L, "Alvin");
        repository.insertUser(user);

        // 确认 UserRepository.insertUser 方法执行成功
        then(repository.getUserMap()).containsExactly(entry(user.getId(), user));
        // 确认 UserHandler.onUserCreated 方法执行成功
        then(handler.getUserMap()).containsExactly(entry(user.getId(), user));

        // 解除事件订阅
        handler.unregister();
    }

    /**
     * 测试异步订阅和事件处理
     *
     * <p>
     * 本例中发送的事件类型为 {@link UserEvent} 类型对象
     * </p>
     *
     * <p>
     * 事件订阅在 {@link UserHandler} 类对象中进行, 由 {@link UserHandler#onUserCreated(UserEvent)} 方法处理事件, 为体现异步特点,
     * 通过 {@code UserHandler(true)} 方式构造对象
     * </p>
     *
     * <p>
     * 事件发布在 {@link UserRepository#insertUser(User)} 方法中进行
     * </p>
     */
    @Test
    void async_shouldEventCanBePublishedAndSubscribedByAsync() {
        // 注册名为 REPO 的异步 EventBus 对象
        EventBusManager.getInstance().registerAsyncEventBus("REPO");

        // 实例化事件发送方对象
        var repository = new UserRepository();
        // 实例化事件订阅方对象
        var handler = new UserHandler(true);

        // 执行 insertUser 方法, 该方法内部会发送 UserEvent 类型事件对象
        var user = new User(1L, "Alvin");
        repository.insertUser(user);

        // 确认 UserRepository.insertUser 方法执行成功
        then(repository.getUserMap()).containsExactly(entry(user.getId(), user));

        // 等待 UserHandler.onUserCreated 方法在 5 秒内被执行
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> then(handler.getUserMap()).containsExactly(entry(user.getId(), user)));

        // 解除事件订阅
        handler.unregister();
    }

    /**
     * 测试事件处理过程中的异常处理
     *
     * <p>
     * {@link EventBus} 在处理订阅时, 会捕获事件处理方法抛出的所有异常, 并通过内部的一个异常处理对象对其进行处理 (注意处理方法为记录日志),
     * 并不会把异常继续传递, 也不会导致事件发送方因为异常导致的连锁失败
     * </p>
     *
     * <p>
     * 如果希望进行更为复杂的异常处理, 可以在创建 {@link EventBus} 对象时, 设置自定义的异常处理对象, 为一个
     * {@link SubscriberExceptionHandler} 类型对象
     * </p>
     */
    @Test
    void exception_shouldHandlerException() {
        var exceptions = Lists.newArrayList();

        // 注册名为 REPO 的 EventBus 对象, 并设置异常处理对象
        EventBusManager.getInstance().registerEventBus("REPO", (ex, c) -> {
            // 记录进入了异常处理程序
            exceptions.add(ex);

            // 确认处理的事件对象类型
            then(c.getEvent()).isInstanceOf(UserEvent.class);

            // 确认处理的事件对象内容
            var event = (UserEvent) c.getEvent();
            then(event.payload()).extracting("id", "name").containsExactly(0L, "Alvin");
            then(event.action()).isEqualTo(Action.CREATE);

            // 确认处理的异常相关的 EventBus 和事件订阅对象
            then(c.getEventBus()).isSameAs(EventBusManager.getInstance().getBus("REPO"));
            then(c.getSubscriber()).isInstanceOf(UserHandler.class);
            then(c.getSubscriberMethod().getName()).isEqualTo("onUserCreated");
        });

        // 实例化事件发送方对象
        var repository = new UserRepository();
        // 实例化事件订阅方对象
        var handler = new UserHandler(false);

        // 执行 insertUser 方法, 该方法内部会发送 UserEvent 类型事件对象
        // 本次测试对象的 id 为 0, 会在事件处理内部引发异常, 已测试异常处理
        var user = new User(0L, "Alvin");
        repository.insertUser(user);

        // 确认 UserRepository.insertUser 方法执行成功
        then(repository.getUserMap()).containsExactly(entry(user.getId(), user));
        // 确认 UserHandler.onUserCreated 方法执行失败
        then(handler.getUserMap()).isEmpty();

        // 确认进行了一次异常处理
        then(exceptions).hasSize(1);

        // 解除事件订阅
        handler.unregister();
    }
}
