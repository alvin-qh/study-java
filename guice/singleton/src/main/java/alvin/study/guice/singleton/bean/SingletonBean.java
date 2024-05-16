package alvin.study.guice.singleton.bean;

/**
 * 这个类根据是否执行了 {@code asEagerSingleton()} 来决定自身是否为单例
 */

public class SingletonBean {
    public void nothing() {
        throw new UnsupportedOperationException();
    }
}
