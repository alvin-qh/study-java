package alvin.study.guava.io;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Queues;
import com.google.common.graph.Traverser;
import com.google.common.io.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.BDDAssertions.then;

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

    // 定义文件排序比较器
    private final Ordering<File> fileOrdering;

    /**
     * 构造器, 创建测试目录
     */
    public FileTraverserTest() throws IOException {
        // 创建临时路径, 作为测试根路径
        parent = java.nio.file.Files.createTempDirectory("test-guava").toFile();

        // 定义文件比较排序器
        fileOrdering = Ordering.from((left, right) -> {
            if (left.isFile()) {
                if (right.isFile()) {
                    return left.compareTo(right);
                } else {
                    return 1;
                }
            } else {
                if (right.isDirectory()) {
                    return left.compareTo(right);
                } else {
                    return -1;
                }
            }
        });
    }

    /**
     * 在每次测试开始前, 创建用于测试的目录和文件
     */
    @BeforeEach
    void createFileTree() throws IOException {
        // 定义要创建的文件列表
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
        // 通过深度优先进行遍历, 并从路径的最深处向上依次删除文件和目录
        Files.fileTraverser().depthFirstPostOrder(parent).forEach(File::delete);

        // 确认所有的文件和目录均已被删除
        then(parent).doesNotExist();
    }

    /**
     * 利用方法递归方式对指定目录和其子目录进行遍历
     *
     * @param dir    要遍历的文件路径
     * @param onFile 参数为所遍历文件的回调函数
     */
    private void fileRecursive(File dir, Consumer<File> onFile) {
        Preconditions.checkArgument(dir.isDirectory(), "Argument dir must be a directory");
        Preconditions.checkArgument(onFile != null, "Argument onFile must non null");

        // 获取文件夹下的内容
        var files = dir.listFiles();
        if (files == null) {
            return;
        }

        // 将文件夹中的内容排序后进行遍历
        Arrays.stream(files).sorted(fileOrdering).forEach(f -> {
            // 对于文件夹, 进行递归处理
            if (f.isDirectory()) {
                fileRecursive(f, onFile);
            }
            // 调用回调函数
            onFile.accept(f);
        });
    }

    /**
     * 测试 {@link #fileRecursive(File, Consumer)} 方法, 通过递归方法调用遍历目录
     */
    @Test
    void recursive_shouldTraverserFilesByRecursion() {
        var files = Lists.<File>newArrayList();

        // 调用方法进行遍历
        fileRecursive(parent, files::add);

        // 确认遍历结果
        then(files).map(File::toString).containsExactly(
            parent + "/a/aa/aaa/aaa1.txt",
            parent + "/a/aa/aaa",
            parent + "/a/aa/aa1.txt",
            parent + "/a/aa",
            parent + "/a/a1.txt",
            parent + "/a/a2.txt",
            parent + "/a",
            parent + "/b/bb/bb1.txt",
            parent + "/b/bb",
            parent + "/b/b1.txt",
            parent + "/b",
            parent + "/c/cc/ccc/ccc1.txt",
            parent + "/c/cc/ccc",
            parent + "/c/cc",
            parent + "/c",
            parent + "/1.txt",
            parent + "/2.txt");
    }

    /**
     * 使用广度优先算法 (BFS) 对文件树进行遍历
     *
     * <p>
     * 借助队列, 通过先入先出 (FIFO) 顺序对每个文件夹的子文件夹进行逐层展开遍历
     * </p>
     */
    @Test
    void breadthFirst_shouldTraverserFilesBreadthFirst() {
        var files = Lists.<File>newArrayList();

        // 定义用于文件树遍历的队列
        var queue = Queues.<File>newArrayDeque();
        // 将根路径入队
        queue.offer(parent);

        // 通过队列进行广度优先遍历
        while (!queue.isEmpty()) {
            // 获取队首元素
            var file = queue.poll();

            // 判断获取的路径是否表示目录
            if (file.isDirectory()) {
                var subFiles = file.listFiles();
                if (subFiles != null) {
                    // 将目录的下级内容排序后入队
                    Arrays.stream(subFiles).sorted(fileOrdering).forEach(queue::offer);
                }
            }

            // 保持此次遍历结果
            files.add(file);
        }

        // 确认遍历结果和其顺序
        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent + "/a",
            parent + "/b",
            parent + "/c",
            parent + "/1.txt",
            parent + "/2.txt",
            parent + "/a/aa",
            parent + "/a/a1.txt",
            parent + "/a/a2.txt",
            parent + "/b/bb",
            parent + "/b/b1.txt",
            parent + "/c/cc",
            parent + "/a/aa/aaa",
            parent + "/a/aa/aa1.txt",
            parent + "/b/bb/bb1.txt",
            parent + "/c/cc/ccc",
            parent + "/a/aa/aaa/aaa1.txt",
            parent + "/c/cc/ccc/ccc1.txt");
    }

    /**
     * 使用深度优先算法 (DFS) 对文件树进行遍历
     *
     * <p>
     * 借助栈, 通过后入先出 (LIFO) 顺序, 沿一条路径遍历到文件树一条分支的最深处后, 再回头遍历文件树的其它分支
     * </p>
     */
    @Test
    void depthFirst_shouldTraverserFilesDepthFirst() {
        var files = Lists.<File>newArrayList();

        // 定义用于文件树遍历的栈
        var stack = Queues.<File>newArrayDeque();
        stack.push(parent);

        // 通过栈进行深度优先遍历
        while (!stack.isEmpty()) {
            // 弹出栈顶元素
            var file = stack.pop();
            // 判断是否为文件夹, 如果是文件夹, 进一步处理文件夹下的子文件夹和文件
            if (file.isDirectory()) {
                var subFiles = file.listFiles();
                if (subFiles != null) {
                    // 将目录的下级内容排序后入栈
                    Arrays
                            .stream(Objects.requireNonNull(file.listFiles()))
                            .sorted(fileOrdering)
                            .forEach(stack::push);
                }
            }

            // 添加本次遍历结果
            files.add(file);
        }

        // 确认遍历结果和其顺序
        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent + "/2.txt",
            parent + "/1.txt",
            parent + "/c",
            parent + "/c/cc",
            parent + "/c/cc/ccc",
            parent + "/c/cc/ccc/ccc1.txt",
            parent + "/b",
            parent + "/b/b1.txt",
            parent + "/b/bb",
            parent + "/b/bb/bb1.txt",
            parent + "/a",
            parent + "/a/a2.txt",
            parent + "/a/a1.txt",
            parent + "/a/aa",
            parent + "/a/aa/aa1.txt",
            parent + "/a/aa/aaa",
            parent + "/a/aa/aaa/aaa1.txt");
    }

    /**
     * 通过 NIO2 进行文件遍历
     *
     * <p>
     * 通过 {@link java.nio.file.Files#newDirectoryStream(Path) Files.newDirectoryStream(Path)} 方法可以获取文件夹下的内容,
     * 返回结果为 {@link java.nio.file.DirectoryStream DirectoryStream} 类型对象, 通过其
     * {@link java.nio.file.DirectoryStream#forEach(Consumer) DirectoryStream.forEach(Consumer)} 方法可以对这些内容进行遍历
     * </p>
     *
     * <p>
     * 注意, {@link java.nio.file.DirectoryStream DirectoryStream} 对象需要通过 {@code close} 方法关闭
     * </p>
     *
     * <p>
     * 注意, NIO2 中, 所有的操作的路径对象为 {@link Path} 类型
     * </p>
     */
    @Test
    void newDirectoryStream_shouldListFileByDirectoryStream() throws IOException {
        var files = Lists.<File>newArrayList();

        // 定义用于广度优先遍历的队列
        var que = Queues.<Path>newArrayDeque();
        // 将根路径入队
        que.offer(parent.toPath());

        // 通过队列进行广度优先遍历
        while (!que.isEmpty()) {
            // 获取队首元素
            var path = que.poll();

            // 如果是文件夹, 则进一步处理文件夹下的内容
            if (java.nio.file.Files.isDirectory(path)) {
                // 从文件夹创建 DirectoryStream 对象
                try (var s = java.nio.file.Files.newDirectoryStream(path)) {
                    // 从 DirectoryStream 对象创建 Stream 对象, 排序后, 将其内容入队
                    StreamSupport.stream(s.spliterator(), false)
                            .map(Path::toFile)
                            .sorted(fileOrdering)
                            .map(File::toPath)
                            .forEach(que::offer);
                }
            }
            // 保存本次遍历的结果
            files.add(path.toFile());
        }

        // 确认遍历结果和其顺序
        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent + "/a",
            parent + "/b",
            parent + "/c",
            parent + "/1.txt",
            parent + "/2.txt",
            parent + "/a/aa",
            parent + "/a/a1.txt",
            parent + "/a/a2.txt",
            parent + "/b/bb",
            parent + "/b/b1.txt",
            parent + "/c/cc",
            parent + "/a/aa/aaa",
            parent + "/a/aa/aa1.txt",
            parent + "/b/bb/bb1.txt",
            parent + "/c/cc/ccc",
            parent + "/a/aa/aaa/aaa1.txt",
            parent + "/c/cc/ccc/ccc1.txt");
    }

    /**
     * 通过 NIO2 进行文件遍历
     *
     * <p>
     * 通过 {@link java.nio.file.Files#list(Path) Files.list(Path)} 方法可以获取一个 {@link java.util.stream.Stream Stream}
     * 对象, 其中包含了该路径下所有文件夹和文件, 可以进行遍历
     * </p>
     *
     * <p>
     * 注意, {@link java.nio.file.Files#list(Path) Files.list(Path)} 方法返回的 {@link java.util.stream.Stream Stream}
     * 对象需要通过 {@code close} 方法进行关闭
     * </p>
     *
     * <p>
     * 注意, NIO2 中, 所有的操作的路径对象为 {@link Path} 类型
     * </p>
     */
    @Test
    void list_shouldListFilesByNIO() throws IOException {
        var files = Lists.<File>newArrayList();

        // 定义用于广度优先遍历的队列
        var que = Queues.<Path>newArrayDeque();
        // 将根路径入队
        que.offer(parent.toPath());

        // 通过队列进行广度优先遍历
        while (!que.isEmpty()) {
            // 获取队首元素
            var path = que.poll();

            // 如果是文件夹, 则进一步处理文件夹下的内容
            if (java.nio.file.Files.isDirectory(path)) {
                // 从文件夹创建 Stream 对象
                try (var s = java.nio.file.Files.list(path)) {
                    // 对 Stream 内容排序后, 将其逐一入队
                    s.map(Path::toFile)
                            .sorted(fileOrdering)
                            .map(File::toPath)
                            .forEach(que::offer);
                }
            }
            // 保存本次遍历的结果
            files.add(path.toFile());
        }

        // 确认遍历结果和其顺序
        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent + "/a",
            parent + "/b",
            parent + "/c",
            parent + "/1.txt",
            parent + "/2.txt",
            parent + "/a/aa",
            parent + "/a/a1.txt",
            parent + "/a/a2.txt",
            parent + "/b/bb",
            parent + "/b/b1.txt",
            parent + "/c/cc",
            parent + "/a/aa/aaa",
            parent + "/a/aa/aa1.txt",
            parent + "/b/bb/bb1.txt",
            parent + "/c/cc/ccc",
            parent + "/a/aa/aaa/aaa1.txt",
            parent + "/c/cc/ccc/ccc1.txt");
    }

    /**
     * 通过 NIO2 进行文件遍历
     *
     * <p>
     * 通过 {@link java.nio.file.Files#walk(Path, java.nio.file.FileVisitOption...) Files.walk(Path, FileVisitOption...)}
     * 方法可以获取一个 {@link java.util.stream.Stream Stream}对象, 其中包含了该路径下所有文件夹和文件及其全部子文件夹内容, 可以进行遍历
     * </p>
     *
     * <p>
     * {@link java.nio.file.Files#walk(Path, java.nio.file.FileVisitOption...) Files.walk(Path, FileVisitOption...)}
     * 可以递归性的一次性获取一个文件夹下面的所有内容, 其算法基于深度优先算法 (DFS), 可以通过方法参数控制最多要深入的目录层级, 默认为
     * {@link Integer#MAX_VALUE}, 表示不限制, 直到遍历到目录树的最深层次为止
     * </p>
     *
     * <p>
     * 注意, {@link java.nio.file.Files#walk(Path, java.nio.file.FileVisitOption...) Files.walk(Path, FileVisitOption...)}
     * 方法返回的 {@link java.util.stream.Stream Stream} 对象需要通过 {@code close} 方法进行关闭
     * </p>
     *
     * <p>
     * 注意, NIO2 中, 所有的操作的路径对象为 {@link Path} 类型
     * </p>
     */
    @Test
    void walk_shouldListFilesUsingFileWalk() throws IOException {
        List<File> files;

        // 获取指定路径下, 包括子文件夹的所有内容
        try (var stream = java.nio.file.Files.walk(parent.toPath())) {
            // 注意, 这里为了保证结果的稳定性, 对结果进行了排序, 导致结果不再符合深度优先的原则
            // 正常情况下无须对遍历结果进行排序
            files = stream.map(Path::toFile).sorted(fileOrdering).toList();
        }

        // 确认遍历结果和其顺序
        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent + "/a",
            parent + "/a/aa",
            parent + "/a/aa/aaa",
            parent + "/b",
            parent + "/b/bb",
            parent + "/c",
            parent + "/c/cc",
            parent + "/c/cc/ccc",
            parent + "/1.txt",
            parent + "/2.txt",
            parent + "/a/a1.txt",
            parent + "/a/a2.txt",
            parent + "/a/aa/aa1.txt",
            parent + "/a/aa/aaa/aaa1.txt",
            parent + "/b/b1.txt",
            parent + "/b/bb/bb1.txt",
            parent + "/c/cc/ccc/ccc1.txt");
    }

    /**
     * 通过 NIO2 进行文件遍历
     *
     * <p>
     * 通过 {@link java.nio.file.Files#walkFileTree(Path, java.nio.file.FileVisitor)
     * Files.walkFileTree(Path, FileVisitor)} 方法对指定的路径下所有内容 (包括子文件夹) 进行遍历, 并通过一个
     * {@link java.nio.file.FileVisitor FileVisitor} 回调获取每次遍历的内容
     * </p>
     *
     * <p>
     * 在 {@link java.nio.file.FileVisitor FileVisitor} 回调中, 可以通过返回不同的 {@link FileVisitResult}
     * 对象以继续或停止之后的操作, 参见 {@link FileVisitResult#TERMINATE}, {@link FileVisitResult#CONTINUE},
     * {@link FileVisitResult#SKIP_SIBLINGS} 以及 {@link FileVisitResult#SKIP_SUBTREE} 值
     * </p>
     *
     * <p>
     * 注意, NIO2 中, 所有的操作的路径对象为 {@link Path} 类型
     * </p>
     */
    @Test
    void walkTree_shouldListFilesUsingFileWalkTree() throws IOException {
        var files = Lists.<File>newArrayList();

        java.nio.file.Files.walkFileTree(parent.toPath(), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                files.add(file.toFile());
                return FileVisitResult.CONTINUE;
            }
        });

        // 注意, 这里为了保证结果的稳定性, 对结果进行了排序, 导致结果不再符合深度优先的原则
        // 正常情况下无须对遍历结果进行排序
        files.sort(fileOrdering);

        // 确认遍历结果和其顺序
        then(files).map(File::toString).containsExactly(
            parent + "/1.txt",
            parent + "/2.txt",
            parent + "/a/a1.txt",
            parent + "/a/a2.txt",
            parent + "/a/aa/aa1.txt",
            parent + "/a/aa/aaa/aaa1.txt",
            parent + "/b/b1.txt",
            parent + "/b/bb/bb1.txt",
            parent + "/c/cc/ccc/ccc1.txt");
    }

    /**
     * 通过 Guava 进行文件遍历
     *
     * <p>
     * Guava 的 {@link Files#fileTraverser()} 方法可以返回一个 {@link Traverser} 对象, 通过该对象可以对指定的文件树进行遍历
     * </p>
     *
     * <p>
     * {@link Traverser#breadthFirst(Object) Traverser.breadthFirst(File)} 方法可以对指定路径进行广度优先遍历 (BFS)
     * </p>
     *
     * <p>
     * {@link Traverser#depthFirstPreOrder(Object) Traverser.depthFirstPreOrder(File)} 方法可以对指定路径进行正序深度优先遍历
     * (DFS)
     * </p>
     *
     * <p>
     * {@link Traverser#depthFirstPostOrder(Iterable) Traverser.depthFirstPostOrder(File)}
     * 方法可以对指定路径进行逆序深度优先遍历 (DFS)
     * </p>
     *
     * <p>
     * 注意, 为了保证遍历结果顺序的稳定, 本例中并未直接使用 {@link Files#fileTraverser()} 方法, 而是通过
     * {@link Traverser#forTree(com.google.common.graph.SuccessorsFunction) Traverser.forTree(SuccessorsFunction)}
     * 方法构建了一个专用于文件遍历的"树形结构"遍历器, 并为每次文件查询结果增加了排序操作以保证最终结果的稳定,
     * {@link Files#fileTraverser()} 内部也是类似实现
     * </p>
     */
    @Test
    void fileTraverser_shouldVisitAllFilesByGuava() {
        // 可以直接获取文件的树形结构遍历器, 但为了保证结果稳定, 本例中不采用
        // var traverser = Files.fileTraverser();

        // 自定义用于文件对象的树形结构遍历器
        var traverser = Traverser.<File>forTree(file -> {
            // 该方法用于获取所给定路径的后继节点, 即如果所给路径表示文件夹, 则继续处理其下的内容
            if (file.isDirectory()) {
                // 获取文件夹下的内容
                var files = file.listFiles();
                if (files != null) {
                    // 排序后返回
                    return Arrays.stream(files).sorted(fileOrdering).toList();
                }
            }
            // 返回空集合表示当前路径没有后继
            return ImmutableList.of();
        });

        // 测试广度优先遍历
        var files = ImmutableList.copyOf(traverser.breadthFirst(parent));
        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent + "/a",
            parent + "/b",
            parent + "/c",
            parent + "/1.txt",
            parent + "/2.txt",
            parent + "/a/aa",
            parent + "/a/a1.txt",
            parent + "/a/a2.txt",
            parent + "/b/bb",
            parent + "/b/b1.txt",
            parent + "/c/cc",
            parent + "/a/aa/aaa",
            parent + "/a/aa/aa1.txt",
            parent + "/b/bb/bb1.txt",
            parent + "/c/cc/ccc",
            parent + "/a/aa/aaa/aaa1.txt",
            parent + "/c/cc/ccc/ccc1.txt");

        // 测试正序深度优先遍历
        files = ImmutableList.copyOf(traverser.depthFirstPreOrder(parent));
        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent + "/a",
            parent + "/a/aa",
            parent + "/a/aa/aaa",
            parent + "/a/aa/aaa/aaa1.txt",
            parent + "/a/aa/aa1.txt",
            parent + "/a/a1.txt",
            parent + "/a/a2.txt",
            parent + "/b",
            parent + "/b/bb",
            parent + "/b/bb/bb1.txt",
            parent + "/b/b1.txt",
            parent + "/c",
            parent + "/c/cc",
            parent + "/c/cc/ccc",
            parent + "/c/cc/ccc/ccc1.txt",
            parent + "/1.txt",
            parent + "/2.txt");

        // 测试逆序深度优先遍历
        files = ImmutableList.copyOf(traverser.depthFirstPostOrder(parent));
        then(files).map(File::toString).containsExactly(
            parent + "/a/aa/aaa/aaa1.txt",
            parent + "/a/aa/aaa",
            parent + "/a/aa/aa1.txt",
            parent + "/a/aa",
            parent + "/a/a1.txt",
            parent + "/a/a2.txt",
            parent + "/a",
            parent + "/b/bb/bb1.txt",
            parent + "/b/bb",
            parent + "/b/b1.txt",
            parent + "/b",
            parent + "/c/cc/ccc/ccc1.txt",
            parent + "/c/cc/ccc",
            parent + "/c/cc",
            parent + "/c",
            parent + "/1.txt",
            parent + "/2.txt",
            parent.toString());
    }
}
