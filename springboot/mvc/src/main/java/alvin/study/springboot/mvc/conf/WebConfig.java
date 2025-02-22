package alvin.study.springboot.mvc.conf;

import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.mvc.core.http.PathsHandlerInterceptor;

/**
 * 配置和 Web 访问相关的配置
 *
 * <p>
 * 通过重写
 * {@link WebServerFactoryCustomizer#customize(org.springframework.boot.web.server.WebServerFactory)
 * WebServerFactoryCustomizer.customize(WebServerFactory)} 方法可以对 Spring Servlet
 * 进行配置, 可配置的项目包括: 默认错误页面的 url, 服务端口号, {@code Context Path}, Http2 配置, 数据压缩等等.
 * 也可以在 {@code application.yml} 的 {@code server} 配置项来进行
 * </p>
 *
 * <p>
 * {@link WebMvcConfigurer#addInterceptors(InterceptorRegistry)} 用于注册拦截器
 * </p>
 *
 * <p>
 * {@link WebConfig#cookieLocaleResolver()} 用于获取本地化语言设置信息在 Cookie 中持久化的配置设置
 * </p>
 *
 * <p>
 * {@link WebMvcConfigurer#addResourceHandlers(ResourceHandlerRegistry)}
 * 用于设置网站静态资源的相关配置. 该设置也可以通过 {@code application.yml} 的
 * {@code spring.web.resources} 配置项进行配置
 * </p>
 *
 * <p>
 * {@link WebConfig#templateResolver()} 方法用于获取模板引擎的配置, 也可以通过
 * {@code application.yml} 的 {@code spring.thymeleaf} 配置项进行配置
 * </p>
 *
 * <p>
 * {@link WebConfig#messageSource()} 方法用于获取一个 Message 源的配置, 也可以通过
 * {@code application.yml} 的 {@code spring.messages} 配置项进行配置
 * </p>
 */
@Slf4j
@Configuration("conf/web")
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer, WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    // 静态资源缓存时间
    // private static final int STATIC_RESOURCE_CACHE_PERIOD = 3600;

    // 默认的拦截器忽略路径
    private static final String[] DEFAULT_EXCLUDE_PATTERNS = new String[] {
        "/d/**",
        "/static/**",
        "/favicon.ico"
    };

    // 请求拦截器接口集合
    private final Set<PathsHandlerInterceptor> interceptors;

    /**
     * 自定义错误页面
     *
     * <p>
     * 需提供一个地址为 {@code /error} 的 Controller 方法, 用来展示错误信息
     * </p>
     */
    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        var errorPage = new ErrorPage("/error");
        factory.addErrorPages(errorPage);

        log.info("[CONF] Error page set as \"{}\"", errorPage.getPath());
    }

    /**
     * 添加拦截器
     *
     * <p>
     * 本例中共注册了两类拦截器:
     * <ul>
     * <li>
     * {@link PathsHandlerInterceptor} 拦截器, 由 {@link WebConfig#interceptors} 集合字段注入, 在
     * {@link #addInterceptors(InterceptorRegistry)} 方法中进行注册. 这类拦截器带有
     * {@link PathsHandlerInterceptor#getPathPatterns()} 和
     * {@link PathsHandlerInterceptor#getExcludePathPatterns()} 方法, 指定了拦截器的作用范围
     * </li>
     * <li>
     * {@link LocaleChangeInterceptor} 拦截器, 用于对本地化语言文件进行切换, 通过
     * {@link LocaleChangeInterceptor#setParamName(String)} 方法设置指定
     * {@code Language Tag} 的请求参数
     * </li>
     * </ul>
     * </p>
     *
     * @see WebMvcConfigurer#addInterceptors(InterceptorRegistry)
     * @see InterceptorRegistry#addInterceptor(org.springframework.web.servlet.HandlerInterceptor)
     * @see org.springframework.web.servlet.config.annotation.InterceptorRegistration#addPathPatterns(String...)
     * @see org.springframework.web.servlet.config.annotation.InterceptorRegistration#excludePathPatterns(String...)
     * @see PathsHandlerInterceptor
     * @see PathsHandlerInterceptor#getPathPatterns()
     * @see PathsHandlerInterceptor#getExcludePathPatterns()
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 遍历拦截器集合
        for (var interceptor : this.interceptors) {
            // 获取该拦截器生效的 url 集合
            var pathPattern = interceptor.getPathPatterns();
            // 获取该拦截器要排除的 url 集合
            var excludePathPatterns = interceptor.getExcludePathPatterns();

            // 如果拦截器对象未提供需要排除的 url 集合, 则设置为默认排除 url 集合
            if (excludePathPatterns == null || excludePathPatterns.length == 0) {
                excludePathPatterns = DEFAULT_EXCLUDE_PATTERNS;
            } else {
                // 将默认需排除的 url 集合和拦截器提供的排除 url 集合合并
                var len = excludePathPatterns.length;

                excludePathPatterns = Arrays.copyOf(excludePathPatterns, len + DEFAULT_EXCLUDE_PATTERNS.length);
                System.arraycopy(
                    DEFAULT_EXCLUDE_PATTERNS, 0, excludePathPatterns, len, DEFAULT_EXCLUDE_PATTERNS.length);
            }

            // 添加拦截器
            registry
                    // 添加拦截器
                    .addInterceptor(interceptor)
                    // 添加拦截器要拦截的 url 范围
                    .addPathPatterns(pathPattern)
                    // 添加拦截器要排除的 url 范围
                    .excludePathPatterns(excludePathPatterns);

            log.info("[CONF] Add new interceptor \"{}\" on {} and exclude {}",
                interceptor.getClass().getName(), pathPattern, excludePathPatterns);
        }

        // 设置语言切换拦截器
        var interceptor = new LocaleChangeInterceptor();
        // 设置切换语言的参数名
        interceptor.setParamName("lang");
        // 注册拦截器, 并设置该拦截器的作用范围
        registry.addInterceptor(interceptor)
                .addPathPatterns("/web/**")
                .excludePathPatterns(DEFAULT_EXCLUDE_PATTERNS);

        log.info("[CONF] Add locale change interceptor \"{}\" on {} and exclude {}, lang param is \"{}\"",
            interceptor.getClass().getName(), "/web/**", DEFAULT_EXCLUDE_PATTERNS, interceptor.getParamName());
    }

    /**
     * 设置静态资源处理的相关配置
     *
     * <p>
     * 静态资源即客户端请求的 {@code .css}, {@code .js}, 图片, 音乐, 视频等不会发生变化的资源
     * </p>
     *
     * <p>
     * 也可以通过 {@code application.yml} 中的 {@code spring.web.resources} 配置项达到类似效果
     * </p>
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                // 设置静态资源 URL 访问路径
                .addResourceHandler("/static/**")
                // 设置静态资源本地存储路径, 即将 URL 路径转换为本地路径
                .addResourceLocations("classpath:/static/")
                // 添加客户端缓存配置
                // .setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES).cachePrivate())
                // 设置静态资源缓存时间
                // .setCachePeriod(STATIC_RESOURCE_CACHE_PERIOD)
                // 开启缓存
                .resourceChain(false)
                // 添加路径资源处理器
                .addResolver(new PathResourceResolver())
                // 添加编码后资源处理器, 包括 gzip 编码
                .addResolver(new EncodedResourceResolver())
                // 添加 MD5 版本处理器
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));

        log.info("[CONF] The static resources was setup");
    }

    /**
     * 获取 {@link CookieLocaleResolver} 对象, 用于指定本地化语言 tag 在 Cookie 中的存储方式
     *
     * @return {@link CookieLocaleResolver} 对象
     */
    @Bean
    CookieLocaleResolver cookieLocaleResolver() {
        // 设置存储语言 tag 在 Cookie 中存储的名称
        var resolver = new CookieLocaleResolver("__language__");
        // 设置默认本地化语言 tag
        resolver.setDefaultLocale(Locale.ENGLISH);
        // 设置 Cookie 有效期.
        resolver.setCookieMaxAge(Duration.ofSeconds(3600));
        return resolver;
    }

    /**
     * 生成 Thymeleaf 模板引擎属性设置对象
     *
     * <p>
     * 也可以通过 {@code application.yml} 的 {@code spring.thymeleaf} 设置项完成类似的设置
     * </p>
     *
     * @return {@link SpringResourceTemplateResolver} 对象
     */
    @Bean
    SpringResourceTemplateResolver templateResolver() {
        var templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(false);
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        log.info("[CONF] The resource template was setup");
        return templateResolver;
    }

    /**
     * 生成 Message 文件源对象
     *
     * <p>
     * Message 文件源, 在本例中位于 {@code classpath:/i18n/message[_*].properties},
     * 由若干文件共同组成, 表示不同语言下的 Message 信息
     * </p>
     *
     * <p>
     * 也可以通过 {@code application.yml} 配置文件中的 {@code spring.message} 配置项达到类似目的
     * </p>
     *
     * @return {@link MessageSource} 对象, 表示一个信息源对象
     */
    @Bean
    MessageSource messageSource() {
        var src = new ReloadableResourceBundleMessageSource();

        // 设置 Message 文件的基本名称
        src.setBasename("classpath:/i18n/message");
        src.setDefaultEncoding("UTF-8");

        log.info("[CONF] MessageSource \"{}\" was created", src.getBasenameSet());
        return src;
    }
}
