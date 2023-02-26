package alvin.study.io;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.jupiter.api.Test;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

/**
 * 测试 {@link CharStreams} 工具类
 *
 * <p>
 * {@link CharStreams} 工具类集合了对字符流操作的一系列方法, 以简化字符流操作的复杂性
 * </p>
 */
class CharStreamUtilsTest {
    private static Reader asReader(String s) {
        return new InputStreamReader(new ByteArrayInputStream(s.getBytes(Charsets.UTF_8)));
    }

    @Test
    void toString_shouldReadCharactersFromReadableIntoString() throws IOException {
        try (var reader = asReader("Hello Guava")) {
            var s = CharStreams.toString(reader);
            then(s).isEqualTo("Hello Guava");
        }
    }

    @Test
    void readLines_shouldReadLinesFromReadable() throws IOException {
        try (var reader = asReader("""
            Line1
            Line2
            Line3
            """)) {
            var lines = CharStreams.readLines(reader);
            then(lines).containsExactly("Line1", "Line2", "Line3");
        }

        try (var reader = asReader("""
            Line1
            Line2
            Line3
            """)) {
            var lines = CharStreams.readLines(reader, new LineProcessor<String>() {
                private final StringBuilder builder = new StringBuilder();

                @Override
                public boolean processLine(String line) throws IOException {
                    if (builder.length() > 0) {
                        builder.append(">>");
                    }
                    builder.append(line);
                    return true;
                }

                @Override
                public String getResult() {
                    return builder.toString();
                }
            });
            then(lines).isEqualTo("Line1>>Line2>>Line3");
        }
    }

    @Test
    void asWriter_shouldWrapAppendableIntoWriterObject() throws IOException {
        var builder = new StringBuilder();

        try (var writer = CharStreams.asWriter(builder)) {
            writer.write("Hello Guava");
            writer.write('.');
        }
        then(builder).hasToString("Hello Guava.");
    }

    @Test
    void copy_shouldCopyCharactersFromReadableIntoAppendable() throws IOException {
        try (var reader = asReader("Hello Guava")) {
            var builder = new StringBuilder();
            var len = CharStreams.copy(reader, builder);

            then(len).isEqualTo(11);
            then(builder).hasToString("Hello Guava");
        }
    }
}
