package alvin.study.springboot.mvc.conf;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

/**
 * 测试环境配置
 *
 * <p>
 * {@link Profile @Profile} 注解表示该配置类型仅在测试中生效
 * </p>
 *
 * <p>
 * {@link TestConfiguration @TestConfiguration} 注解和
 * {@link org.springframework.context.annotation.Configuration @Configuration}
 * 功能类似, 但专用于测试
 * </p>
 */
@Profile("test")
@TestConfiguration("conf/testing")
public class TestConfig {}
