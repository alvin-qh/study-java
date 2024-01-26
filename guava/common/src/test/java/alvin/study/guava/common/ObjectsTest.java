package alvin.study.guava.common;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * 演示对象操作工具类
 *
 * <p>
 * {@link Objects} 以及 {@link MoreObjects} 类具备一系列对象操作的公共方法, {@link ComparisonChain} 类可以协助进行对象比较, 这几个
 * 类配合, 可以简化对象判等, 对象转字符串和对象比较等方法
 * </p>
 *
 * <p>
 * {@link Objects#equal(Object, Object)} 用于比较两个对象是否相等, 内部调用 {@link Object#equals(Object)} 方法, 和直接
 * 使用 {@link Object#equals(Object)} 方法不同, {@link Objects#equal(Object, Object)} 方法可以比较左值为 {@code null}
 * 的情况
 * <div>
 * 对于 JDK8 以上版本, 应使用 JDK 的 {@link java.util.Objects#equals(Object, Object) Objects.equals(Object, Object)}
 * 方法和 {@link java.util.Objects#deepEquals(Object, Object) Objects.deepEquals(Object, Object)} 方法
 * </div>
 * </p>
 *
 * <p>
 * {@link MoreObjects#firstNonNull(Object, Object)} 用于对两个值进行是否为 {@code null} 判断, 行为如下:
 * <ul>
 * <li>如果两个参数都不为 {@code null}, 则返回第一个参数的值</li>
 * <li>如果第一个参数为 {@code null}, 则返回第二个参数的值</li>
 * </ul>
 * </p>
 *
 * <p>
 * {@link MoreObjects#toStringHelper(Class)} 方法用于辅助将对象转为字符串表达, 返回一个 {@link MoreObjects.ToStringHelper}
 * 类型对象, 通过该对象的 {@code add} 方法和 {@code addValue} 方法生成字符串结果
 * </p>
 *
 * <p>
 * {@link ComparisonChain#start()} 方法用于返回一个 {@link ComparisonChain} 对象, 用于通过多个比较方法组织一个比较链, 规则为:
 * 链条上优先比较的结果如果非 {@code 0}, 则整体返回该结果, 否则执行链条上下一级的比较方法. 比较时可以使用
 * {@link java.util.Comparator} 对象或 {@link Ordering} 对象指定比较规则
 * </p>
 */
class ObjectsTest {
    /**
     * 判断两个对象是否相等
     *
     * <p>
     * {@link Object#equals(Object)} 用于对两个对象进行等值比较, 该方法内部调用 {@link Object#equals(Object)} 方法
     * </p>
     *
     * <p>
     * 该方法可以解决 {@link Object#equals(Object)} 方法左值为 {@code null} 的情况
     * </p>
     *
     * <p>
     * 在重写 {@link Object#equals(Object)} 方法时, 可以通过 {@link Objects#equal(Object, Object)} 方法简化操作
     * </p>
     *
     * <p>
     * 对于 JDK8 以上版本, 应使用 JDK 的 {@link java.util.Objects#equals(Object, Object)
     * Objects.equals(Object, Object)} 方法
     * </p>
     */
    @Test
    @SuppressWarnings("null")
    void equal_shouldCheckTwoObjectsIfEquals() {
        var obj1 = Integer.valueOf(100);
        var obj2 = Integer.valueOf(100);

        // 判断两个对象相等
        then(Objects.equal(obj1, obj2)).isTrue();

        // 判断两个对象不相等
        obj2 += 1;
        then(Objects.equal(obj1, obj2)).isFalse();

        // 对于两个 null 比较, 结果为相等
        then(Objects.equal(null, null)).isTrue();
    }

    /**
     * 通过多个对象计算 hash 值
     *
     * <p>
     * {@link Objects#hashCode(Object...)} 用于对多个对象计算 hash 值, 该方法内部调用 {@link Object#hashCode()} 方法
     * </p>
     *
     * <p>
     * 在重写 {@link Object#hashCode()} 方法时, 可以通过 {@link Objects#hashCode(Object...)} 方法简化操作
     * </p>
     *
     * <p>
     * 对于 JDK8 以上版本, 应使用 JDK 的 {@link java.util.Objects#hash(Object...) Objects.hash(Object...)} 方法
     * </p>
     */
    @Test
    void hashCode_shouldCalculateHashCode() {
        var obj1 = Integer.valueOf(100);
        // 计算一个对象的 hash 值
        then(Objects.hashCode(obj1)).isEqualTo(131);

        var obj2 = Integer.valueOf(100);
        // 计算多个对象的 hash 值
        then(Objects.hashCode(obj1, obj2)).isEqualTo(4161);
    }

    /**
     * 在两个引用中选择非 {@code null} 的返回
     *
     * <p>
     * {@link MoreObjects#firstNonNull(Object, Object)} 方法, 当第一个参数不为 {@code null} 时, 返回第一个参数值, 否则返回第二个
     * 参数值
     * </p>
     *
     * <p>
     * 特别的, 如果两个参数都为 {@code null}, 则抛出 {@link NullPointerException} 异常, 即该方法不允许两个参数均为 {@code null}
     * </p>
     */
    @Test
    @SuppressWarnings("null")
    void firstNonNull_shouldGetNotNullBetweenTwoReferences() {
        var obj1 = new Object();
        var obj2 = new Object();

        // 确认第一个参数不为 null, 则返回第一个参数值
        then(MoreObjects.firstNonNull(obj1, null)).isSameAs(obj1);
        then(MoreObjects.firstNonNull(obj1, obj2)).isSameAs(obj1);

        // 确认如果第一个参数为 null, 则返回第二个参数值
        then(MoreObjects.firstNonNull(null, obj2)).isSameAs(obj2);

        // 确认如果两个参数均为 null, 则抛出异常
        thenThrownBy(() -> MoreObjects.firstNonNull(null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Both parameters are null");
    }

    /**
     * 测试产生对象字符串描述的辅助方法
     *
     * <p>
     * {@link MoreObjects#toStringHelper(Object)} 方法可以协助产生所需类型对象的字符串描述, 可以简化重写 {@link Object#toString()}
     * 方法的步骤
     * </p>
     *
     * <p>
     * {@link MoreObjects#toStringHelper(Object)} 的参数只是为了取一个类名称字符串, 所以该方法还有
     * {@link MoreObjects#toStringHelper(Class)} 和 {@link MoreObjects#toStringHelper(String)} 这两个重载方法, 其功能都是
     * 一致的
     * </p>
     */
    @Test
    void toStringHelper_shouldMakeStringValueByObject() {
        var obj = (Object) 100;

        // 通过对象参数产生一个字符串
        var s = MoreObjects.toStringHelper(obj)
                .add("name", "Alvin")
                .addValue(obj)
                .toString();
        then(s).isEqualTo("Integer{name=Alvin, 100}");

        // 通过类型参数产生一个字符串
        s = MoreObjects.toStringHelper(Integer.class)
                .add("name", "Alvin")
                .addValue(obj)
                .toString();
        then(s).isEqualTo("Integer{name=Alvin, 100}");

        // 通过字符串参数产生一个字符串
        s = MoreObjects.toStringHelper("Integer")
                .add("name", "Alvin")
                .addValue(obj)
                .toString();
        then(s).isEqualTo("Integer{name=Alvin, 100}");
    }

    /**
     * 测试通过比较链对对象进行比较
     *
     * <p>
     * 在对象比较大小时, 往往需要比较对象的多个属性. {@link ComparisonChain} 类可以产生一个比较链, 以简化
     * {@link Comparator#compare(Object, Object)} 方法的重写步骤
     * </p>
     *
     * <p>
     * 比较链的基本规则是, 先按链条上优先的规则进行比较, 如果返回相等的结果, 则再通过之后的规则进行比较, 直到返回非相等结果或链条上所有的规则
     * 都比较完毕, 类似于如下语句:
     * <blockquote><pre>
     * var result = compare1(a, b);
     * if (result == 0) {
     *     result = compare2(c, d);
     *     if (result == 0) {
     *         result = compare3(a, d);
     *     }
     * }
     * return result;
     * </pre></blockquote>
     * </p>
     */
    @Test
    void compare_shouldUseChainToCompareObjects() {
        var obj1 = Integer.valueOf(100);
        var obj2 = Integer.valueOf(100);
        var obj3 = Integer.valueOf(200);

        // 定义两级比较条件, 因为 obj1 和 obj2 相等, 所以结果为 obj2 和 obj3 的比较结果
        var r = ComparisonChain.start()
                .compare(obj1, obj2)
                .compare(obj2, obj3, Ordering.explicit(obj3, obj2))
                .result();

        // 确认最终结果为 obj2 和 obj3 比较的结果
        then(r).isEqualTo(1);
    }
}
