package alvin.study.guava.common;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.math.BigInteger;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.google.common.primitives.UnsignedBytes;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedInts;
import com.google.common.primitives.UnsignedLong;
import com.google.common.primitives.UnsignedLongs;

/**
 * 针对于简单类型的一组操作方法
 *
 * <p>
 * Guava 在 {@code com.google.common.primitive} 包下包含了一组工具类, 用于针对简单对象进行操作
 * </p>
 *
 * <p>
 * 该组工具类主要是针对于简单对象数组进行操作, 避免因需要集合操作将简单对象放入集合引发的装箱和拆箱动作导致性能损耗
 * </p>
 *
 * <p>
 * 针对不同的简单类型, Guava 提供了 {@link Bytes}, {@link Shorts}, {@link Ints}, {@link Longs}, {@link Floats},
 * {@link Doubles}, {@link Chars} 以及 {@link Booleans} 等工具类, 对应不同的简单类型进行操作
 * </p>
 */
class PrimitivesTest {
    /**
     * 将简单类型元素包装为 {@link java.util.List List} 集合
     *
     * <p>
     * 通过各工具类的 {@code asList} 方法, 可以将一个简单对象数组 (或简单对象不定参数) 转化为 {@link java.util.List} 集合
     * </p>
     */
    @Test
    void asList_shouldCollectNumbersToList() {
        // 将 byte 类型数组转为 List 集合
        {
            var list = Bytes.asList((byte) 0x1, (byte) 0x2, (byte) 0x3, (byte) 0x4, (byte) 0x5);
            then(list).containsExactly((byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5);
        }

        // 将 short 类型数组转为 List 集合
        {
            var list = Shorts.asList((short) 1, (short) 2, (short) 3, (short) 4, (short) 5);
            then(list).containsExactly((short) 1, (short) 2, (short) 3, (short) 4, (short) 5);
        }

        // 将 int 类型数组转为 List 集合
        {
            var list = Ints.asList(1, 2, 3, 4, 5);
            then(list).containsExactly(1, 2, 3, 4, 5);
        }

        // 将 long 类型数组转为 List 集合
        {
            var list = Longs.asList(1, 2, 3, 4, 5);
            then(list).containsExactly(1L, 2L, 3L, 4L, 5L);
        }

        // 将 float 类型数组转为 List 集合
        {
            var list = Floats.asList(1.1f, 1.2f, 1.3f, 1.4f, 1.5f);
            then(list).containsExactly(1.1f, 1.2f, 1.3f, 1.4f, 1.5f);
        }

        // 将 double 类型数组转为 List 集合
        {
            var list = Doubles.asList(1.1, 1.2, 1.3, 1.4, 1.5);
            then(list).containsExactly(1.1, 1.2, 1.3, 1.4, 1.5);
        }

        // 将 boolean 类型数组转为 List 集合
        {
            var list = Booleans.asList(true, true, false, true, false);
            then(list).containsExactly(true, true, false, true, false);
        }

        // 将 char 类型数组转为 List 集合
        {
            var list = Chars.asList('A', 'A', 'B', 'B', 'A');
            then(list).containsExactly('A', 'A', 'B', 'B', 'A');
        }
    }

    /**
     * 将长度较长类型转为长度较短类型时, 检查数值是否越界
     *
     * <p>
     * 例如: 将一个 {@code long} 类型数值转为 {@code int} 类型时, 如果 {@code long} 类型数值超出了 {@code int} 类型的取值范围,
     * 则 Java 的强制类型转换会将超出部分丢弃, 导致转换结果错误且不可预期
     * </p>
     *
     * <p>
     * 正确的做法是, 检查 {@code long} 类型数值是否在 {@link Integer#MIN_VALUE} 和 {@link Integer#MAX_VALUE} 之间, 如果超出,
     * 则抛出异常, 如果未超出, 则进行转换
     * </p>
     *
     * <p>
     * {@link Shorts#checkedCast(long)} 和 {@link Ints#checkedCast(long)} 方法即完成上述操作, 前者是将一个整数转为
     * {@code short} 类型, 后者转为 {@link int} 类型. 如无法完成转换, 则抛出 {@link IllegalArgumentException} 异常
     * </p>
     *
     * <p>
     * 其它简单类型未提供转换检测的方法, 因为其它类型的应用场景不涉及这类转换操作; 另外, 浮点数的二进制存储和整数不同, 也不涉及此类转换检测
     * </p>
     */
    @Test
    void checkedCast_shouldConvertLongToIntegerSafely() {
        // 检查所给整数是否能转化为 short 类型
        {
            // 确认超出 short 最大值时, 转换失败
            thenThrownBy(() -> Shorts.checkedCast((long) Short.MAX_VALUE + 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Out of range");

            // 确认小于 short 最小值时, 转换失败
            thenThrownBy(() -> Shorts.checkedCast((long) Short.MIN_VALUE - 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Out of range");
        }

        // 检查所给整数是否能转化为 int 类型
        {
            // 确认超出 int 最大值时, 转换失败
            thenThrownBy(() -> Ints.checkedCast((long) Integer.MAX_VALUE + 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Out of range");

            // 确认小于 int 最小值时, 转换失败
            thenThrownBy(() -> Ints.checkedCast((long) Integer.MIN_VALUE - 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Out of range");
        }

        // 检查所给整数是否能转化为 char 类型
        {
            // 确认超出 char 最大值时, 转换失败
            thenThrownBy(() -> Chars.checkedCast((long) Character.MAX_VALUE + 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Out of range");

            // 确认小于 char 最小值时, 转换失败
            thenThrownBy(() -> Chars.checkedCast((long) Character.MIN_VALUE - 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Out of range");
        }
    }

    /**
     * 将整数转为长度较小的整数, 且限定饱和范围
     *
     * <p>
     * {@link Shorts#saturatedCast(long)} 和 {@link Ints#saturatedCast(long)} 方法在无法完成转换时采取如下策略:
     * <ul>
     * <li>
     * 若所给整数大于 {@code MAX_VALUE}, 则转换后的结果为 {@code MAX_VALUE}
     * </li>
     * <li>
     * 若所给整数小于 {@code MIN_VALUE}, 则转换后的结果为 {@code MIN_VALUE}
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 其它简单类型未提供类型转换的方法, 因为其它类型的应用场景不涉及这类转换操作; 另外, 浮点数的二进制存储和整数不同, 也不涉及此类转换
     * </p>
     */
    @Test
    void saturatedCast_shouldConvertLongToIntegerSafely() {
        // 将整数转为 short 类型
        {
            // 确认大于 Short.MAX_VALUE 的值转换后等于 Short.MAX_VALUE
            var val = Shorts.saturatedCast((long) Short.MAX_VALUE + 1);
            then(val).isEqualTo(Short.MAX_VALUE);

            // 确认小于 Short.MIN_VALUE 的值转换后等于 Short.MIN_VALUE
            val = Shorts.saturatedCast((long) Short.MIN_VALUE - 1);
            then(val).isEqualTo(Short.MIN_VALUE);
        }

        // 将整数转为 int 类型
        {
            // 确认大于 Integer.MAX_VALUE 的值转换后等于 Integer.MAX_VALUE
            var val = Ints.saturatedCast((long) Integer.MAX_VALUE + 1);
            then(val).isEqualTo(Integer.MAX_VALUE);

            // 确认小于 Integer.MIN_VALUE 的值转换后等于 Integer.MIN_VALUE
            val = Ints.saturatedCast((long) Integer.MIN_VALUE - 1);
            then(val).isEqualTo(Integer.MIN_VALUE);
        }

        // 将整数转为 char 类型
        {
            // 确认大于 Character.MAX_VALUE 的值转换后等于 Character.MAX_VALUE
            var val = Chars.saturatedCast((long) Character.MAX_VALUE + 1);
            then(val).isEqualTo(Character.MAX_VALUE);

            // 确认小于 Character.MIN_VALUE 的值转换后等于 Character.MIN_VALUE
            val = Chars.saturatedCast((long) Character.MIN_VALUE - 1);
            then(val).isEqualTo(Character.MIN_VALUE);
        }
    }

    /**
     * 将字符串转为数值, 且在转换失败后不抛出异常
     *
     * <p>
     * JDK 数值类型的 {@code parse...} 方法用于将字符串转为数值 (整数或浮点数), 且在转换失败后抛出运行时异常
     * {@link NumberFormatException} (例如 {@link Integer#parseInt(String, int)}). 这类方法返回简单类型对象
     * </p>
     *
     * <p>
     * Guava 提供了该方面转换不抛出异常的版本, 即 {@code tryParse} 方法, 例如: {@link Ints#tryParse(String, int)}, 如果转换失败,
     * 则返回 {@code null} 值. 这类方法返回引用类型对象
     * </p>
     *
     * <p>
     * 所以 Guava 的版本避免了异常捕获的开销, 但引入了对象拆箱的开销, 根据不同情况, 按需使用即可
     * </p>
     *
     * <p>
     * Guava 只为 4 中简单类型提供了该方法, 分别为: {@link Ints}, {@link Longs}, {@link Floats} 和 {@link Doubles},
     * 其它类型一般情况下不涉及此类转换 (或可被其它类型转换取代, 例如 {@link Ints#tryParse(String, int)} 可覆盖 {@code short} 类型).
     * 如需其它类型的转换, 可以使用 JDK 版本, 例如: {@link Boolean#parseBoolean(String)} 方法
     * </p>
     */
    @Test
    void tryParse_shouldParseStringIntoInteger() {
        // 将字符串解析为 Integer 类型
        {
            // 确认正确字符串的解析结果
            var val = Ints.tryParse("1010101", 10);
            then(val).isEqualTo(1010101);

            // 确认错误字符串返回 null 值
            val = Ints.tryParse("101-101", 10);
            then(val).isNull();
        }

        // 将字符串解析为 Long 类型
        {
            // 确认正确字符串的解析结果
            var val = Longs.tryParse("1010101", 10);
            then(val).isEqualTo(1010101);

            // 确认错误字符串返回 null 值
            val = Longs.tryParse("101-101", 10);
            then(val).isNull();
        }

        // 将字符串解析为 Float 类型
        {
            // 确认正确字符串的解析结果
            var val = Floats.tryParse("1.234");
            then(val).isEqualTo(1.234f);

            // 确认错误字符串返回 null 值
            val = Floats.tryParse("101-101");
            then(val).isNull();
        }

        // 将字符串解析为 Double 类型
        {
            // 确认正确字符串的解析结果
            var val = Doubles.tryParse("1.234");
            then(val).isEqualTo(1.234);

            // 确认错误字符串返回 null 值
            val = Doubles.tryParse("101-101");
            then(val).isNull();
        }
    }

    /**
     * 从 {@code byte} 数组中创建整数值
     *
     * <p>
     * 整数都是由若干个 {@code byte} 组成 (例如 {@code int} 类型由 4 个 {@code byte} 构成), 所以给定确定数量的 {@code byte},
     * 即可将它们拼装成对应类型的整数值
     * </p>
     *
     * <p>
     * Guava 提供 {@code fromBytes} 方法来完成此类操作, 不同简单类型对应的 {@code byte} 数量也不同
     * </p>
     *
     * <p>
     * 浮点类型的存储方式和整数不同, 且在不同系统上都有所差异, 所以不存在需要将浮点数和 {@code byte} 数组相互转换的情况
     * </p>
     */
    @Test
    void fromBytes_shouldConvertIntegerValueFromBytes() {
        // 将 2 个 byte 转为一个 short 数值
        {
            // 将 0x12 和 0x34 转为 0x1234
            var val = Shorts.fromBytes((byte) 0x12, (byte) 0x34);
            then(val).isEqualTo((short) 0x1234);

            // 将 [0x12, 0x34] 转为 0x1234
            val = Shorts.fromByteArray(new byte[]{ (byte) 0x12, (byte) 0x34 });
            then(val).isEqualTo((short) 0x1234);
        }

        // 将 4 个 byte 转为一个 int 数值
        {
            // 将 0x12, 0x34, 0x56 和 0x78 转为 0x12345678
            var val = Ints.fromBytes((byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78);
            then(val).isEqualTo(0x12345678);

            // 将 [0x12, 0x34, 0x56, 0x78] 转为 0x12345678
            val = Ints.fromByteArray(new byte[]{ (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78 });
            then(val).isEqualTo(0x12345678);
        }

        // 将 8 个 byte 转为一个 long 数值
        {
            // 将 0x12, 0x34, 0x56, 0x78, 0x90, 0xAB, 0xCD 和 0xEF 转为 0x1234567890ABCDEF
            var val = Longs.fromBytes(
                    (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78,
                    (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF);
            then(val).isEqualTo(0x1234567890ABCDEFL);

            // 将 [0x12, 0x34, 0x56, 0x78, 0x90, 0xAB, 0xCD, 0xEF] 转为 0x1234567890ABCDEF
            val = Longs.fromByteArray(new byte[]{
                    (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78,
                    (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF
            });
            then(val).isEqualTo(0x1234567890ABCDEFL);
        }
    }

    /**
     * 将整数转为对应的 {@code byte} 数组
     *
     * <p>
     * 整数都是由若干个 {@code byte} 组成 (例如 {@code int} 类型由 4 个 {@code byte} 构成), 所以对于给定类型的整数值,
     * 可以转换为不同长度的 {@code byte} 数组
     * </p>
     *
     * <p>
     * Guava 提供 {@code toBytes} 方法来完成此类操作, 不同简单类型得到的 {@code byte} 数量也不同
     * </p>
     *
     * <p>
     * 浮点类型的存储方式和整数不同, 且在不同系统上都有所差异, 所以不存在需要将浮点数和 {@code byte} 数组相互转换的情况
     * </p>
     */
    @Test
    void toBytes_shouldConvertIntegerValueIntoBytes() {
        // 将 short 值转为 2 个 byte 的数组
        {
            // 将 0x1234 转为 [0x12, 0x34]
            var bytes = Shorts.toByteArray((short) 0x1234);
            then(bytes).containsExactly(0x12, 0x34);
        }

        // 将 int 值转为 4 个 byte 的数组
        {
            // 将 0x12, 0x34, 0x56 和 0x78 转为 0x12345678
            var bytes = Ints.toByteArray(0x12345678);
            then(bytes).containsExactly(0x12, 0x34, 0x56, 0x78);
        }

        // 将 long 值转为 8 个 byte 的数组
        {
            // 将 0x12, 0x34, 0x56, 0x78, 0x90, 0xAB, 0xCD 和 0xEF 转为 0x1234567890ABCDEF
            var bytes = Longs.toByteArray(0x1234567890ABCDEFL);
            then(bytes).containsExactly(
                    0x12, 0x34, 0x56, 0x78,
                    0x90, 0xAB, 0xCD, 0xEF);
        }
    }

    /**
     * 在简单类型数组中查找指定元素的索引
     *
     * <p>
     * JDK 的集合类型提供了 {@link java.util.List#indexOf(Object) List.indexOf(T)} 和
     * {@link java.util.List#lastIndexOf(Object) List.lastIndexOf(T)} 方法完成在集合中进行查找, 但对于简单对象数组,
     * 则难以转化为集合对象, 在转换过程中涉及的装箱操作也会降低执行效率
     * </p>
     *
     * <p>
     * Guava 提供了 {@code indexOf} 和 {@code lastIndexOf} 可以协助在简单对象数值中执行查询, 可以查询元素的索引以及子数组的索引
     * </p>
     */
    @Test
    void indexOf_shouldFindElementIndexInArray() {
        // 在 byte 数组中进行查找
        {
            var array = new byte[]{ 0x1, 0x2, 0x3, 0x4, 0x5 };

            // 查找 byte 元素的索引
            var index = Bytes.indexOf(array, (byte) 0x2);
            then(index).isEqualTo(1);

            // 反向查找 byte 元素的索引
            index = Bytes.lastIndexOf(array, (byte) 0x4);
            then(index).isEqualTo(3);

            // 查找 byte 元素数组的索引
            index = Bytes.indexOf(array, new byte[]{ 0x3, 0x4 });
            then(index).isEqualTo(2);
        }

        // 在 short 数组中进行查找
        {
            var array = new short[]{ 1, 2, 3, 4, 5 };

            // 查找 short 元素的索引
            var index = Shorts.indexOf(array, (short) 2);
            then(index).isEqualTo(1);

            // 反向查找 short 元素的索引
            index = Shorts.lastIndexOf(array, (short) 4);
            then(index).isEqualTo(3);

            // 查找 short 元素数组的索引
            index = Shorts.indexOf(array, new short[]{ 2, 3 });
            then(index).isEqualTo(1);
        }

        // 在 int 数组中进行查找
        {
            var array = new int[]{ 1, 2, 3, 4, 5 };

            // 查找 int 元素的索引
            var index = Ints.indexOf(array, 2);
            then(index).isEqualTo(1);

            // 反向查找 int 元素的索引
            index = Ints.lastIndexOf(array, 4);
            then(index).isEqualTo(3);

            // 查找 int 元素数组的索引
            index = Ints.indexOf(array, new int[]{ 2, 3 });
            then(index).isEqualTo(1);
        }

        // 在 long 数组中进行查找
        {
            var array = new long[]{ 1, 2, 3, 4, 5 };

            // 查找 long 元素的索引
            var index = Longs.indexOf(array, 2L);
            then(index).isEqualTo(1);

            // 反向查找 long 元素的索引
            index = Longs.lastIndexOf(array, 4L);
            then(index).isEqualTo(3);

            // 查找 long 元素数组的索引
            index = Longs.indexOf(array, new long[]{ 2, 3 });
            then(index).isEqualTo(1);
        }

        // 在 long 数组中进行查找
        {
            var array = new float[]{ 0.1f, 0.2f, 0.3f, 0.4f, 0.5f };

            // 查找 long 元素的索引
            var index = Floats.indexOf(array, 0.2f);
            then(index).isEqualTo(1);

            // 反向查找 long 元素的索引
            index = Floats.lastIndexOf(array, 0.4f);
            then(index).isEqualTo(3);

            // 查找 long 元素数组的索引
            index = Floats.indexOf(array, new float[]{ 0.2f, 0.3f });
            then(index).isEqualTo(1);
        }

        // 在 double 数组中进行查找
        {
            var array = new double[]{ 0.1, 0.2, 0.3, 0.4, 0.5 };

            // 查找 double 元素的索引
            var index = Doubles.indexOf(array, 0.2);
            then(index).isEqualTo(1);

            // 反向查找 double 元素的索引
            index = Doubles.lastIndexOf(array, 0.4);
            then(index).isEqualTo(3);

            // 查找 double 元素数组的索引
            index = Doubles.indexOf(array, new double[]{ 0.2, 0.3 });
            then(index).isEqualTo(1);
        }

        // 在 char 数组中进行查找
        {
            var array = new char[]{ 'A', 'B', 'C', 'D', 'E' };

            // 查找 char 元素的索引
            var index = Chars.indexOf(array, 'B');
            then(index).isEqualTo(1);

            // 反向查找 char 元素的索引
            index = Chars.lastIndexOf(array, 'D');
            then(index).isEqualTo(3);

            // 查找 char 元素数组的索引
            index = Chars.indexOf(array, new char[]{ 'B', 'C' });
            then(index).isEqualTo(1);
        }

        // 在 boolean 数组中进行查找
        {
            var array = new boolean[]{ true, false, true };

            // 查找 boolean 元素的索引
            var index = Booleans.indexOf(array, true);
            then(index).isEqualTo(0);

            // 反向查找 boolean 元素的索引
            index = Booleans.lastIndexOf(array, true);
            then(index).isEqualTo(2);

            // 查找 boolean 元素数组的索引
            index = Booleans.indexOf(array, new boolean[]{ false, true });
            then(index).isEqualTo(1);
        }
    }

    /**
     * 将简单类型数组连接为字符串
     *
     * <p>
     * 通过各工具类的 {@code join} 方法, 可以将一个简单类型数组元素连接为字符串, 类似于 {@link String#join(CharSequence, Iterable)}
     * 方法, 但后者只接受元素类型为字符串类型的集合参数
     * </p>
     */
    @Test
    void join_shouldJoinArrayToString() {
        // 将 short 类型数组连接为字符串
        {
            var str = Shorts.join(">>", (short) 1, (short) 2, (short) 3, (short) 4, (short) 5);
            then(str).isEqualTo("1>>2>>3>>4>>5");
        }

        // 将 int 类型数组连接为字符串
        {
            var str = Ints.join(">>", 1, 2, 3, 4, 5);
            then(str).isEqualTo("1>>2>>3>>4>>5");
        }

        // 将 long 类型数组连接为字符串
        {
            var str = Longs.join(">>", 1, 2, 3, 4, 5);
            then(str).isEqualTo("1>>2>>3>>4>>5");
        }

        // 将 float 类型数组连接为字符串
        {
            var str = Floats.join(">>", 0.1f, 0.2f, 0.3f, 0.4f, 0.5f);
            then(str).isEqualTo("0.1>>0.2>>0.3>>0.4>>0.5");
        }

        // 将 double 类型数组连接为字符串
        {
            var str = Doubles.join(">>", 0.1, 0.2, 0.3, 0.4, 0.5);
            then(str).isEqualTo("0.1>>0.2>>0.3>>0.4>>0.5");
        }

        // 将 char 类型数组连接为字符串
        {
            var str = Chars.join(">>", 'A', 'B', 'C', 'D', 'E');
            then(str).isEqualTo("A>>B>>C>>D>>E");
        }
    }

    /**
     * 获取针对于各简单类型的转换器对象, 可以在字符串和简单对象之间进行转换
     *
     * <p>
     * Guava 提供了"双向转换器" {@link com.google.common.base.Converter Converter} 类型,
     * 而各个工具类都实现了各自对应的简单类型和字符串的转换
     * </p>
     *
     * <p>
     * 若一个 {@link com.google.common.base.Converter Converter} 对象可以将 A 类型值转为 B 类型, 则
     * {@link com.google.common.base.Converter#reverse() Converter.reverse()} 方法返回的对象可以将 B 类型值转为 A 类型
     * </p>
     *
     * <p>
     * Guava 为数值类型工具类提供了这类转换器, 包括: {@link Shorts#stringConverter()}, {@link Ints#stringConverter()},
     * {@link Longs#stringConverter()}, {@link Floats#stringConverter()} 和 {@link Doubles#stringConverter()} 方法
     * </p>
     */
    @Test
    void convert_shouldConvertObjectsWorked() {
        // 获取转换器, 在 short 数值和字符串之间转换
        {
            var convert = Shorts.stringConverter();

            // 将字符串转为 short 数值
            then(convert.convert("10010")).isInstanceOf(Short.class).isEqualTo((short) 10010);
            // 将集合中的字符串批量转为 short 数值
            then(convert.convertAll(ImmutableList.of("1", "2"))).containsExactly((short) 1, (short) 2);

            // 获取反向转换器对象, 将 short 数值转为字符串值
            then(convert.reverse().convert((short) 10010)).isEqualTo("10010");
        }

        // 获取转换器, 在 int 数值和字符串之间转换
        {
            var convert = Ints.stringConverter();

            // 将字符串转为 int 数值
            then(convert.convert("10010")).isInstanceOf(Integer.class).isEqualTo(10010);
            // 将集合中的字符串批量转为 int 数值
            then(convert.convertAll(ImmutableList.of("1", "2"))).containsExactly(1, 2);

            // 获取反向转换器对象, 将 int 数值转为字符串值
            then(convert.reverse().convert(10010)).isEqualTo("10010");
        }

        // 获取转换器, 在 long 数值和字符串之间转换
        {
            var convert = Longs.stringConverter();

            // 将字符串转为 long 数值
            then(convert.convert("10010")).isInstanceOf(Long.class).isEqualTo(10010L);
            // 将集合中的字符串批量转为 long 数值
            then(convert.convertAll(ImmutableList.of("1", "2"))).containsExactly(1L, 2L);

            // 获取反向转换器对象, 将 long 数值转为字符串值
            then(convert.reverse().convert(10010L)).isEqualTo("10010");
        }

        // 获取转换器, 在 float 数值和字符串之间转换
        {
            var convert = Floats.stringConverter();

            // 将字符串转为 float 数值
            then(convert.convert("100.1")).isInstanceOf(Float.class).isEqualTo(100.10f);
            // 将集合中的字符串批量转为 short 数值
            then(convert.convertAll(ImmutableList.of("0.1", "0.2"))).containsExactly(0.1f, 0.2f);

            // 获取反向转换器对象, 将 float 数值转为字符串值
            then(convert.reverse().convert(100.10f)).isEqualTo("100.1");
        }

        // 获取转换器, 在 double 数值和字符串之间转换
        {
            var convert = Doubles.stringConverter();

            // 将字符串转为 double 数值
            then(convert.convert("100.1")).isInstanceOf(Double.class).isEqualTo(100.10);
            // 将集合中的字符串批量转为 double 数值
            then(convert.convertAll(ImmutableList.of("0.1", "0.2"))).containsExactly(0.1, 0.2);

            // 获取反向转换器对象, 将 double 数值转为字符串值
            then(convert.reverse().convert(100.10)).isEqualTo("100.1");
        }
    }

    /**
     * 将多个简单对象数组连接为一个数组
     *
     * <p>
     * 简单对象工具类的 {@code concat} 方法可以将指定类型的若干简单对象数组连接为一个数组
     * </p>
     */
    @Test
    void concat_shouldConcatElementsInArraysToOneArray() {
        // 将多个 byte 数组连接为一个 byte 数组
        {
            var array = Bytes.concat(
                    new byte[]{ 0x1, 0x2 },
                    new byte[]{ 0x3, 0x4 },
                    new byte[]{ 0x5 });

            then(array).containsExactly(0x1, 0x2, 0x3, 0x4, 0x5);
        }

        // 将多个 short 数组连接为一个 short 数组
        {
            var array = Shorts.concat(
                    new short[]{ 1, 2 },
                    new short[]{ 3, 4 },
                    new short[]{ 5 });

            then(array).containsExactly(1, 2, 3, 4, 5);
        }

        // 将多个 int 数组连接为一个 int 数组
        {
            var array = Ints.concat(
                    new int[]{ 1, 2 },
                    new int[]{ 3, 4 },
                    new int[]{ 5 });

            then(array).containsExactly(1, 2, 3, 4, 5);
        }

        // 将多个 long 数组连接为一个 long 数组
        {
            var array = Longs.concat(
                    new long[]{ 1, 2 },
                    new long[]{ 3, 4 },
                    new long[]{ 5 });

            then(array).containsExactly(1L, 2L, 3L, 4L, 5L);
        }

        // 将多个 float 数组连接为一个 float 数组
        {
            var array = Floats.concat(
                    new float[]{ 0.1f, 0.2f },
                    new float[]{ 0.3f, 0.4f },
                    new float[]{ 0.5f });

            then(array).containsExactly(0.1f, 0.2f, 0.3f, 0.4f, 0.5f);
        }

        // 将多个 double 数组连接为一个 double 数组
        {
            var array = Doubles.concat(
                    new double[]{ 0.1, 0.2 },
                    new double[]{ 0.3, 0.4 },
                    new double[]{ 0.5 });

            then(array).containsExactly(0.1, 0.2, 0.3, 0.4, 0.5);
        }

        // 将多个 char 数组连接为一个 char 数组
        {
            var array = Chars.concat(
                    new char[]{ 'A', 'B' },
                    new char[]{ 'C', 'D' },
                    new char[]{ 'E' });

            then(array).containsExactly('A', 'B', 'C', 'D', 'E');
        }

        // 将多个 boolean 数组连接为一个 boolean 数组
        {
            var array = Booleans.concat(
                    new boolean[]{ true, false },
                    new boolean[]{ false, true },
                    new boolean[]{ true });

            then(array).containsExactly(true, false, false, true, true);
        }
    }

    /**
     * 在简单对象数组中查找一个值是否存在
     *
     * <p>
     * 简单对象工具类的 {@code contains} 方法用于查询一个值是否在数组中存在, 并返回 {@code true} 或 {@code false}
     * </p>
     */
    @Test
    void contains_shouldCheckArrayContainsElement() {
        // 判断指定的 byte 值是否在数组中
        {
            then(Bytes.contains(new byte[]{ 1, 2 }, (byte) 0)).isFalse();
            then(Bytes.contains(new byte[]{ 1, 2 }, (byte) 1)).isTrue();
        }

        // 判断指定的 short 值是否在数组中
        {
            then(Shorts.contains(new short[]{ 1, 2 }, (short) 0)).isFalse();
            then(Shorts.contains(new short[]{ 1, 2 }, (short) 1)).isTrue();
        }

        // 判断指定的 int 值是否在数组中
        {
            then(Ints.contains(new int[]{ 1, 2 }, 0)).isFalse();
            then(Ints.contains(new int[]{ 1, 2 }, 1)).isTrue();
        }

        // 判断指定的 long 值是否在数组中
        {
            then(Longs.contains(new long[]{ 1, 2 }, 0L)).isFalse();
            then(Longs.contains(new long[]{ 1, 2 }, 1L)).isTrue();
        }

        // 判断指定的 float 值是否在数组中
        {
            then(Floats.contains(new float[]{ 0.1f, 0.2f }, 0f)).isFalse();
            then(Floats.contains(new float[]{ 0.1f, 0.2f }, 0.1f)).isTrue();
        }

        // 判断指定的 double 值是否在数组中
        {
            then(Doubles.contains(new double[]{ 0.1, 0.2 }, 0)).isFalse();
            then(Doubles.contains(new double[]{ 0.1, 0.2 }, 0.1)).isTrue();
        }

        // 判断指定的 char 值是否在数组中
        {
            then(Chars.contains(new char[]{ 'A', 'B' }, 'C')).isFalse();
            then(Chars.contains(new char[]{ 'A', 'B' }, 'A')).isTrue();
        }

        // 判断指定的 boolean 值是否在数组中
        {
            then(Booleans.contains(new boolean[]{ true, true }, false)).isFalse();
            then(Booleans.contains(new boolean[]{ true, false }, false)).isTrue();
        }
    }

    /**
     * 从数值数组中找到最大值或最小值
     *
     * <p>
     * 对于表示数值的简单对象, 其工具类的 {@code max} 和 {@code min} 方法用于在数值中找到最大值或最小值, 这些方法包括:
     * {@link Shorts#max(short...)}/{@link Shorts#min(short...)},
     * {@link Ints#max(int...)}/{@link Ints#min(int...)},
     * {@link Longs#max(long...)}/{@link Longs#min(long...)},
     * {@link Floats#max(float...)}/{@link Floats#min(float...)} 以及
     * {@link Doubles#max(double...)}/{@link Doubles#min(double...)}
     * </p>
     *
     * <p>
     * JDK 中提供的方法 ({@link Math#max(int, int)} 和 {@link Math#min(int, int)}) 方法也为各种数值简单类型提供了重载,
     * 但只能进行两个值的比较, 多个值就得嵌套或循环调用
     * </p>
     */
    @Test
    void maxMin_shouldFindMaxOrMinValueInArray() {
        // 在若干 short 值中找到最大/最小值
        {
            var max = Shorts.max((short) 1, (short) 2, (short) 3);
            then(max).isEqualTo((short) 3);

            var min = Shorts.min((short) 1, (short) 2, (short) 3);
            then(min).isEqualTo((short) 1);
        }

        // 在若干 int 值中找到最大/最小值
        {
            var max = Ints.max(1, 2, 3);
            then(max).isEqualTo(3);

            var min = Ints.min(1, 2, 3);
            then(min).isEqualTo(1);
        }

        // 在若干 long 值中找到最大/最小值
        {
            var max = Longs.max(1L, 2L, 3L);
            then(max).isEqualTo(3L);

            var min = Longs.min(1L, 2L, 3L);
            then(min).isEqualTo(1);
        }

        // 在若干 float 值中找到最大/最小值
        {
            var max = Floats.max(0.1f, 0.2f, 0.3f);
            then(max).isEqualTo(0.3f);

            var min = Floats.min(0.1f, 0.2f, 0.3f);
            then(min).isEqualTo(0.1f);
        }

        // 在若干 double 值中找到最大/最小值
        {
            var max = Doubles.max(0.1, 0.2, 0.3);
            then(max).isEqualTo(0.3);

            var min = Doubles.min(0.1, 0.2, 0.3);
            then(min).isEqualTo(0.1);
        }
    }

    /**
     * 将简单对象数组元素进行反转
     *
     * <p>
     * 简单对象工具类的 {@code reverse} 方法可以将一个简单类型数组的元素进行倒置
     * </p>
     */
    @Test
    void reverse_shouldReverseElementsInArray() {
        // 将 byte 数组元素进行反转
        {
            var array = new byte[]{ 0x1, 0x2, 0x3 };

            Bytes.reverse(array);
            then(array).containsExactly(0x3, 0x2, 0x1);
        }

        // 将 short 数组元素进行反转
        {
            var array = new short[]{ 1, 2, 3 };

            Shorts.reverse(array);
            then(array).containsExactly(3, 2, 1);
        }

        // 将 int 数组元素进行反转
        {
            var array = new int[]{ 1, 2, 3 };

            Ints.reverse(array);
            then(array).containsExactly(3, 2, 1);
        }

        // 将 long 数组元素进行反转
        {
            var array = new long[]{ 1, 2, 3 };

            Longs.reverse(array);
            then(array).containsExactly(3, 2, 1);
        }

        // 将 float 数组元素进行反转
        {
            var array = new float[]{ 0.1f, 0.2f, 0.3f };

            Floats.reverse(array);
            then(array).containsExactly(0.3f, 0.2f, 0.1f);
        }

        // 将 double 数组元素进行反转
        {
            var array = new double[]{ 0.1, 0.2, 0.3 };

            Doubles.reverse(array);
            then(array).containsExactly(0.3, 0.2, 0.1);
        }

        // 将 char 数组元素进行反转
        {
            var array = new char[]{ 'A', 'B', 'C' };

            Chars.reverse(array);
            then(array).containsExactly('C', 'B', 'A');
        }

        // 将 boolean 数组元素进行反转
        {
            var array = new boolean[]{ false, true };

            Booleans.reverse(array);
            then(array).containsExactly(true, false);
        }
    }

    /**
     * 对简单对象数组进行逆序排序 (从大到小)
     *
     * <p>
     * JDK 的 {@link java.util.Arrays Arrays} 工具类提供了 {@link java.util.Arrays#sort(int[]) Arrays.sort(int[])}
     * 方法以及各种简单对象数组类型的重载, 可以对简单类型数组进行自然序排序 (从小到大); Guava 提供的 {@code sortDescending}
     * 方法可以进行逆序排序 (从大到小), 作为 JDK 方法的补充
     * </p>
     *
     * <p>
     * Guava 对常用数值类型提供了逆序排序方法, 包括: {@link Shorts#sortDescending(short[])},
     * {@link Ints#sortDescending(int[])}, {@link Longs#sortDescending(long[])}, {@link Floats#sortDescending(float[])},
     * {@link Doubles#sortDescending(double[])} 和 {@link Chars#sortDescending(char[])}, 对使用场景不高的 {@code byte}
     * 和 {@code boolean} 类型未提供逆序排序方法
     * </p>
     */
    @Test
    void sortDescending_shouldSortArrayByDescendingOrder() {
        // 对 short 类型数组进行逆序排序
        {
            var array = new short[]{ 0x1, 0x3, 0x2 };

            Shorts.sortDescending(array);
            then(array).containsExactly(0x3, 0x2, 0x1);
        }

        // 对 int 类型数组进行逆序排序
        {
            var array = new int[]{ 1, 3, 2 };

            Ints.sortDescending(array);
            then(array).containsExactly(3, 2, 1);
        }

        // 对 long 类型数组进行逆序排序
        {
            var array = new long[]{ 1, 3, 2 };

            Longs.sortDescending(array);
            then(array).containsExactly(3, 2, 1);
        }

        // 对 float 类型数组进行逆序排序
        {
            var array = new float[]{ 0.1f, 0.3f, 0.2f };

            Floats.sortDescending(array);
            then(array).containsExactly(0.3f, 0.2f, 0.1f);
        }

        // 对 double 类型数组进行逆序排序
        {
            var array = new double[]{ 0.1, 0.3, 0.2 };

            Doubles.sortDescending(array);
            then(array).containsExactly(0.3, 0.2, 0.1);
        }

        // 对 char 类型数组进行逆序排序
        {
            var array = new char[]{ 'A', 'C', 'B' };

            Chars.sortDescending(array);
            then(array).containsExactly('C', 'B', 'A');
        }
    }

    /**
     * 旋转数组元素
     *
     * <p>
     * 该方法相当于 {@link java.util.Collections#rotate(java.util.List, int) Collections.rotate(List, int)}
     * 的简单类型数组版本
     * </p>
     */
    @Disabled
    void rotate_shouldRotateArrayElements() {
        // todo: 下个版本会增加 Ints.rotate 等方法, 届时增加演示
    }

    /**
     * 为数组扩展新的存储空间
     *
     * <p>
     * 简单对象工具类的 {@code ensureCapacity} 方法用于对数组空间进行扩展, 即将产生一个空间更大的新数组, 将原数组的内容复制到新数组,
     * 其余空间使用 {@code 0} 或 {@code false} 填充
     * </p>
     *
     * <p>
     * {@code ensureCapacity(array, minLength, padding)}, 第一个参数即原数组; 第二个参数为扩展的最小长度, 如果该值小于等于原数组长度,
     * 则不对原数组进行扩展, 直接返回原数组引用; 第三个参数可以为 {@code 0}, 也可以为一个增长值. 后两个参数表达的语义为:
     * 将数组扩长度扩展到 {@code minLength} 大小, 并为之后扩展预留 {@code padding} 个位置. 所以最终返回的数组长度为
     * {@code minLength + padding}
     * </p>
     */
    @Test
    void ensureCapacity_shouldGuaranteeTheLengthOfArrayForNewElements() {
        // 扩展 byte 数组长度
        {
            var array = new byte[]{ 1, 2, 3, 4, 5 };

            // 设置最小扩展长度和原数组相等, 此时直接返回原数组引用
            var newArray = Bytes.ensureCapacity(array, array.length, 0);
            then(newArray).isSameAs(array);

            // 设置最小扩展长度为原数组长度 + 1, 返回新数组比原数组长度 + 1
            newArray = Bytes.ensureCapacity(array, array.length + 1, 0);
            then(newArray).hasSize(array.length + 1).contains(array);

            // 设置最小扩展长度为原数组长度 + 1, 且预留 5 个元素空间, 返回新数组比原数组长度 + 6
            newArray = Bytes.ensureCapacity(array, array.length + 1, 5);
            then(newArray).hasSize(array.length + 6).contains(array);
        }

        // 扩展 short 数组长度
        {
            var array = new short[]{ 1, 2, 3, 4, 5 };

            // 设置最小扩展长度和原数组相等, 此时直接返回原数组引用
            var newArray = Shorts.ensureCapacity(array, array.length, 0);
            then(newArray).isSameAs(array);

            // 设置最小扩展长度为原数组长度 + 1, 返回新数组比原数组长度 + 1
            newArray = Shorts.ensureCapacity(array, array.length + 1, 0);
            then(newArray).hasSize(array.length + 1).contains(array);

            newArray = Shorts.ensureCapacity(array, array.length + 1, 5);
            then(newArray).hasSize(array.length + 6).contains(array);
        }

        // 扩展 int 数组长度
        {
            var array = new int[]{ 1, 2, 3, 4, 5 };

            // 设置最小扩展长度和原数组相等, 此时直接返回原数组引用
            var newArray = Ints.ensureCapacity(array, array.length, 0);
            then(newArray).isSameAs(array);

            // 设置最小扩展长度为原数组长度 + 1, 返回新数组比原数组长度 + 1
            newArray = Ints.ensureCapacity(array, array.length + 1, 0);
            then(newArray).hasSize(array.length + 1).contains(array);

            // 设置最小扩展长度为原数组长度 + 1, 且预留 5 个元素空间, 返回新数组比原数组长度 + 6
            newArray = Ints.ensureCapacity(array, array.length + 1, 5);
            then(newArray).hasSize(array.length + 6).contains(array);
        }

        // 扩展 long 数组长度
        {
            var array = new long[]{ 1, 2, 3, 4, 5 };

            // 设置最小扩展长度和原数组相等, 此时直接返回原数组引用
            var newArray = Longs.ensureCapacity(array, array.length, 0);
            then(newArray).isSameAs(array);

            // 设置最小扩展长度为原数组长度 + 1, 返回新数组比原数组长度 + 1
            newArray = Longs.ensureCapacity(array, array.length + 1, 0);
            then(newArray).hasSize(array.length + 1).contains(array);

            // 设置最小扩展长度为原数组长度 + 1, 且预留 5 个元素空间, 返回新数组比原数组长度 + 6
            newArray = Longs.ensureCapacity(array, array.length + 1, 5);
            then(newArray).hasSize(array.length + 6).contains(array);
        }

        // 扩展 float 数组长度
        {
            var array = new float[]{ 0.1f, 0.2f, 0.3f, 0.4f, 0.5f };

            // 设置最小扩展长度和原数组相等, 此时直接返回原数组引用
            var newArray = Floats.ensureCapacity(array, array.length, 0);
            then(newArray).isSameAs(array);

            // 设置最小扩展长度为原数组长度 + 1, 返回新数组比原数组长度 + 1
            newArray = Floats.ensureCapacity(array, array.length + 1, 0);
            then(newArray).hasSize(array.length + 1).contains(array);

            // 设置最小扩展长度为原数组长度 + 1, 且预留 5 个元素空间, 返回新数组比原数组长度 + 6
            newArray = Floats.ensureCapacity(array, array.length + 1, 5);
            then(newArray).hasSize(array.length + 6).contains(array);
        }

        // 扩展 double 数组长度
        {
            var array = new double[]{ 0.1, 0.2, 0.3, 0.4, 0.5 };

            // 设置最小扩展长度和原数组相等, 此时直接返回原数组引用
            var newArray = Doubles.ensureCapacity(array, array.length, 0);
            then(newArray).isSameAs(array);

            // 设置最小扩展长度为原数组长度 + 1, 返回新数组比原数组长度 + 1
            newArray = Doubles.ensureCapacity(array, array.length + 1, 0);
            then(newArray).hasSize(array.length + 1).contains(array);

            // 设置最小扩展长度为原数组长度 + 1, 且预留 5 个元素空间, 返回新数组比原数组长度 + 6
            newArray = Doubles.ensureCapacity(array, array.length + 1, 5);
            then(newArray).hasSize(array.length + 6).contains(array);
        }

        // 扩展 char 数组长度
        {
            var array = new char[]{ 'A', 'B', 'C' };

            // 设置最小扩展长度和原数组相等, 此时直接返回原数组引用
            var newArray = Chars.ensureCapacity(array, array.length, 0);
            then(newArray).isSameAs(array);

            // 设置最小扩展长度为原数组长度 + 1, 返回新数组比原数组长度 + 1
            newArray = Chars.ensureCapacity(array, array.length + 1, 0);
            then(newArray).hasSize(array.length + 1).contains(array);

            // 设置最小扩展长度为原数组长度 + 1, 且预留 5 个元素空间, 返回新数组比原数组长度 + 6
            newArray = Chars.ensureCapacity(array, array.length + 1, 5);
            then(newArray).hasSize(array.length + 6).contains(array);
        }

        // 扩展 boolean 数组长度
        {
            var array = new boolean[]{ true, false, true };

            // 设置最小扩展长度和原数组相等, 此时直接返回原数组引用
            var newArray = Booleans.ensureCapacity(array, array.length, 0);
            then(newArray).isSameAs(array);

            // 设置最小扩展长度为原数组长度 + 1, 返回新数组比原数组长度 + 1
            newArray = Booleans.ensureCapacity(array, array.length + 1, 0);
            then(newArray).hasSize(array.length + 1).contains(array);

            // 设置最小扩展长度为原数组长度 + 1, 且预留 5 个元素空间, 返回新数组比原数组长度 + 6
            newArray = Booleans.ensureCapacity(array, array.length + 1, 5);
            then(newArray).hasSize(array.length + 6).contains(array);
        }
    }

    /**
     * 将变量值修正到指定区间内
     *
     * <p>
     * 简单类型工具类的 {@code constrainToRange} 方法可以将一个变量值进行修正, 让其符合指定区间. 即, 如果所给变量值在区间范围内,
     * 则返回该变量值; 如果所给变量小于区间下限, 则返回区间下限值; 如果所给变量大于区间上限值, 则返回区间上限值
     * </p>
     *
     * <p>
     * 区间由后两个参数指定, 为闭区间, 限定了返回值的取值范围
     * </p>
     *
     * <p>
     * 除 {@link Bytes} 类外, 其它简单类型工具类都包含 {@code constrainToRange} 方法
     * </p>
     */
    @Test
    void constrainToRange_shouldConstrainValueInRange() {
        // 获取指定范围内的 short 值
        {
            // 在 [1..9] 范围内取值 6, 返回 6
            var n = Shorts.constrainToRange((short) 6, (short) 1, (short) 9);
            then(n).isEqualTo((short) 6);

            // 在 [1..9] 范围内取值 0, 返回 1
            n = Shorts.constrainToRange((short) 0, (short) 1, (short) 9);
            then(n).isEqualTo((short) 1);

            // 在 [1..9] 范围内取值 10, 返回 9
            n = Shorts.constrainToRange((short) 10, (short) 1, (short) 9);
            then(n).isEqualTo((short) 9);
        }

        // 获取指定范围内的 int 值
        {
            // 在 [1..9] 范围内取值 6, 返回 6
            var n = Ints.constrainToRange(6, 1, 9);
            then(n).isEqualTo(6);

            // 在 [1..9] 范围内取值 0, 返回 1
            n = Ints.constrainToRange(0, 1, 9);
            then(n).isEqualTo(1);

            n = Ints.constrainToRange(10, 1, 9);
            then(n).isEqualTo(9);
        }

        // 获取指定范围内的 long 值
        {
            // 在 [1..9] 范围内取值 6, 返回 6
            var n = Longs.constrainToRange(6L, 1L, 9L);
            then(n).isEqualTo(6L);

            // 在 [1..9] 范围内取值 0, 返回 1
            n = Longs.constrainToRange(0L, 1L, 9L);
            then(n).isEqualTo(1L);

            // 在 [1..9] 范围内取值 10, 返回 9
            n = Longs.constrainToRange(10L, 1L, 9L);
            then(n).isEqualTo(9L);
        }

        // 获取指定范围内的 float 值
        {
            // 在 [0.1..0.9] 范围内取值 0.6, 返回 0.6
            var n = Floats.constrainToRange(0.6f, 0.1f, 0.9f);
            then(n).isEqualTo(0.6f);

            // 在 [0.1..0.9] 范围内取值 0, 返回 0.1
            n = Floats.constrainToRange(0f, 0.1f, 0.9f);
            then(n).isEqualTo(0.1f);

            // 在 [0.1..0.9] 范围内取值 1.0, 返回 0.9
            n = Floats.constrainToRange(1.0f, 0.1f, 0.9f);
            then(n).isEqualTo(0.9f);
        }

        // 获取指定范围内的 double 值
        {
            // 在 [0.1..0.9] 范围内取值 0.6, 返回 0.6
            var n = Doubles.constrainToRange(0.6, 0.1, 0.9);
            then(n).isEqualTo(0.6);

            // 在 [0.1..0.9] 范围内取值 0, 返回 0.1
            n = Doubles.constrainToRange(0, 0.1, 0.9);
            then(n).isEqualTo(0.1);

            // 在 [0.1..0.9] 范围内取值 1.0, 返回 0.9
            n = Doubles.constrainToRange(1.0, 0.1, 0.9);
            then(n).isEqualTo(0.9);
        }

        // 获取指定范围内的 char 值
        {
            // 在 [B..E] 范围内取值 D, 返回 D
            var n = Chars.constrainToRange('D', 'B', 'E');
            then(n).isEqualTo('D');

            // 在 [B..E] 范围内取值 A, 返回 B
            n = Chars.constrainToRange('A', 'B', 'E');
            then(n).isEqualTo('B');

            // 在 [B..E] 范围内取值 F, 返回 E
            n = Chars.constrainToRange('F', 'B', 'E');
            then(n).isEqualTo('E');
        }
    }

    /**
     * 比较两个简单类型的数组
     *
     * <p>
     * 简单类型工具类的 {@code lexicographicalComparator} 方法返回一个比较器对象, 可以对特定类型的数组进行比较. 比较规则是:
     * 逐元素进行比较, 每个元素通过字典序进行比较 (即数字按大小比较, 字符按字典顺序进行比较)
     * </p>
     *
     * <p>
     * 除 {@link Bytes} 类外, 其它简单类型工具类都包含 {@code constrainToRange} 方法
     * </p>
     */
    @Test
    void lexicographicalComparator_shouldCompareTwoArraysByLexicographicalOrder() {
        // 对 short 类型数组进行比较
        {
            var comparator = Shorts.lexicographicalComparator();

            then(comparator.compare(new short[]{ 1, 2, 3 }, new short[]{ 1, 2, 3 })).isEqualTo(0);
            then(comparator.compare(new short[]{ 1, 2, 3, 4 }, new short[]{ 1, 2, 3 })).isEqualTo(1);
            then(comparator.compare(new short[]{ 1, 2 }, new short[]{ 1, 2, 3 })).isEqualTo(-1);
        }

        // 对 int 类型数组进行比较
        {
            var comparator = Ints.lexicographicalComparator();

            then(comparator.compare(new int[]{ 1, 2, 3 }, new int[]{ 1, 2, 3 })).isEqualTo(0);
            then(comparator.compare(new int[]{ 1, 2, 3, 4 }, new int[]{ 1, 2, 3 })).isEqualTo(1);
            then(comparator.compare(new int[]{ 1, 2 }, new int[]{ 1, 2, 3 })).isEqualTo(-1);
        }

        // 对 long 类型数组进行比较
        {
            var comparator = Longs.lexicographicalComparator();

            then(comparator.compare(new long[]{ 1L, 2L, 3L }, new long[]{ 1L, 2L, 3L })).isEqualTo(0);
            then(comparator.compare(new long[]{ 1L, 2L, 3L, 4L }, new long[]{ 1L, 2L, 3L })).isEqualTo(1);
            then(comparator.compare(new long[]{ 1L, 2L }, new long[]{ 1L, 2L, 3L })).isEqualTo(-1);
        }

        // 对 float 类型数组进行比较
        {
            var comparator = Floats.lexicographicalComparator();

            then(comparator.compare(new float[]{ 0.1f, 0.2f, 0.3f }, new float[]{ 0.1f, 0.2f, 0.3f })).isEqualTo(0);
            then(comparator.compare(
                    new float[]{ 0.1f, 0.2f, 0.3f, 0.4f },
                    new float[]{ 0.1f, 0.2f, 0.3f })).isEqualTo(1);
            then(comparator.compare(new float[]{ 0.1f, 0.2f }, new float[]{ 0.1f, 0.2f, 0.3f })).isEqualTo(-1);
        }

        // 对 double 类型数组进行比较
        {
            var comparator = Doubles.lexicographicalComparator();

            then(comparator.compare(new double[]{ 0.1, 0.2, 0.3 }, new double[]{ 0.1, 0.2, 0.3 })).isEqualTo(0);
            then(comparator.compare(new double[]{ 0.1, 0.2, 0.3, 0.4 }, new double[]{ 0.1, 0.2, 0.3 })).isEqualTo(1);
            then(comparator.compare(new double[]{ 0.1, 0.2 }, new double[]{ 0.1, 0.2, 0.3 })).isEqualTo(-1);
        }

        // 对 char 类型数组进行比较
        {
            var comparator = Chars.lexicographicalComparator();

            then(comparator.compare(new char[]{ 'A', 'B', 'C' }, new char[]{ 'A', 'B', 'C' })).isEqualTo(0);
            then(comparator.compare(new char[]{ 'A', 'B', 'C', 'D' }, new char[]{ 'A', 'B', 'C' })).isEqualTo(1);
            then(comparator.compare(new char[]{ 'A', 'B' }, new char[]{ 'A', 'B', 'C' })).isEqualTo(-1);
        }

        // 对 boolean 类型数组进行比较
        {
            var comparator = Booleans.lexicographicalComparator();

            then(comparator.compare(
                    new boolean[]{ true, false, true },
                    new boolean[]{ true, false, true })).isEqualTo(0);
            then(comparator.compare(
                    new boolean[]{ true, false, true, false },
                    new boolean[]{ true, false, true })).isEqualTo(1);
            then(comparator.compare(new boolean[]{ true, false }, new boolean[]{ true, false, true })).isEqualTo(-1);
        }
    }

    /**
     * 将引用类型集合转为简单类型数组
     *
     * <p>
     * 简单类型工具类的 {@code toArray} 方法可以将一个引用类型集合对象转为简单类型数组对象,
     * 在一个方法内同时完成集合转数组以及集合元素拆箱的工作
     * </p>
     *
     * <p>
     * 在 Java 中, 集合存储的必须为 {@link Object} 引用类型 (例如: {@code List<Integer>}), 而数组允许存储简单类型
     * (例如: {@code int[]}), 通过 {@link java.util.List#toArray()} 方法获得的是引用类型数组, 需要逐元素拆箱才能得到简单对象数组,
     * 该方法简化了这类操作
     * </p>
     */
    @Test
    void toArray_shouldConvertCollectionToArray() {
        // 将 Collection<Byte> 转为 byte[]
        {
            var array = Bytes.toArray(ImmutableList.of(0x1, 0x2, 0x3));
            then(array).isInstanceOf(byte[].class).containsExactly(0x1, 0x2, 0x3);
        }

        // 将 Collection<Sort> 转为 short[]
        {
            var array = Shorts.toArray(ImmutableList.of(1, 2, 3));
            then(array).isInstanceOf(short[].class).containsExactly(1, 2, 3);
        }

        // 将 Collection<Integer> 转为 int[]
        {
            var array = Ints.toArray(ImmutableList.of(1, 2, 3));
            then(array).isInstanceOf(int[].class).containsExactly(1, 2, 3);
        }

        // 将 Collection<Long> 转为 long[]
        {
            var array = Longs.toArray(ImmutableList.of(1, 2, 3));
            then(array).isInstanceOf(long[].class).containsExactly(1, 2, 3);
        }

        // 将 Collection<Float> 转为 float[]
        {
            var array = Floats.toArray(ImmutableList.of(0.1, 0.2, 0.3));
            then(array).isInstanceOf(float[].class).containsExactly(0.1f, 0.2f, 0.3f);
        }

        // 将 Collection<Double> 转为 double[]
        {
            var array = Doubles.toArray(ImmutableList.of(0.1, 0.2, 0.3));
            then(array).isInstanceOf(double[].class).containsExactly(0.1, 0.2, 0.3);
        }

        // 将 Collection<Char> 转为 char[]
        {
            var array = Chars.toArray(ImmutableList.of('A', 'B', 'C'));
            then(array).isInstanceOf(char[].class).containsExactly('A', 'B', 'C');
        }

        // 将 Collection<Boolean> 转为 boolean[]
        {
            var array = Booleans.toArray(ImmutableList.of(true, false, true));
            then(array).isInstanceOf(boolean[].class).containsExactly(true, false, true);
        }
    }

    /**
     * 测试无符号类型
     *
     * <p>
     * Guava 拓展了 Java 的整数类型, 增加了包括 {@link UnsignedInteger} 以及 {@link UnsignedLong} 无符号整数类型以及
     * {@link UnsignedBytes}, {@link UnsignedInts} 以及 {@link UnsignedLongs} 工具类
     * </p>
     *
     * <p>
     * 在计算机系统中, 整数符号是通过二进制最高位来表示的, 最高位为 {@code 1} 表示负数; 最高位为 {@code 0} 表示正数, 剩余的位数表示数值
     * </p>
     *
     * <p>
     * 计算机在内存中使用补码 (反码 + 1) 的形式保存"负数", 以达到通过加法运算器求减法的目标. 例如: {@code byte 2} 的二进制为
     * {@code 00000010b}, 则 {@code byte -2} 的值为其补码, 即 {@code 11111101b + 1b = 11111110b}
     * </p>
     *
     * <p>
     * 所谓"无符号", 即不将最高位作为符号位, 将所有的二进制都作为数值, 此时 {@code byte -2} 的值 {@code 11111110b} 表示整数
     * {@code 254}
     * </p>
     *
     * <p>
     * 所以无论是有符号整数或是无符号整数, 其二进制表示都是一致的, 例如 {@code byte -1} 转为的无符号为 {@code byte 255}, 二进制都是
     * {@code 11111111b}, 对无符号整数的"加", "减", "乘", 都可以转为对应的无符号整数进行操作. 但涉及"除", "比较", "指数", "对数"
     * 等运算, 相同二进制位的无符号和有符号整数, 计算结果不同
     * </p>
     *
     * <p>
     * 在 JDK 9 之前, Java 并不直接支持无符号类型, 如果要对无符号类型进行运算, 需要自行通过位运算符进行, 例如要将一个 {@code int}
     * 值 {@code n} 转为无符号类型, 则需要进行 {@code n & 0x00000000FFFFFFFFL} 这类运算将符号位设置为 {@code 0} 并保留有效数字位不变,
     * 其结果是一个 {@code long} 类型
     * </p>
     *
     * <p>
     * JDK 9 之后, 增加了类似 {@link Integer#toUnsignedLong(int)}, {@link Integer#toUnsignedString(int)} 以及
     * {@link Integer#compareUnsigned(int, int)} 方法, 以支持有限的无符号整数操作
     * </p>
     *
     * <p>
     * Guava 提供了更为完备的无符号整数相关方法, 但由于 Java 的简单类型并不支持无符号整数, 所以无符号整数仍是基于有符号数进程存储的
     * (基于相同的二进制值), 只是在运算和转换时表现为无符号数
     * </p>
     */
    @Test
    void unsigned_shouldUseUnsignedPrimitive() {
        // 测试无符号字节的最大值
        {
            var max = UnsignedBytes.MAX_VALUE;
            then(UnsignedBytes.toInt(max)).isEqualTo(0xFF);
        }

        // 测试无符号整数的最大值
        {
            var max = UnsignedInteger.MAX_VALUE;
            then(max.longValue()).isEqualTo(0xFFFFFFFFL);
        }

        // 测试无符号长整数的最大值
        {
            var max = UnsignedLong.MAX_VALUE;
            then(max.bigIntegerValue()).isEqualTo(new BigInteger("FFFFFFFFFFFFFFFF", 16));
        }

        // 对无符号整数进行比较
        {
            var b1 = (byte) 0xF1; // -14
            var b2 = (byte) 0x0F; // 15

            then(b1 < b2).isTrue();

            // 对应的有符号和无符号数比较结果不同
            then(UnsignedBytes.compare(b1, b2)).isGreaterThan(0);
            then(Byte.compareUnsigned(b1, b2)).isGreaterThan(0);
        }

        // 对无符号整数进行运算
        {
            var i1 = -2; // 0xFFFFFFFE, 11111111111111111111111111111110b
            var i2 = -1; // 0xFFFFFFFF, 11111111111111111111111111111111b

            var u1 = UnsignedInteger.fromIntBits(i1);
            then(u1.longValue()).isEqualTo(Integer.toUnsignedLong(i1)).isEqualTo(i1 & 0x00000000FFFFFFFFL);

            var u2 = UnsignedInteger.fromIntBits(i2);
            then(u2.longValue()).isEqualTo(i2 & 0x00000000FFFFFFFFL);

            // 加法运算, 对应的无符号和有符号数运算结果相同
            // -2 + -1 = 0xFFFFFFFF + 0xFFFFFFFE
            // = 11111111111111111111111111111110b + 11111111111111111111111111111111b
            // = 0xFFFFFFFD = 11111111111111111111111111111101b = -3
            then(u1.plus(u2).intValue()).isEqualTo(-3);

            // 减法运算, 对应的无符号和有符号数运算结果相同
            // -2 - -1 = 0xFFFFFFFF - 0xFFFFFFFE
            // = 11111111111111111111111111111110b - 11111111111111111111111111111111b
            // = 0xFFFFFFFE = 11111111111111111111111111111111b = -1
            then(u1.minus(u2).intValue()).isEqualTo(-1);

            // 乘法运算, 对应的无符号和有符号数运算结果相同
            // -2 * -1 = 0xFFFFFFFF * 0xFFFFFFFE
            // = 11111111111111111111111111111110b * 11111111111111111111111111111111b
            // = 10b = 2
            then(u1.times(u2).intValue()).isEqualTo(2);

            // 除法运算中, 对应的无符号和有符号数运算结果不同
            then(u1.dividedBy(u2).intValue()).isEqualTo(0);
        }
    }
}
