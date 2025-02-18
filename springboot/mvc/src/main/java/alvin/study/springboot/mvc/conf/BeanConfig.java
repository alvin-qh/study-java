package alvin.study.springboot.mvc.conf;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.annotation.RequestScope;

import com.auth0.jwt.algorithms.Algorithm;

import alvin.study.springboot.mvc.core.context.Context;
import alvin.study.springboot.mvc.core.context.WebContext;
import alvin.study.springboot.mvc.core.i18n.I18n;
import alvin.study.springboot.mvc.core.i18n.MessageI18n;
import alvin.study.springboot.mvc.util.security.Jwt;
import lombok.extern.slf4j.Slf4j;

/**
 * 配置当前 Application 中所有需要容器化管理的 Bean 对象
 *
 * <p>
 * 当某些 Bean 对象无法直接通过
 * {@link org.springframework.stereotype.Component @Component},
 * {@link org.springframework.stereotype.Service @Service} 等注解标记, 但又希望被 Spring
 * 对象容器管理时, 可以在当前类中提供这些类型的构建方法
 * </p>
 *
 * <p>
 * 在构建方法上增加 {@link Bean @Bean} 注解, 即表示该方法返回的对象会被送入
 * </p>
 */
@Slf4j
@Configuration("conf/bean")
public class BeanConfig {
    /**
     * 配置上下文对象, 每次请求范围内有效
     */
    @Bean
    @Lazy
    @RequestScope
    Context context() {
        var ctx = new WebContext();
        log.info("[CONF] Context \"{}\" was created", ctx.name());
        return ctx;
    }

    /**
     * 获取 i18n 对象
     */
    @Bean
    @Lazy
    @RequestScope
    I18n i18n(MessageSource messageSource) {
        var i18n = new MessageI18n(messageSource, MessageI18n.createRequestLocale());
        log.info("[CONF] I18n \"{}\" was created", i18n.getLocale().getLanguage());
        return i18n;
    }

    /**
     * 获取验证器工厂对象
     *
     * @param messageSource 国际化消息对象
     * @return 验证器工厂对象
     */
    @Bean
    LocalValidatorFactoryBean getValidator(MessageSource messageSource) {
        var bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

    /**
     * 获取 {@link Jwt} 对象
     *
     * @param key    加密密钥
     * @param jti    JWT ID
     * @param period 过期时间
     * @return {@link Jwt} 对象
     */
    @Bean
    Jwt jwt(@Value("${application.security.jwt.key}") String key,
            @Value("${application.security.jwt.jti}") String jti,
            @Value("${application.security.session.period}") String period) {
        var alg = Algorithm.HMAC256(key);
        var prd = Duration.parse(period);
        return new Jwt(alg, jti, prd);
    }
}
