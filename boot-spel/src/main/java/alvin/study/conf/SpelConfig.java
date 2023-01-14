package alvin.study.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import alvin.study.infra.model.Value;

/**
 * 配置
 */
@Configuration("util/spel")
public class SpelConfig {
    @Bean
    ExpressionParser expressionParser() {
        return new SpelExpressionParser(
            new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, getClass().getClassLoader()));
    }

    @Bean("intValue")
    Value<Integer> intValue() {
        return new Value<>(100);
    }
}
