package alvin.study.springboot.jooq.util.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 辅助获取 Bean 对象的工具类
 */
@Component
public class SpringBeanUtil implements ApplicationContextAware {
    // 应用上下文对象
    private static ApplicationContext context;

    /**
     * 获取 {@link ApplicationContext} 对象
     */
    public static ApplicationContext getApplicationContext() { return context; }

    /**
     * 注入 {@link ApplicationContext} 对象
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (context == null) {
            context = applicationContext;
        }
    }

    /**
     * 通过名称获取 Bean
     *
     * @param name Bean 名称
     * @return Bean 对象
     */
    public static Object getBean(String name) {
        return context.getBean(name);
    }

    /**
     * 通过类型获取 Bean
     *
     * @param clazz Bean 类型
     * @return Bean 对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    /**
     * 通过名称和类型获取 Bean
     *
     * @param name  Bean 名称
     * @param clazz Bean 类型
     * @return Bean 对象
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }
}
