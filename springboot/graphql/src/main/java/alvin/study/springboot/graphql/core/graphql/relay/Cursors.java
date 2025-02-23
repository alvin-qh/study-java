package alvin.study.springboot.graphql.core.graphql.relay;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import jakarta.validation.constraints.NotNull;

import org.springframework.lang.Contract;

import com.google.common.base.Strings;

import graphql.relay.ConnectionCursor;
import graphql.relay.DefaultConnectionCursor;

import alvin.study.springboot.graphql.core.exception.FieldError;
import alvin.study.springboot.graphql.core.exception.InputException;

/**
 * 对查询结果在整体记录集合的位置 (游标) 进行编解码操作的类型
 *
 * <p>
 * 在本例中, 游标是通过一个 前缀 + 记录序号 (即第几条记录) 经过编码后的结果
 * </p>
 */
public class Cursors {
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    // 游标前缀
    private static final String DUMMY_CURSOR_PREFIX = "alvin_study_";

    private Cursors() {}

    /**
     * 从 Base64 字符串中解析游标的值
     *
     * @param cursor 游标字符串
     * @return 记录序号
     */
    public static  Integer parseCursor(String cursor) {
        if (Strings.isNullOrEmpty(cursor)) {
            return null;
        }
        try {
            var rawCursor = new String(DECODER.decode(cursor), StandardCharsets.UTF_8);
            if (!rawCursor.startsWith(DUMMY_CURSOR_PREFIX)) {
                throw new InputException("cursor");
            }
            return Integer.parseInt(rawCursor.substring(DUMMY_CURSOR_PREFIX.length()));
        } catch (IllegalArgumentException e) {
            throw new InputException().setFieldError(new FieldError("cursor", cursor));
        }
    }

    /**
     * 将记录序号编码为游标值
     *
     * @param cursor 记录序号
     * @return 游标值
     */
    public static String makeCursor(Integer cursor) {
        if (cursor == null) {
            return null;
        }
        var bytes = (DUMMY_CURSOR_PREFIX + cursor).getBytes(StandardCharsets.UTF_8);
        return ENCODER.encodeToString(bytes);
    }

    /**
     * 将记录序号编码为游标值
     *
     * @param cursor 记录序号
     * @return 游标值
     */
    @Contract("_ -> new")
    public static @NotNull ConnectionCursor makeConnCursor(Integer cursor) {
        return new DefaultConnectionCursor(makeCursor(cursor));
    }
}
