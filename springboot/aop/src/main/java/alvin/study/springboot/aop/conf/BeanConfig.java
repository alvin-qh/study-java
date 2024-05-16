package alvin.study.springboot.aop.conf;

import alvin.study.springboot.aop.aspect.AnnotationAdvice;
import alvin.study.springboot.aop.aspect.Message;
import alvin.study.springboot.aop.aspect.MethodAdvice;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 产生所需的 Bean 对象
 */
@Configuration("core/bean")
public class BeanConfig {
    /**
     * 在 {@link MethodAdvice MethodAdvice} 中使用的消息队列
     *
     * @return {@link LinkedBlockingDeque} 类型对象
     */
    @Bean
    @Qualifier("mqForMethodAdvice")
    BlockingDeque<Message> mqForMethodAdvice() {
        return new LinkedBlockingDeque<>(100);
    }

    /**
     * 在 {@link AnnotationAdvice AnnotationAdvice} 中使用的消息队列
     *
     * @return {@link LinkedBlockingDeque} 类型对象
     */
    @Bean
    @Qualifier("mqForAnnotationAdvice")
    BlockingDeque<Message> mqForAnnotationAdvice() {
        return new LinkedBlockingDeque<>(100);
    }
}
