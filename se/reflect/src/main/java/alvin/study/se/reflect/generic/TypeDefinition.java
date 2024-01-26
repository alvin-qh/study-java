package alvin.study.se.reflect.generic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 用于获取泛型参数类型的工具类
 *
 * <p>
 * 由于 Java 本身的类型擦除行为, 导致在运行时, 所有的泛型类型都会退化成 {@code Object} 类型, 丢失了泛型类型定义,
 * 本工具类用于获取泛型参数 {@code <T>} 的类型定义
 * </p>
 *
 * <p>
 * 通过如下方式可以进行泛型类型获取:
 *
 * <pre>
 * var def = new TypeDefinition<List<String>>() { }
 * var type = def.getGenericArgumentRawType();
 *
 * var obj = (Object) new ArrayList<String>();
 * var list = type.cast(obj);
 * </pre>
 * </p>
 */
public abstract class TypeDefinition<T> implements ParameterizedType {
    /**
     * 当前对象的泛型参数类型, 及 {@code <T>} 参数的类型
     */
    private final ParameterizedType genericArgumentType;

    /**
     * 构造器, 获取当前对象的泛型参数类型
     */
    protected TypeDefinition() {
        // 获取当前对象类型的泛型超类 (即当前类) 的类型
        var parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();

        // 获取当前类的泛型参数 (即 <T> 参数) 的类型
        // 由于当前类型只有一个泛型参数, 所以获取第一个泛型参数类型即可
        genericArgumentType = (ParameterizedType) parameterizedType.getActualTypeArguments()[0];
    }

    /**
     * 获取泛型参数类型的类型名称
     *
     * @return 泛型参数类型的类型名称
     */
    @Override
    public String getTypeName() { return genericArgumentType.getTypeName(); }

    /**
     * 获取泛型参数类型的所有者类型
     *
     * <p>
     * 所谓的所有者类型, 即如果当前泛型类型是一个类型的内部类 (Inner Class), 例如 {@link java.util.Map.Entry
     * Map.Entry} 类型是 {@link java.util.Map Map} 类型的内部类, 所以前者的 {@code ownerType} 为后者
     * </p>
     *
     * @return 当前泛型类型的所有类型
     */
    @Override
    public Type getOwnerType() { return genericArgumentType.getOwnerType(); }

    /**
     * 获取泛型参数类型的 {@link Class} 对象
     *
     * @return 泛型参数类型的 {@link Class} 对象
     */
    @Override
    public Class<T> getRawType() { return (Class<T>) genericArgumentType.getRawType(); }

    /**
     * 获取泛型参数类型的泛型参数
     *
     * <p>
     * 如果当前泛型参数 ({@code <T>}) 仍是一个带有泛型参数的类型 (例如 {@code T = List<String>}),
     * 则该方法返回泛型参数列表
     * </p>
     *
     * @return 泛型参数类型的泛型参数
     */
    @Override
    public Type[] getActualTypeArguments() { return genericArgumentType.getActualTypeArguments(); }
}
