package alvin.study.guice.bind.inte;

/**
 * 简单接口
 *
 * <p>
 * 需要在 {@link com.google.inject.Module Module}
 * 类型中设置该接口的绑定关系
 * </p>
 */
public interface BindDemo {
    String test();
}
