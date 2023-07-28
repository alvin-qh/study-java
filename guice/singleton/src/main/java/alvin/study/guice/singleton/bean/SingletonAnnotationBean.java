package alvin.study.guice.singleton.bean;

import jakarta.inject.Singleton;

/**
 * 用于演示注入的类型
 *
 * <p>
 * 通过 {@link Singleton @Singleton} 注解的类型表示单例模式, 单例模式的对象在容器的整个生命周期内只存在一份
 * </p>
 */
@Singleton
public class SingletonAnnotationBean {
}
