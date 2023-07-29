package alvin.study.guava.reflect;

import com.google.common.reflect.ClassPath;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 查找指定的类对象
 *
 * <p>
 * 根据指定的匹配规则, 返回符合条件的 {@link Class} 类型对象
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassFinder {
    /**
     * 根据指定的的"包"名称, 返回该包下面所有的 {@link Class} 对象
     *
     * @param packageName 包名称
     * @return 包下面所有的 {@link Class} 对象
     */
    public static Set<Class<?>> inPackage(String packageName) throws IOException {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses()
                .stream()
                .filter(ci -> ci.getPackageName().startsWith(packageName))
                .map(ClassPath.ClassInfo::load)
                .collect(Collectors.toSet());
    }
}
