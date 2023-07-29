package alvin.study.guava.io;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * 用于测试对象序列化的简单类型
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Staff implements Serializable {
    private final long id;
    private final String name;

    @SneakyThrows
    public static Staff deserialize(byte[] data) {
        try (var oi = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (Staff) oi.readObject();
        }
    }
}
