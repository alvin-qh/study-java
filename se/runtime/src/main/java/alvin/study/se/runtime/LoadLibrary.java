package alvin.study.se.runtime;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 从资源中释放动态库并进行加载
 */
public final class LoadLibrary {
    private LoadLibrary() {
    }

    /**
     * 从资源中释放动态库, 并加载
     *
     * <p>
     * JDK 提供了两个方法来读取 C++ 动态库文件, 其中:
     * <ul>
     * <li>
     * {@link Runtime#loadLibrary(String)} 方法, 以动态库名称为参数, 加载该动态, 要求该动态库文件必须存在于
     * Classpath 中
     * </li>
     * <li>
     * {@link Runtime#load(String)} 方法, 以动态库全路径文件名为参数, 加载该动态, 该动态库可以在任何位置
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * {@link System#load(String)} 方法和 {@link System#loadLibrary(String)}
     * 方法和上述两个方法一致
     * </p>
     *
     * @param resource 资源名称
     */
    public static void load(String resource) throws IOException {
        // 获取临时路径下的动态库文件
        var dst = new File(findTempDir(), resource);
        try {
            // 如果文件不存在, 则从资源中进行释放
            if (!dst.exists()) {
                // 将资源文件复制到临时路径中
                try (var os = new FileOutputStream(dst, false)) {
                    // 动态库资源存储在 resources/native 下
                    try (var is = LoadLibrary.class.getResourceAsStream("/native/" + resource)) {
                        if (is == null) {
                            throw new NullPointerException("is");
                        }
                        ByteStreams.copy(is, os);
                    }
                }
            }
            // 加载动态库
            System.load(dst.getAbsolutePath());
        } finally {
            dst.deleteOnExit();
        }
    }

    /**
     * 获取当前系统的临时文件夹路径
     *
     * @return 表示临时文件夹路径的 {@link File} 对象
     */
    private static File findTempDir() throws IOException {
        // 获取临时路径字符串
        var tempDirName = System.getProperty("java.io.tmpdir");
        if (Strings.isNullOrEmpty(tempDirName)) {
            throw new FileNotFoundException("Cannot find temp dir");
        }

        // 将临时路径字符串转为 File 对象
        return new File(tempDirName);
    }
}
