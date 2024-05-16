package alvin.study.testing.junit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试注入一个临时路径
 *
 * <p>
 * 在测试时, 如果需要测试文件的读取和写入, 需要创建文件所在的路径, 但固定的路径意味着多线程测试以及测试后清理的问题.
 * {@link TempDir @TempDir} 注解可以创建一个临时目录, 并在测试结束后自动删除
 * </p>
 *
 * <p>
 * 临时目录会创建在 {@code "java.io.tmpdir"} 系统属性指定的临时根目录下, 通过 {@link System#getProperty(String)} 方法可以获取这个
 * 根路径
 * </p>
 */
class TempDirTest {
    /**
     * 注入创建的临时目录
     */
    @TempDir
    private Path tmpDir;

    /**
     * 测试 {@link #tmpDir} 字段为成功创建的临时目录
     */
    @Test
    void tempDir_shouldATempDirCreated() {
        // 确认临时目录存在
        then(tmpDir).exists();

        // 确认创建的临时目录为 "java.io.tmpdir" 系统属性定义目录的子目录
        var rootTmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
        then(tmpDir).startsWith(rootTmpDir);
    }
}
