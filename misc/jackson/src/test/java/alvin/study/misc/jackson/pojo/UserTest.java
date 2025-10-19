package alvin.study.misc.jackson.pojo;

import static org.assertj.core.api.BDDAssertions.then;

import java.security.Security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import alvin.study.misc.jackson.pojo.view.InternalView;
import alvin.study.misc.jackson.pojo.view.PublicView;

/**
 * 测试 {@link User} 类型的 JSON 序列化以及反序列化操作
 *
 * <p>
 * 本测试使用了视图功能, 通过指定视图类型, 可以将一个对象序列化为不同的 JSON 结果,
 * 通过 {@link User#getMobile()} 以及 {@link User#getEncodedMobile()}
 * 方法上的 {@link com.fasterxml.jackson.annotation.JsonView @JsonView}
 * 注解来指定视图类型
 * </p>
 */
class UserTest {
    // 要序列化的对象
    private static final User OBJECT = new User(
        1L,
        "Alvin",
        "13991312123",
        "");

    // 期待的通过 PublicView 视图类型得到的序列化结果
    private static final String PUBLIC_VIEW_JSON = """
        {"id":1,"username":"Alvin","encodedMobile":"D1K1YwoZk5efKfWUrcIwig=="}""";

    // 期待的通过 InternalView 视图类型得到的序列化结果
    private static final String INTERNAL_VIEW_JSON = """
        {"id":1,"username":"Alvin","mobile":"13991312123"}""";

    /**
     * 在每次测试执行前执行
     *
     * <p>
     * 设置安全属性
     * </p>
     */
    @BeforeEach
    void beforeEach() {
        // 设置安全属性
        Security.setProperty("crypto.policy", "unlimited");
    }

    /**
     * 测试通过 {@link PublicView} 视图序列化 {@link User} 对象
     */
    @Test
    void toJson_shouldEncodeUserObjectToJsonWithPublicView() throws Exception {
        // 序列化对象, 使用 PublicView 视图标识
        var mapper = new ObjectMapper();
        var writer = mapper.writerWithView(PublicView.class);

        // 将对象序列化为 JSON 字符串, 确认序列化结果符合预期
        var json = writer.writeValueAsString(OBJECT);
        then(json).isEqualTo(PUBLIC_VIEW_JSON);
    }

    /**
     * 测试通过 {@link InternalView} 视图序列化 {@link User} 对象
     */
    @Test
    void toJson_shouldEncodeUserObjectToJsonWithInternalView() throws Exception {
        // 序列化对象, 使用 InternalView 视图标识
        var mapper = new ObjectMapper();
        var writer = mapper.writerWithView(InternalView.class);

        // 将对象序列化为 JSON 字符串, 确认序列化结果符合预期
        var json = writer.writeValueAsString(OBJECT);
        then(json).isEqualTo(INTERNAL_VIEW_JSON);
    }

    /**
     * 测试将具备 {@code encodedMobile} 字段的 JSON 反序列化为 {@link User} 对象
     *
     * <p>
     * 该测试会通过 {@link User#User(Long, String, String, String)} 构造器创建
     * {@link User} 对象, 由于 JSON 字符串中包含 {@code encodedMobile} 字段,
     * 因此会将该字段值解密后, 设置到 {@link User#mobile} 字段上
     * </p>
     */
    @Test
    void fromJson_shouldDecodePublicViewJsonToUserObject() throws Exception {
        var mapper = new ObjectMapper();

        // 将对象序列化为 JSON 字符串, 确认序列化结果符合预期
        var obj = mapper.readValue(PUBLIC_VIEW_JSON, User.class);
        then(obj).isEqualTo(OBJECT);
    }

    /**
     * 测试将具备 {@code mobile} 字段的 JSON 反序列化为 {@link User} 对象
     *
     * <p>
     * 该测试会通过 {@link User#User(Long, String, String, String)} 构造器创建
     * {@link User} 对象, 由于 JSON 字符串中包含 {@code mobile} 字段,
     * 因此会将该字段值直接设置到 {@link User#mobile} 字段上
     * </p>
     */
    @Test
    void fromJson_shouldDecodeInternalViewJsonToUserObject() throws Exception {
        var mapper = new ObjectMapper();

        // 将对象序列化为 JSON 字符串, 确认序列化结果符合预期
        var obj = mapper.readValue(INTERNAL_VIEW_JSON, User.class);
        then(obj).isEqualTo(OBJECT);
    }
}
