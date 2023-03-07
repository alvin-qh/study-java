package alvin.study.io;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.io.Files;

/**
 * 演示各类文件遍历操作
 *
 * <p>
 * Guava 库的 {@link Files#fileTraverser()} 方法返回一个文件遍历器 {@link com.google.common.graph.Traverser Traverser} 对象,
 * 该对象通过如下方法对文件进行遍历:
 * <ul>
 * <li>
 * {@link com.google.common.graph.Traverser#breadthFirst(Object) Traverser.breadthFirst(File)}
 * 方法用于对一个目录及其子目录进行"广度优先"遍历, 即先对某一级目录的所有文件进行遍历, 之后在进入下一级目录进行遍历
 * </li>
 * <li>
 * {@link com.google.common.graph.Traverser#depthFirstPreOrder(Object) Traverser.depthFirstPreOrder(File)}
 * 方法用于对一个目录及其子目录进行正序"深度优先"遍历, 即先从顶层目录依次向下层目录访问, 到达最底层目录后, 依次向上访问每一级目录中的文件
 * </li>
 * <li>
 * {@link com.google.common.graph.Traverser#depthFirstPostOrder(Object) Traverser.depthFirstPostOrder(File)}
 * 方法用于对一个目录及其子目录进行逆序"深度优先"遍历, 即先到达最底层的目录, 然后依次向上访问每一级目录以及其中的文件
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 类似 Guava 库, 通过 JDK 自带的库也可以实现类似的结果, 包括使用 JDK6 的 IO 库, JDK 7 的 NIO 库以及 JDK 8 的扩展方法
 * </p>
 */
class FileTraverserTest {
    // 定义测试目录, 每个的测试相关路径和文件都存入该测试目录
    private final File parent;

    /**
     * 构造器, 创建测试目录
     */
    public FileTraverserTest() throws IOException {
        parent = java.nio.file.Files.createTempDirectory("test-guava").toFile();
    }

    /**
     * 在每次测试开始前, 创建用于测试的目录和文件
     */
    @BeforeEach
    void createFileTree() throws IOException {
        var files = new File[] {
            new File(parent, "1.txt"),
            new File(parent, "2.txt"),
            new File(parent, "a/a1.txt"),
            new File(parent, "a/a2.txt"),
            new File(parent, "a/aa/aa1.txt"),
            new File(parent, "a/aa/aaa/aaa1.txt"),
            new File(parent, "b/b1.txt"),
            new File(parent, "b/bb/bb1.txt"),
            new File(parent, "c/cc/ccc/ccc1.txt")
        };

        // 创建测试目录, 并在测试目录中创建一系列子目录和文件
        for (var file : files) {
            var parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            file.createNewFile();
        }
    }

    /**
     * 每次测试完成后, 删除测试目录
     */
    @AfterEach
    void removeFileTree() {
        Files.fileTraverser().depthFirstPostOrder(parent).forEach(f -> f.delete());
        then(parent).doesNotExist();
    }

    /**
     * 利用方法递归方式对指定目录和其子目录进行遍历
     *
     * @param dir    要遍历的文件路径
     * @param onFile 参数为所遍历文件的回调函数
     */
    static void fileRecursive(File dir, Consumer<File> onFile) {
        Preconditions.checkArgument(dir.isDirectory(), "Argument dir must be a directory");
        Preconditions.checkArgument(onFile != null, "Argument onFile must non null");

        // 列举目录下面的所有子目录和文件
        var dirs = dir.listFiles();
        // 对列举的内容进行排序, 将目录排到文件之前
        Arrays.sort(dirs, (l, r) -> {
            if (l.isDirectory()) {
                return -1;
            }
            if (r.isDirectory()) {
                return 1;
            }
            return r.compareTo(l);
        });

        // 遍历列举的目录和文件
        for (var file : dirs) {
            if (file.isDirectory()) {
                // 对于目录, 调用递归方法
                fileRecursive(file, onFile);
            }
            onFile.accept(file);
        }
    }

    /**
     * 测试 {@link #fileRecursive(File, Consumer)} 方法, 通过递归方法调用遍历目录
     */
    @Test
    void recursive_shouldTraverserFilesByRecursion() {
        var files = Lists.<File>newArrayList();

        // 调用方法进行遍历
        fileRecursive(parent, file -> files.add(file));

        // 确认遍历结果
        then(files).map(File::toString).containsExactly(
            parent.toString() + "/c/cc/ccc/ccc1.txt",
            parent.toString() + "/c/cc/ccc",
            parent.toString() + "/c/cc",
            parent.toString() + "/c",
            parent.toString() + "/a/aa/aaa/aaa1.txt",
            parent.toString() + "/a/aa/aaa",
            parent.toString() + "/a/aa/aa1.txt",
            parent.toString() + "/a/aa",
            parent.toString() + "/a/a2.txt",
            parent.toString() + "/a/a1.txt",
            parent.toString() + "/a",
            parent.toString() + "/b/bb/bb1.txt",
            parent.toString() + "/b/bb",
            parent.toString() + "/b/b1.txt",
            parent.toString() + "/b",
            parent.toString() + "/2.txt",
            parent.toString() + "/1.txt");
    }

    /**
     *
     */
    @Test
    void breadthFirst_shouldTraverserFilesBreadthFirst() {
        var files = Lists.<File>newArrayList();

        var queue = Queues.<File>newArrayDeque();
        queue.offer(parent);

        while (!queue.isEmpty()) {
            var file = queue.poll();
            if (file.isDirectory()) {
                Arrays.stream(file.listFiles()).forEach(f -> queue.offer(f));
            }
            files.add(file);
        }

        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent.toString() + "/b",
            parent.toString() + "/1.txt",
            parent.toString() + "/a",
            parent.toString() + "/c",
            parent.toString() + "/2.txt",
            parent.toString() + "/b/bb",
            parent.toString() + "/b/b1.txt",
            parent.toString() + "/a/a1.txt",
            parent.toString() + "/a/aa",
            parent.toString() + "/a/a2.txt",
            parent.toString() + "/c/cc",
            parent.toString() + "/b/bb/bb1.txt",
            parent.toString() + "/a/aa/aa1.txt",
            parent.toString() + "/a/aa/aaa",
            parent.toString() + "/c/cc/ccc",
            parent.toString() + "/a/aa/aaa/aaa1.txt",
            parent.toString() + "/c/cc/ccc/ccc1.txt");
    }

    @Test
    void depthFirst_shouldTraverserFilesDepthFirst() {
        var files = Lists.<File>newArrayList();

        Deque<File> stack = Lists.<File>newLinkedList();
        stack.push(parent);

        while (!stack.isEmpty()) {
            var file = stack.pop();
            if (file.isDirectory()) {
                for (var child : file.listFiles()) {
                    stack.push(child);
                }
            }
            files.add(file);
        }

        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent.toString() + "/2.txt",
            parent.toString() + "/c",
            parent.toString() + "/c/cc",
            parent.toString() + "/c/cc/ccc",
            parent.toString() + "/c/cc/ccc/ccc1.txt",
            parent.toString() + "/a",
            parent.toString() + "/a/a2.txt",
            parent.toString() + "/a/aa",
            parent.toString() + "/a/aa/aaa",
            parent.toString() + "/a/aa/aaa/aaa1.txt",
            parent.toString() + "/a/aa/aa1.txt",
            parent.toString() + "/a/a1.txt",
            parent.toString() + "/1.txt",
            parent.toString() + "/b",
            parent.toString() + "/b/b1.txt",
            parent.toString() + "/b/bb",
            parent.toString() + "/b/bb/bb1.txt");
    }

    @Test
    void newDirectoryStream_shouldListFileByDirectoryStream() throws IOException {
        var files = Lists.<File>newArrayList();

        Queue<Path> queue = Lists.<Path>newLinkedList();
        queue.offer(parent.toPath());

        while (!queue.isEmpty()) {
            var path = queue.poll();
            if (java.nio.file.Files.isDirectory(path)) {
                try (var s = java.nio.file.Files.newDirectoryStream(path)) {
                    s.forEach(p -> queue.offer(p));
                }
            }

            files.add(path.toFile());
        }

        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent.toString() + "/b",
            parent.toString() + "/1.txt",
            parent.toString() + "/a",
            parent.toString() + "/c",
            parent.toString() + "/2.txt",
            parent.toString() + "/b/bb",
            parent.toString() + "/b/b1.txt",
            parent.toString() + "/a/a1.txt",
            parent.toString() + "/a/aa",
            parent.toString() + "/a/a2.txt",
            parent.toString() + "/c/cc",
            parent.toString() + "/b/bb/bb1.txt",
            parent.toString() + "/a/aa/aa1.txt",
            parent.toString() + "/a/aa/aaa",
            parent.toString() + "/c/cc/ccc",
            parent.toString() + "/a/aa/aaa/aaa1.txt",
            parent.toString() + "/c/cc/ccc/ccc1.txt");
    }

    @Test
    void list_shouldListFilesByNIO() throws IOException {
        var files = Lists.<File>newArrayList();

        Queue<Path> queue = Lists.<Path>newLinkedList();
        queue.offer(parent.toPath());

        while (!queue.isEmpty()) {
            var path = queue.poll();
            if (java.nio.file.Files.isDirectory(path)) {
                try (var s = java.nio.file.Files.list(path)) {
                    s.forEach(p -> queue.offer(p));
                }
            }

            files.add(path.toFile());
        }

        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent.toString() + "/b",
            parent.toString() + "/1.txt",
            parent.toString() + "/a",
            parent.toString() + "/c",
            parent.toString() + "/2.txt",
            parent.toString() + "/b/bb",
            parent.toString() + "/b/b1.txt",
            parent.toString() + "/a/a1.txt",
            parent.toString() + "/a/aa",
            parent.toString() + "/a/a2.txt",
            parent.toString() + "/c/cc",
            parent.toString() + "/b/bb/bb1.txt",
            parent.toString() + "/a/aa/aa1.txt",
            parent.toString() + "/a/aa/aaa",
            parent.toString() + "/c/cc/ccc",
            parent.toString() + "/a/aa/aaa/aaa1.txt",
            parent.toString() + "/c/cc/ccc/ccc1.txt");
    }

    @Test
    void walk_shouldListFilesUsingFileWalk() throws IOException {
        List<File> files;

        try (var stream = java.nio.file.Files.walk(parent.toPath(), Integer.MAX_VALUE)) {
            files = stream.map(Path::toFile).toList();
        }

        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent.toString() + "/b",
            parent.toString() + "/b/bb",
            parent.toString() + "/b/bb/bb1.txt",
            parent.toString() + "/b/b1.txt",
            parent.toString() + "/1.txt",
            parent.toString() + "/a",
            parent.toString() + "/a/a1.txt",
            parent.toString() + "/a/aa",
            parent.toString() + "/a/aa/aa1.txt",
            parent.toString() + "/a/aa/aaa",
            parent.toString() + "/a/aa/aaa/aaa1.txt",
            parent.toString() + "/a/a2.txt",
            parent.toString() + "/c",
            parent.toString() + "/c/cc",
            parent.toString() + "/c/cc/ccc",
            parent.toString() + "/c/cc/ccc/ccc1.txt",
            parent.toString() + "/2.txt");
    }

    @Test
    void walkTree_shouldListFilesUsingFileWalk() throws IOException {
        var files = Lists.<File>newArrayList();

        java.nio.file.Files.walkFileTree(parent.toPath(), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                files.add(file.toFile());
                return FileVisitResult.CONTINUE;
            }
        });

        then(files).map(File::toString).containsExactly(
            parent.toString() + "/b/bb/bb1.txt",
            parent.toString() + "/b/b1.txt",
            parent.toString() + "/1.txt",
            parent.toString() + "/a/a1.txt",
            parent.toString() + "/a/aa/aa1.txt",
            parent.toString() + "/a/aa/aaa/aaa1.txt",
            parent.toString() + "/a/a2.txt",
            parent.toString() + "/c/cc/ccc/ccc1.txt",
            parent.toString() + "/2.txt");
    }

    @Test
    void fileTraverser_shouldVisitAllFilesByGuava() {
        var traverser = Files.fileTraverser();

        var files = ImmutableList.copyOf(traverser.breadthFirst(parent));
        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent.toString() + "/b",
            parent.toString() + "/1.txt",
            parent.toString() + "/a",
            parent.toString() + "/c",
            parent.toString() + "/2.txt",
            parent.toString() + "/b/bb",
            parent.toString() + "/b/b1.txt",
            parent.toString() + "/a/a1.txt",
            parent.toString() + "/a/aa",
            parent.toString() + "/a/a2.txt",
            parent.toString() + "/c/cc",
            parent.toString() + "/b/bb/bb1.txt",
            parent.toString() + "/a/aa/aa1.txt",
            parent.toString() + "/a/aa/aaa",
            parent.toString() + "/c/cc/ccc",
            parent.toString() + "/a/aa/aaa/aaa1.txt",
            parent.toString() + "/c/cc/ccc/ccc1.txt");

        files = ImmutableList.copyOf(traverser.depthFirstPreOrder(parent));
        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent.toString() + "/b",
            parent.toString() + "/b/bb",
            parent.toString() + "/b/bb/bb1.txt",
            parent.toString() + "/b/b1.txt",
            parent.toString() + "/1.txt",
            parent.toString() + "/a",
            parent.toString() + "/a/a1.txt",
            parent.toString() + "/a/aa",
            parent.toString() + "/a/aa/aa1.txt",
            parent.toString() + "/a/aa/aaa",
            parent.toString() + "/a/aa/aaa/aaa1.txt",
            parent.toString() + "/a/a2.txt",
            parent.toString() + "/c",
            parent.toString() + "/c/cc",
            parent.toString() + "/c/cc/ccc",
            parent.toString() + "/c/cc/ccc/ccc1.txt",
            parent.toString() + "/2.txt");

        files = ImmutableList.copyOf(traverser.depthFirstPostOrder(parent));
        then(files).map(File::toString).containsExactly(
            parent.toString() + "/b/bb/bb1.txt",
            parent.toString() + "/b/bb",
            parent.toString() + "/b/b1.txt",
            parent.toString() + "/b",
            parent.toString() + "/1.txt",
            parent.toString() + "/a/a1.txt",
            parent.toString() + "/a/aa/aa1.txt",
            parent.toString() + "/a/aa/aaa/aaa1.txt",
            parent.toString() + "/a/aa/aaa",
            parent.toString() + "/a/aa",
            parent.toString() + "/a/a2.txt",
            parent.toString() + "/a",
            parent.toString() + "/c/cc/ccc/ccc1.txt",
            parent.toString() + "/c/cc/ccc",
            parent.toString() + "/c/cc",
            parent.toString() + "/c",
            parent.toString() + "/2.txt",
            parent.toString());
    }
}
