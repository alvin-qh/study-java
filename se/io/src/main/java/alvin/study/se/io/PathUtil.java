package alvin.study.se.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

/**
 * 路径工具类
 *
 * <p>
 * Java 中主要通过 {@link Paths} 类型对路径进行操作, 本例中对该类型进行包装
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PathUtil {
    /**
     * 组合多个路径为一个完整路径
     *
     * @param first 第一个路径字符串
     * @param more  后续的路径部分
     * @return 组合后的路径字符串
     */
    public static @NotNull String combine(String first, String... more) {
        return Paths.get(first, more).toString();
    }
}
