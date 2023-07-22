package alvin.study.conf;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import alvin.study.aspect.Message;

/**
 * 产生所需的 Bean 对象
 */
@Configuration("core/bean")
public class BeanConfig {
    /**
     * 在 {@link alvin.study.aspect.MethodAdvice MethodAdvice} 中使用的消息队列
     *
     * @return {@link LinkedBlockingDeque} 类型对象
     */
    @Bean
    @Qualifier("mqForMethodAdvice")
    BlockingDeque<Message> mqForMethodAdvice() {
        return new LinkedBlockingDeque<>(100);
    }

    /**
     * 在 {@link alvin.study.aspect.AnnotationAdvice AnnotationAdvice} 中使用的消息队列
     *
     * @return {@link LinkedBlockingDeque} 类型对象
     */
    @Bean
    @Qualifier("mqForAnnotationAdvice")
    BlockingDeque<Message> mqForAnnotationAdvice() {
        return new LinkedBlockingDeque<>(100);
    }
}
