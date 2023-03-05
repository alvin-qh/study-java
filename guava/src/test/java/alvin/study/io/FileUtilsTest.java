package alvin.study.io;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.google.common.io.Files;

class FileUtilsTest {
    @Test
    void shouldCreateParentDirsOfFile() throws IOException {
        var parentPath = new File("test/aa/bb/cc");

        var newFile = new File(parentPath, "test-01.txt");
        newFile.deleteOnExit();

        then(parentPath).doesNotExist();

        try {
            Files.createParentDirs(newFile);
            then(parentPath).exists();
        } finally {
            deleteDirs(new File("test"));
        }
    }

    static void deleteDirs(File dir) {
        var traverser = Files.fileTraverser();
        traverser.depthFirstPostOrder(dir).forEach(f -> f.delete());
    }
}
