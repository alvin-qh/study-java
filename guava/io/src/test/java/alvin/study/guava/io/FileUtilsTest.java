package alvin.study.guava.io;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

/**
 * 测试文件工具类
 *
 * <p>
 * Guava 提供了 {@link Files} 类, 该类封装了针对于文件对象 ({@link File} 类型对象)
 * 操作的一系列方法
 * </p>
 *
 * <p>
 * Guava 提供了 {@link MoreFiles} 类, 该类封装了针对于路径对象
 * ({@link java.nio.file.Path Path} 类型对象) 操作的一系列方法
 * </p>
 *
 * <p>
 * 另外, {@link Files} 类以及 {@link MoreFiles} 类还可以和
 * {@link com.google.common.io.ByteSource ByteSource},
 * {@link com.google.common.io.CharSource CharSource},
 * {@link com.google.common.io.ByteSink ByteSink} 以及
 * {@link com.google.common.io.CharSink CharSink} 类配合使用, 参考
 * {@link ByteSourceTest}, {@link CharSourceTest},
 * {@link ByteSinkTest} 以及 {@link CharSinkTest} 测试类
 * </p>
 */
class FileUtilsTest {
    /**
     * 删除指定目录, 包括目录下面包含的文件和子目录
     *
     * <p>
     * 通过
     * {@link MoreFiles#deleteRecursively(java.nio.file.Path,
     * com.google.common.io.RecursiveDeleteOption...)
     * MoreFiles.deleteRecursively(Path, RecursiveDeleteOption...)}
     * 方法可以"递归地"删除指定目录下的所有文件和子目录
     * </p>
     *
     * @param dir 表示要删除的目录的 {@link Path} 对象
     * @throws IOException
     */
    static void deleteDirs(Path dir) throws IOException {
        MoreFiles.deleteRecursively(dir, RecursiveDeleteOption.ALLOW_INSECURE);
        then(dir).doesNotExist();
    }

    /**
     * {@link #deleteDirs(Path)} 的重载方法, 以 {@link File} 对象为参数
     *
     * @param dir 表示要删除的目录的 {@link File} 对象
     * @throws IOException
     */
    static void deleteDirs(File dir) throws IOException {
        deleteDirs(dir.toPath());
    }

    /**
     * 测试创建一个文件所在的路径
     *
     * <p>
     * 通过 {@link Files#createParentDirs(File)} 方法用于创建一个文件所在的路径
     * </p>
     *
     * <p>
     * 例如一个文件 {@code /aa/bb/cc/d.txt}, 则文件所在的目录为 {@code /aa/bb/cc},
     * 如果该目录不存在, 则 {@code createParentDirs} 方法可以创建该目录 (包括其上级目录)
     * </p>
     */
    @Test
    @SneakyThrows
    void createParentDirs_shouldCreateParentDirsOfFile() {
        // 包含文件的目录
        var parentPath = new File("test/aa/bb/cc");

        // 完整路径, 目录 + 文件
        var newFile = new File(parentPath, "test-01.txt");
        newFile.deleteOnExit();

        // 此时目录并不存在
        then(parentPath).doesNotExist();

        try {
            // 创建文件所在的目录
            Files.createParentDirs(newFile);

            // 确认此时文件已经存在
            then(parentPath).exists();
        } finally {
            deleteDirs(new File("test"));
        }
    }

    /**
     * 测试创建一个文件所在的路径
     *
     * <p>
     * 通过
     * {@link MoreFiles#createParentDirectories(Path,
     * java.nio.file.attribute.FileAttribute...)
     * MoreFiles.createParentDirectories(Path, FileAttribute...)}
     * 方法用于创建一个文件所在的路径
     * </p>
     *
     * <p>
     * 例如一个文件 {@code /aa/bb/cc/d.txt}, 则文件所在的目录为 {@code /aa/bb/cc},
     * 如果该目录不存在, 则 {@code createParentDirectories} 方法可以创建该目录
     * (包括其上级目录)
     * </p>
     */
    @Test
    @SneakyThrows
    void createParentDirectories_shouldCreateParentDirsOfPath() {
        // 包含文件的目录
        var parentPath = Path.of("test/aa/bb/cc");

        // 完整路径, 目录 + 文件
        var newFile = parentPath.resolve("test-01.txt");

        // 此时目录并不存在
        then(parentPath).doesNotExist();

        // 路径应该具备 rwx 三个权限属性
        var attrs = PosixFilePermissions.asFileAttribute(
            PosixFilePermissions.fromString("rwx------"));
        try {
            // 创建文件所在的目录
            MoreFiles.createParentDirectories(newFile, attrs);

            // 确认此时文件已经存在
            then(parentPath).exists();
        } finally {
            deleteDirs(new File("test"));
        }
    }

    /**
     * 获取指定文件的文件名和扩展名
     *
     * <p>
     * 通过 {@link Files#getFileExtension(String)} 方法用于获取一个文件名的扩展名
     * </p>
     *
     * <p>
     * 通过 {@link Files#getNameWithoutExtension(String)}
     * 方法用于获取一个文件名的主文件名, 即文件名中去掉了路径和扩展名部分
     * </p>
     */
    @Test
    void extension_shouldGetNameExtensionOrWithoutExtensionOfFile() {
        var filename = "/aa/bb/cc/d.dat";

        // 确认获取文件扩展名
        var extension = Files.getFileExtension(filename);
        then(extension).isEqualTo("dat");

        // 确认获取主文件名
        var domain = Files.getNameWithoutExtension(filename);
        then(domain).isEqualTo("d");
    }

    /**
     * 获取指定文件的文件名和扩展名
     *
     * <p>
     * 通过 {@link MoreFiles#getFileExtension(Path)} 方法用于获取一个 {@link Path}
     * 对象所表示文件的扩展名
     * </p>
     *
     * <p>
     * 通过 {@link MoreFiles#getNameWithoutExtension(Path)} 方法用于获取一个
     * {@link Path} 对象表示文件的主文件名, 即文件名中去掉了路径和扩展名部分
     * </p>
     */
    @Test
    void extension_shouldGetNameExtensionOrWithoutExtensionOfPath() {
        var path = Path.of("/aa/bb/cc/d.dat");

        // 确认获取文件扩展名
        var extension = MoreFiles.getFileExtension(path);
        then(extension).isEqualTo("dat");

        // 确认获取主文件名
        var domain = MoreFiles.getNameWithoutExtension(path);
        then(domain).isEqualTo("d");
    }

    /**
     * 从 {@link File} 对象创建 {@link java.io.BufferedWriter BufferedWriter}
     * 以及 {@link java.io.BufferedReader BufferedReader} 对象
     *
     * <p>
     * 通过 {@link Files#newWriter(File, java.nio.charset.Charset)
     * Files.newWriter(File, Charset)} 方法用于通过一个
     * {@link File} 对象, 创建一个 {@link java.io.BufferedWriter BufferedWriter}
     * 对象, 用于对文件进行写入
     * </p>
     *
     * <p>
     * 通过 {@link Files#newReader(File, java.nio.charset.Charset)
     * Files.newReader(File, Charset)} 方法用于通过一个
     * {@link File} 对象, 创建一个 {@link java.io.BufferedReader BufferedReader}
     * 对象, 用于对文件进行读取
     * </p>
     */
    @Test
    @SneakyThrows
    void readWriter_shouldGetFileWithOrWithoutExtension() {
        // 创建临时文件
        var file = File.createTempFile("guava-test", ".bin");
        file.deleteOnExit();

        try {
            // 通过文件对象创建 Writer 对象, 写入内容
            try (var writer = Files.newWriter(file, StandardCharsets.UTF_8)) {
                writer.write("Hello Guava");
            }

            // 通过文件对象创建 Reader 对象, 读取内容, 确认读取内容和写入内容一致
            try (var reader = Files.newReader(file, StandardCharsets.UTF_8)) {
                var s = reader.readLine();
                then(s).isEqualTo("Hello Guava");
            }
        } finally {
            file.delete();
        }
    }

    /**
     * 从文件中读取多行文本
     *
     * <p>
     * 通过 {@link Files#readLines(File, java.nio.charset.Charset)
     * Files.readLines(File, Charset)}
     * 方法可以从文件中读取多行文本 (行之间通过 {@code \n}) 分隔
     * </p>
     */
    @Test
    @SneakyThrows
    void readLines_shouldReadMultiLinesFromFile() {
        var file = File.createTempFile("guava-test", ".txt");
        var sink = Files.asCharSink(file, StandardCharsets.UTF_8);

        // 写入多行文本
        sink.writeLines(ImmutableList.of("Line1", "Line2", "Line3"));

        // 读取多行文本, 确认读取内容和写入内容一致
        var lines = Files.readLines(file, StandardCharsets.UTF_8);
        then(lines).containsExactly("Line1", "Line2", "Line3");
    }

    /**
     * 创建内存映射文件
     *
     * <p>
     * 通过 {@link Files#map(File, MapMode, long)} 方法可以用于创建内存映射文件,
     * 返回一个 {@link java.nio.MappedByteBuffer MappedByteBuffer} 对象,
     * 表示一个和文件进行映射的缓冲区对象
     * </p>
     *
     * <p>
     * 所谓内存映射文件, 即将文件和内存地址进行对应, 通过底层 IO,
     * 即可以通过内存读取方式读取文件内容
     * </p>
     */
    @Test
    @SneakyThrows
    void map_shouldCreateMemoryMappingFile() {
        // 创建临时文件
        var file = File.createTempFile("guava-test", ".mm");
        file.deleteOnExit();

        // 用于读写的数据
        var data = "Hello Guava".getBytes(StandardCharsets.UTF_8);

        try {
            // 创建内存映射文件, 得到 MappedByteBuffer 对象
            var buf = Files.map(file, MapMode.READ_WRITE, 1024);
            // 向缓冲区写入数据
            buf.put("Hello Guava".getBytes(StandardCharsets.UTF_8));

            // 反转缓冲区
            buf.flip();

            var bs = new byte[buf.limit()];
            // 从缓冲区读取数据
            buf.get(bs);
            then(bs).isEqualTo(data);

            // 从文件中读取内容，确认和写入映射缓冲区的内容一致
            try (var is = new FileInputStream(file)) {
                bs = is.readNBytes(11);
            }
            then(bs).isEqualTo(data);
        } finally {
            file.delete();
        }
    }

    /**
     * 通过 {@link File} 对象创建空文件
     *
     * <p>
     * 通过 {@link Files#touch(File)} 方法用于新建一个空文件 (当文件不存在时),
     * 类似于 linux 的 {@code touch} 命令
     * </p>
     */
    @Test
    @SneakyThrows
    void touch_shouldCreateNewEmptyFilesAsFileObject() {
        var file = File.createTempFile("guava-test", ".dat");

        try {
            file.delete();
            then(file).doesNotExist();

            // 创建空白新文件, 确认文件已被创建
            Files.touch(file);
            then(file).exists();
        } finally {
            file.delete();
        }
    }

    /**
     * 通过 {@link Path} 对象创建空文件
     *
     * <p>
     * 通过 {@link MoreFiles#touch(Path)} 方法用于新建一个空文件 (当文件不存在时),
     * 类似于 linux 的 {@code touch} 命令
     * </p>
     */
    @Test
    @SneakyThrows
    void touch_shouldCreateNewEmptyFilesAsPathObject() {
        var file = java.nio.file.Files.createTempFile("guava-test", ".dat");

        try {
            java.nio.file.Files.delete(file);
            then(file).doesNotExist();

            // 创建空白新文件, 确认文件已被创建
            MoreFiles.touch(file);
            then(file).exists();
        } finally {
            java.nio.file.Files.delete(file);
        }
    }

    /**
     * 将所给的路径字符串进行简化处理
     *
     * <p>
     * 通过 {@link Files#simplifyPath(String)} 方法可以对一个表示路径的字符串进行简化
     * </p>
     *
     * <p>
     * 所谓的字符串简化, 包括:
     * <ul>
     * <li>
     * 对于空字符串 {@code ""}, 简化结果为 {@code "."}, 表示当前路径
     * </li>
     * <li>
     * 对于当前路径 {@code "."}, 简化结果仍为 {@code "."}
     * </li>
     * <li>
     * 对于路径前包含 {@code "./"} 部分的, 简化后会删除这部分, 例如
     * {@code "./t.txt"} => {@code "t.txt"}
     * </li>
     * <li>
     * 对于路径中包括 {@code "../"} 部分的, 简化后会进行展开, 例如
     * {@code "/aa/bb/../t.txt"} =>
     * {@code "/aa/t.txt"}
     * </li>
     * <li>
     * 对于路径中包括多余 {@code "/"} 部分的, 简化后会被删除 (除了最开头的
     * {@code "/"} 字符), 例如 {@code "//aa//bb/"} => {@code "/aa/bb"}
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void simplifyPath_shouldSimplifyFilePathname() {
        then(Files.simplifyPath("")).isEqualTo(".");
        then(Files.simplifyPath(".")).isEqualTo(".");
        then(Files.simplifyPath("./t.txt")).isEqualTo("t.txt");
        then(Files.simplifyPath("/aa/bb/../t.txt")).isEqualTo("/aa/t.txt");
        then(Files.simplifyPath("//aa//bb/")).isEqualTo("/aa/bb");
    }

    /**
     * 移动文件
     *
     * <p>
     * 通过 {@link Files#move(File, File)} 方法可以将文件进行移动
     * </p>
     *
     * <p>
     * 要移动的目标文件所在的路径必须存在, 否则移动会失败
     * </p>
     */
    @Test
    @SneakyThrows
    void move_shouldMoveFile() {
        var srcFile = new File("test/aa/a.dat");
        var dstFile = new File("test/bb/b.dat");

        try {
            // 创建存放文件的目录
            Files.createParentDirs(srcFile);
            Files.createParentDirs(dstFile);

            // 创建原文件, 确认此时原文件存在, 目标文件不存在
            Files.touch(srcFile);
            then(srcFile).exists();
            then(dstFile).doesNotExist();

            // 移动文件, 确认此时原文件不存在, 目标文件存在
            Files.move(srcFile, dstFile);
            then(srcFile).doesNotExist();
            then(dstFile).exists();
        } finally {
            deleteDirs(new File("test"));
        }
    }

    /**
     * 写入和读取文件
     *
     * <p>
     * 通过 {@link Files#write(byte[], File)} 方法可以将数据全部写入文件
     * </p>
     *
     * <p>
     * 通过 {@link Files#toByteArray(File)} 方法可以文件内容全部读取,
     * 返回包含文件内容的字节数组
     * </p>
     */
    @Test
    @SneakyThrows
    void move_shouldWriteAndReadFile() {
        // 用于读写的数据
        var data = "Hello Guava".getBytes(StandardCharsets.UTF_8);

        // 创建用于读写的文件
        var file = File.createTempFile("guava-test", ".dat");

        // 将数据写入文件
        Files.write(data, file);

        // 确认从文件中读取的内容和写入的一致
        then(Files.toByteArray(file)).isEqualTo(data);
    }

    /**
     * 列举指定目录中的所有文件和子目录, 指定目录由 {@link Path} 类型对象表示
     *
     * <p>
     * 通过 {@link MoreFiles#listFiles(Path)} 方法可以列举指定目录中的内容,
     * 包括文件和子目录, 列举的结果为一个 {@link Path} 类型对象集合
     * </p>
     *
     * <p>
     * 列举的内容中只包含指定目录中的内容, 不包含其子目录中的内容
     * </p>
     */
    @Test
    @SneakyThrows
    void listFiles_shouldListFilesAndSubdirectoriesByGivenPath() {
        // 定义父一级目录
        var parent = Path.of("test");

        // 定义要在父目录中创建的子目录和文件
        var paths = new Path[] {
            parent.resolve("1.txt"),
            parent.resolve("2.txt"),
            parent.resolve("3.txt"),
            parent.resolve("a"),
            parent.resolve("b")
        };

        try {
            // 创建父目录
            java.nio.file.Files.createDirectories(parent);

            // 创建测试用的文件和子目录
            for (var path : paths) {
                // 判断有无扩展名, 本例中是否为路径或文件是通过是否具备扩展名来判断
                if (Strings.isNullOrEmpty(MoreFiles.getFileExtension(path))) {
                    java.nio.file.Files.createDirectories(path);
                } else {
                    MoreFiles.touch(path);
                }
            }

            // 列举目录中的所有文件和子目录
            var files = MoreFiles.listFiles(parent);
            // 确认结果符合预期
            then(files).hasSize(paths.length).contains(paths);
        } finally {
            deleteDirs(parent);
        }
    }
}
