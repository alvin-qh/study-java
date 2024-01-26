package alvin.study.misc.jackson.pojo;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Strings;

import alvin.study.misc.jackson.encode.Encoder;
import alvin.study.misc.jackson.pojo.view.InternalView;
import alvin.study.misc.jackson.pojo.view.PublicView;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

/**
 * 通过视图控制 JSON 序列化结果
 *
 * <p>
 * 本例演示了如何通过 {@link JsonView @JsonView} 注解, 将不同的视图设置, 可以将同一个对象序列化为不同的 JSON 字符串
 * </p>
 *
 * <p>
 * 要按照视图序列化对象, 需要在 {@code ObjectMapper} 序列化时, 通过视图创建 {@link com.fasterxml.jackson.databind.ObjectWriter
 * ObjectWriter} 对象, 参考: {@link com.fasterxml.jackson.databind.ObjectMapper#writerWithView(Class)
 * ObjectMapper.writerWithView(Class)} 方法以及 {@link Encoder#toJson(Object)
 * Encoder.toJson(Object)} 方法
 * </p>
 *
 * <p>
 * 另外, 当前 POJO 对象应该可以根据不同 JSON 字段 (如: {@code mobile} 或者 {@code encodedMobile}
 * 字段反序列化对象
 * </p>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class User {
    // 用于加密 mobile 的密钥
    private static final byte[] SECRET_KEY = "uBdUx82vPHkDKb284d7NkjFoNcKWBuka".getBytes(); // cspell: disable-line

    // 加密的初始化向量
    private static final byte[] IV = "c558Gq0YQK2QUlMc".getBytes();

    private final Long id;
    private final String username;

    // 该字段不自动序列化, 而是通过 @JsonGetter 注解的 getter 方法序列化
    @JsonIgnore
    private final String mobile;

    /**
     * 通过 JSON 反序列化时的构造器, 设置了 JSON 字段和构造器参数间的关联关系
     *
     * @param id            {@code id} 属性
     * @param username      用户名
     * @param mobile        手机号, 该字段在 JSON 中包含 {@code mobile} 字段时生效
     * @param encodedMobile 加密的手机号, 该字段在 JSON 中包含 {@code encodedMobile} 字段时生效
     */
    @JsonCreator
    @SneakyThrows
    public User(
            @JsonProperty("id") Long id,
            @JsonProperty("username") String username,
            @JsonProperty("mobile") String mobile,
            @JsonProperty("encodedMobile") String encodedMobile) {
        this.id = id;
        this.username = username;

        // 根据 JSON 传入不同的字段, 分别处理不同的手机号码类型
        if (Strings.isNullOrEmpty(encodedMobile)) {
            // 将 JSON 的 mobile 字段直接设置到 mobile 字段值上
            this.mobile = mobile;
        } else {
            // 解密被加密的手机号
            var secretKey = new SecretKeySpec(SECRET_KEY, "AES");

            var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV));

            // 将 JSON 的 encodedMobile 字段解密后设置到 mobile 字段上
            this.mobile = new String(cipher.doFinal(Base64.getDecoder().decode(encodedMobile)), StandardCharsets.UTF_8);
        }
    }

    /**
     * 在视图为 {@link PublicView} 时, 序列化 {@code encodedMobile} 字段, 即对 {@code mobile} 字段进行加密后输出
     *
     * @return 加密后的 {@code mobile} 字段值
     */
    @JsonView(PublicView.class)
    @JsonGetter("encodedMobile")
    @SneakyThrows
    public String getEncodedMobile() {
        var secretKey = new SecretKeySpec(SECRET_KEY, "AES");

        var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV));

        var result = cipher.doFinal(mobile.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * 在视图为 {@link InternalView} 时, 序列化 {@code mobile} 字段
     *
     * @return {@code mobile} 字段值
     */
    @JsonView(InternalView.class)
    @JsonGetter("mobile")
    public String getMobile() { return this.mobile; }
}
