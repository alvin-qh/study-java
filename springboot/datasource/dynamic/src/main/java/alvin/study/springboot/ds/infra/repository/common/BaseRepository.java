package alvin.study.springboot.ds.infra.repository.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 持久化操作超类
 */
public abstract class BaseRepository {
    // 时间日期格式
    private static final DateTimeFormatter DB_DATETIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .toFormatter();

    // 注入 JDBC 模板对象
    @Autowired
    private JdbcTemplate template;

    /**
     * 将所给的字符串转化为指定类型对象
     *
     * @param <T>   要转换的类型
     * @param value 要转换的原始字符串
     * @param type  要转换的类型
     * @return 转换结果
     */
    @SuppressWarnings("unchecked")
    protected static <T> T map(String value, Class<T> type) {
        if (type == Instant.class) {
            // 字符串转为 UTC 日期时间对象
            var ldt = LocalDateTime.parse(value, DB_DATETIME_FORMATTER);
            return (T) ldt.toInstant(ZoneOffset.UTC);
        }

        return null;
    }

    /**
     * 获取注入的 JDBC 模板对象
     *
     * @return JDBC 模板对象
     */
    protected JdbcTemplate template() {
        return template;
    }
}
