package alvin.study.se.concurrent;

import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

public class ExecutorServiceTest {
    @Test
    void ss() {
        try (var executor = Executors.newSingleThreadExecutor()) {}
    }
}
