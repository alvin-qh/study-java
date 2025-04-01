package alvin.study.springboot.mvc.bootstrap;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 应用监听器, 当应用初始化完毕后执行一次
 *
 * @see ApplicationListener
 * @see ApplicationReadyEvent
 */
@Slf4j
@Component
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {
    /**
     * 处理应用程序初始化完毕事件
     */
    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("Application was ready");
    }
}
