package alvin.study.guice.bind;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

import com.google.inject.Key;
import com.google.inject.Module;

import alvin.study.guice.bind.AnnotationBindingModule.BindDemoA;
import alvin.study.guice.bind.AnnotationBindingModule.BindDemoB;
import alvin.study.guice.bind.anno.A;
import alvin.study.guice.bind.anno.B;
import alvin.study.guice.bind.inte.BindDemo;
import jakarta.inject.Inject;

/**
 * 测试 {@link AnnotationBindingModule} 类型
 */
class AnnotationBindingModuleTest extends BaseModuleTest {
    /**
     * {@link A @A} 表示被注解的对象类型为 {@link BindDemoA} 类型
     */
    @A
    @Inject
    private BindDemo bindDemoA;

    /**
     * {@link B @B} 表示被注解的对象类型为 {@link BindDemoB} 类型
     */
    @B
    @Inject
    private BindDemo bindDemoB;

    @Override
    protected Module getModule() { return new AnnotationBindingModule(); }

    /**
     * 测试注入的对象符合预期
     */
    @Test
    void bind_shouldObjectInjected() {
        // 通过 @A 注解获取 BindDemoA 类型对象
        then(bindDemoA).isInstanceOf(BindDemoA.class);
        then(bindDemoA.test()).isEqualTo("annotation-bind-class-a");

        // 通过 @B 注解获取 BindDemoB 对象
        then(bindDemoB).isInstanceOf(BindDemoB.class);
        then(bindDemoB.test()).isEqualTo("annotation-bind-class-b");
    }

    /**
     * 测试通过编程获取目标对象
     */
    @Test
    void bind_shouldGetObjectByInjector() {
        // 通过 Injector 对象和注解类型获取绑定在 BindDemo 类型上的对象
        var bindDemoA = injector.getInstance(Key.get(BindDemo.class, A.class));
        var bindDemoB = injector.getInstance(Key.get(BindDemo.class, B.class));

        // 确认获取的对象和注入的对象一致
        then(bindDemoA).isSameAs(this.bindDemoA);
        then(bindDemoB).isSameAs(this.bindDemoB);
    }
}
