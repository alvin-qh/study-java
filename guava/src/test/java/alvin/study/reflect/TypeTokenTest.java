package alvin.study.reflect;

import com.google.common.reflect.TypeToken;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link com.google.common.reflect.TypeToken} 类型
 */
public class TypeTokenTest {
    @Test
    void of_shouldGetTypeTokenFromClassObject() {
        var token = TypeToken.of(String.class);

        then(token.getType()).isEqualTo(String.class);
        then(token.getRawType()).isEqualTo(String.class);
    }

    @Test
    void of_shouldGetTypeTokenFromGenericClassObject() {
        var token = new TypeToken<List<String>>() {
        };

        then(token.getType())
            .isInstanceOf(ParameterizedType.class)
            .extracting(t -> (ParameterizedType) t)
            .extracting(ParameterizedType::getActualTypeArguments)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .hasSize(1)
            .contains(String.class);

        then(token.getRawType()).isEqualTo(List.class);
    }

    @Test
    void isArray_shouldCheckTypeIsArray() {
        var token = TypeToken.of(String[].class);

        then(token.isArray()).isTrue();
    }
}
