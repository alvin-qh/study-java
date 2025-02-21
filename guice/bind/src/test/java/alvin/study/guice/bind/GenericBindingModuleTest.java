package alvin.study.guice.bind;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 测试 {@link GenericBindingModule} 类型
 */
class GenericBindingModuleTest extends BaseModuleTest {
    /**
     * 绑定到 {@code HashSet<Integer>} 类型对象
     */
    @Inject
    private Set<Integer> intSet;

    /**
     * 绑定到 {@code TreeSet<Double>} 类型对象
     */
    @Inject
    private Set<Double> doubleSet;

    /**
     * 绑定到 {@code LinkedHashSet<String>} 类型对象
     */
    @Inject
    private Set<String> stringSet;

    @Override
    protected Module getModule() { return new GenericBindingModule(); }

    /**
     * 测试注入的对象符合预期
     */
    @Test
    void bind_shouldObjectInjected() {
        then(intSet).isInstanceOf(HashSet.class);
        then(intSet).isEmpty();

        then(doubleSet).isInstanceOf(TreeSet.class);
        then(doubleSet).containsExactlyInAnyOrder(1.1, 1.2, 1.3, 1.4);

        then(stringSet).isInstanceOf(LinkedHashSet.class);
        then(stringSet).containsExactlyInAnyOrder("a", "b", "c", "d");
    }

    /**
     * 测试通过编程获取目标对象
     */
    @Test
    void bind_shouldGetObjectByInjector() {
        var intSet = injector.getInstance(Key.get(new TypeLiteral<Set<Integer>>() {}));
        var doubleSet = injector.getInstance(Key.get(new TypeLiteral<Set<Double>>() {}));
        var strSet = injector.getInstance(Key.get(new TypeLiteral<Set<String>>() {}));

        then(intSet).isSameAs(this.intSet);
        then(doubleSet).isSameAs(this.doubleSet);
        then(strSet).isSameAs(this.stringSet);
    }
}
