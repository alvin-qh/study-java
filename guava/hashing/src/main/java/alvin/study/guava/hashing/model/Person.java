package alvin.study.guava.hashing.model;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import com.google.common.hash.Funnel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 定义一个类型用于计算对象的散列值
 */
@Getter
@RequiredArgsConstructor
public class Person {
    private final long id;
    private final String name;
    private final LocalDate birthday;
    private final String location;

    /**
     * 将对象的所需字段写入散列对象中
     *
     * <p>
     * {@link Funnel} 接口通过其 {@link Funnel#funnel(Object, com.google.common.hash.PrimitiveSink)
     * Funnel.funnel(Object, PrimitiveSink)} 方法, 将 {@code 参数 1} 写入 {@code 参数 2} 对象中
     * </p>
     *
     * <p>
     * {@link com.google.common.hash.PrimitiveSink PrimitiveSink} 接口即一个散列函数对象 (参考其子接口
     * {@link com.google.common.hash.Hasher Hasher} 类型), 其一系列的 {@code putXXX} 方法 (例如:
     * {@link com.google.common.hash.PrimitiveSink#putInt(int) PrimitiveSink.putInt(int)} 方法)
     * 用于将各种类型值写入散列函数对象中, 作为散列计算的一部分数据
     * </p>
     *
     * @return {@link Funnel} 接口对象
     */
    public static Funnel<Person> makeFunnel() {
        return (person, into) -> into
                .putLong(person.getId())
                .putString(person.getName(), StandardCharsets.UTF_8)
                .putString(person.getBirthday().toString(), StandardCharsets.UTF_8)
                .putString(person.getLocation(), StandardCharsets.UTF_8);
    }
}
