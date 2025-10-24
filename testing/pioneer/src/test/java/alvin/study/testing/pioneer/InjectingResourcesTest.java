package alvin.study.testing.pioneer;

import static org.assertj.core.api.BDDAssertions.then;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junitpioneer.jupiter.resource.Dir;
import org.junitpioneer.jupiter.resource.New;
import org.junitpioneer.jupiter.resource.Shared;
import org.junitpioneer.jupiter.resource.TemporaryDirectory;

import lombok.SneakyThrows;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * 演示注入临时目录资源
 */
@TestMethodOrder(OrderAnnotation.class)
class InjectingResourcesTest {
    /**
     * 注入一个新的临时目录资源
     *
     * <p>
     * {@code value} 属性指定用于创建临时目录的类; {@code arguments}
     * 属性用于指定创建临时路径的名称前缀
     * </p>
     *
     * <p>
     * 注入的临时路径会创建到 {@code "java.io.tmpdir"} 系统属性指定的路径中,
     * 即系统默认的临时路径根路径 (在 Linux 系统中, 这个路径为 {@code /tmp})
     * </p>
     *
     * <p>
     * 通过 {@link New @New} 注解创建的临时路径在当前测试方法结束后删除,
     * 且不同测试方法的 {@link New @New} 注解创建的测试路径不相同
     * </p>
     *
     * @param tmpDir 创建的临时目录对象
     */
    @Test
    void new_shouldCreateNewTempDirResource(
            @New(value = TemporaryDirectory.class, arguments = "new-dir-prefix") Path tmpDir) {
        // 确认临时路径已被创建
        then(tmpDir).exists();

        // 获取系统临时路径根路径
        var rootTmpDir = Paths.get(System.getProperty("java.io.tmpdir"));

        // relativize 方法用于获取 tmpDir 路径基于 rootTmpDir 路径的相对路径
        // 例如 tmpDir="/tmp/new-dir-prefix-xxx", rootTmpDir="/tmp",
        // 则结果为 "new-dir-prefix-xxx"
        then(rootTmpDir.relativize(tmpDir).toString()).startsWith("new-dir-prefix");
    }

    /**
     * 注入一个新的临时目录资源
     *
     * <p>
     * {@link Dir @Dir} 注解相当于 {@code @New(TemporaryDirectory.class)}
     * 注解的快捷方法
     * </p>
     */
    @Test
    void dir_shouldCreateNewTempDirResource(@Dir Path tmpDir) {
        // 确认临时路径已被创建
        then(tmpDir).exists();

        // 获取系统临时路径根路径
        var rootTmpDir = Paths.get(System.getProperty("java.io.tmpdir"));

        // 确认产生的临时目录在 rootTmpDir 路径下
        then(tmpDir).startsWith(rootTmpDir);
    }

    /**
     * 注入一个共享临时目录资源
     *
     * <p>
     * {@code factory} 属性指定用于创建临时目录的类; {@code name}
     * 属性用于指定临时路径的名称
     * </p>
     *
     * <p>
     * 注入的临时路径会创建到 {@code java.io.tmpdir} 系统属性指定的路径中,
     * 即系统默认的临时路径根路径 (在 Linux 系统中, 这个路径为 {@code /tmp})
     * </p>
     *
     * <p>
     * 通过 {@link Shared @Shared} 注解创建的临时路径会在当前测试中共享,
     * 所以其它测试方法也可以用同样的方式注入同一个临时目录, 在所有测试结束后,
     * 该临时路径被删除
     * </p>
     *
     * <p>
     * 当前测试方法创建的临时路径会在
     * {@link #new_shouldCreateSharedTempDirResourceRead(Path)}
     * 方法中使用
     * </p>
     *
     * @param tmpDir 创建的共享临时目录对象
     */
    @Test
    @SneakyThrows
    @Order(1)
    void new_shouldCreateSharedTempDirResourceWrite(
            @Shared(factory = TemporaryDirectory.class, name = "shared-tmp-dir") Path tmpDir) {
        // 确认临时路径已被创建
        then(Files.exists(tmpDir)).isTrue();

        // 在临时路径中创建一个文件
        var file = Files.createFile(Paths.get(tmpDir.toString(), "test.txt"));
        then(file).exists();

        // 在文件中写入内容
        try (var out = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            out.write("Hello World".getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * 注入一个共享临时目录资源
     *
     * <p>
     * {@code factory} 属性指定用于创建临时目录的类; {@code name}
     * 属性用于指定临时路径的名称
     * </p>
     *
     * <p>
     * 注入的临时路径会创建到 {@code java.io.tmpdir} 系统属性指定的路径中,
     * 即系统默认的临时路径根路径 (在 Linux 系统中, 这个路径为 {@code /tmp})
     * </p>
     *
     * <p>
     * 通过 {@link Shared @Shared} 注解创建的临时路径会在当前测试中共享,
     * 所以其它测试方法也可以用同样的方式注入同一个临时目录, 在所有测试结束后,
     * 该临时路径被删除
     * </p>
     *
     * <p>
     * {@link Shared @Shared} 注解的 {@code scope} 属性可以指定共享资源的范围,
     * 默认情况下, 共享资源在当前测试类中共享, 即当前测试类结束后, 共享资源被删除.
     * 如果设置为 {@link Shared.Scope#GLOBAL}, 则该共享资源在整个测试中共享,
     * 即所有测试完毕后共享资源才会被
     * 删除
     * </p>
     *
     * <p>
     * 当前测试方法创建的临时路径会在
     * {@link #new_shouldCreateSharedTempDirResourceWrite(Path)}
     * 方法中使用
     * </p>
     *
     * @param tmpDir 创建的共享临时目录对象
     */
    @Test
    @SneakyThrows
    @Order(2)
    void new_shouldCreateSharedTempDirResourceRead(
            @Shared(factory = TemporaryDirectory.class, name = "shared-tmp-dir") Path tmpDir) {
        // 确认临时路径已被创建
        then(Files.exists(tmpDir)).isTrue();

        var filePath = Paths.get(tmpDir.toString(), "test.txt");
        then(filePath).exists();

        try (var in = Files.newInputStream(filePath, StandardOpenOption.READ)) {
            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            then(content).isEqualTo("Hello World");
        }
    }
}
