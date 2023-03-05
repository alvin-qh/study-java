package alvin.study.io;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Deque;
import java.util.Queue;
import java.util.UUID;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

class FileTraverserTest {
    private final File parent = new File(UUID.randomUUID().toString());

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

        for (var file : files) {
            var parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            file.createNewFile();
        }
    }

    @AfterEach
    void removeFileTree() {
        Files.fileTraverser().depthFirstPostOrder(parent).forEach(f -> f.delete());
    }

    static void fileRecursive(File dir, Consumer<File> onFile) {
        Preconditions.checkArgument(dir.isDirectory(), "Argument dir must be a directory");
        Preconditions.checkArgument(onFile != null, "Argument onFile must non null");

        for (var file : dir.listFiles()) {
            if (file.isDirectory()) {
                fileRecursive(file, onFile);
            }
            onFile.accept(file);
        }
    }

    @Test
    void recursive_shouldTraverserFilesByRecursion() {
        var files = Lists.<File>newArrayList();

        fileRecursive(parent, file -> files.add(file));
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
            parent.toString() + "/2.txt");
    }

    @Test
    void breadthFirst_shouldTraverserFilesBreadthFirst() {
        var files = Lists.<File>newArrayList();

        Queue<File> queue = Lists.<File>newLinkedList();
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
                var childFiles = Lists.<File>newLinkedList();
                for (var child : file.listFiles()) {
                    if (child.isFile()) {
                        stack.push(child);
                    } else {
                        childFiles.add(child);
                    }
                }
                childFiles.forEach(f -> stack.push(f));
            }
            files.add(file);
        }

        then(files).map(File::toString).containsExactly(
            parent.toString(),
            parent.toString() + "/c",
            parent.toString() + "/c/cc",
            parent.toString() + "/c/cc/ccc",
            parent.toString() + "/c/cc/ccc/ccc1.txt",
            parent.toString() + "/a",
            parent.toString() + "/a/aa",
            parent.toString() + "/a/aa/aaa",
            parent.toString() + "/a/aa/aaa/aaa1.txt",
            parent.toString() + "/a/aa/aa1.txt",
            parent.toString() + "/a/a2.txt",
            parent.toString() + "/a/a1.txt",
            parent.toString() + "/b",
            parent.toString() + "/b/bb",
            parent.toString() + "/b/bb/bb1.txt",
            parent.toString() + "/b/b1.txt",
            parent.toString() + "/2.txt",
            parent.toString() + "/1.txt");
    }

    @Test
    void newDirectoryStream_shouldListFileByDirectoryStream() throws IOException {
        var files = Lists.<File>newArrayList();

        Queue<Path> queue = Lists.<Path>newLinkedList();
        queue.offer(parent.toPath());

        while (!queue.isEmpty()) {
            var path = queue.poll();
            if (java.nio.file.Files.isDirectory(path)) {
                java.nio.file.Files.newDirectoryStream(path).forEach(p -> queue.offer(p));
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
    void list_shouldListFiles() throws IOException {
        var files = Lists.<File>newArrayList();

        Queue<Path> queue = Lists.<Path>newLinkedList();
        queue.offer(parent.toPath());

        while (!queue.isEmpty()) {
            var path = queue.poll();
            if (java.nio.file.Files.isDirectory(path)) {
                java.nio.file.Files.list(path).forEach(p -> queue.offer(p));
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
