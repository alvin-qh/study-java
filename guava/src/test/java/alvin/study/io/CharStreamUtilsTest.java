package alvin.study.io;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.jupiter.api.Test;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

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
