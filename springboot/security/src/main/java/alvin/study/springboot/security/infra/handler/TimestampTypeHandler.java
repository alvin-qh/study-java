package alvin.study.springboot.security.infra.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * 处理 {@link Timestamp} 类型字段值转换
 *
 * <p>
 * 由于 H2 数据库的某些原因, 将 {@code TIMESTAMP} 类型的字段转为 {@link Timestamp} 类型时会引发时区错误,
 * 本方法用来消除字段转换时的时区错误
 * </p>
 *
 * <p>
 * 解决方法为, 屏蔽 JDBC 在转换字段时的行为, 从 {@link ResultSet} 中获取原始字段字符串值, 根据正确的时区情况进行转换
 * </p>
 */
@MappedTypes(Timestamp.class)
@MappedJdbcTypes(JdbcType.TIMESTAMP)
public class TimestampTypeHandler extends BaseTypeHandler<Timestamp> {
    /**
     * 将 {@link Timestamp} 值设置到 {@link PreparedStatement} 中
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Timestamp parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setTimestamp(i, parameter);
    }

    /**
     * 从 {@link ResultSet} 中获取 {@link JdbcType#TIMESTAMP} 类型字段值, 并转为
     * {@link Timestamp} 类型返回
     */
    @Override
    public Timestamp getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 获取 TIMESTAMP 字段的字符串值, 转为 Timestamp 对象, 不做时区转换
        // 在本例中, 整个系统使用 UTC 时区, 所以存在数据表字段的时间值已经是 UTC 时间, 无需做任何转换
        return Timestamp.valueOf(rs.getString(columnName));
    }

    /**
     * 从 {@link ResultSet} 中获取 {@link JdbcType#TIMESTAMP} 类型字段值, 并转为
     * {@link Timestamp} 类型返回
     *
     * <p>
     * 由于 H2 数据库的某些原因, 将 {@code TIMESTAMP} 类型的字段转为 {@link Timestamp} 类型时会引发时区错误,
     * 本方法用来消除字段转换时的时区错误
     * </p>
     *
     * <p>
     * 解决方法为, 屏蔽 JDBC 在转换字段时的行为, 从 {@link ResultSet} 中获取原始字段字符串值, 根据正确的时区情况进行转换
     * </p>
     */
    @Override
    public Timestamp getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // 获取 TIMESTAMP 字段的字符串值, 转为 Timestamp 对象, 不做时区转换
        // 在本例中, 整个系统使用 UTC 时区, 所以存在数据表字段的时间值已经是 UTC 时间, 无需做任何转换
        return Timestamp.valueOf(rs.getString(columnIndex));
    }

    /**
     * 从 {@link ResultSet} 中获取 {@link JdbcType#TIMESTAMP} 类型字段值, 并转为
     * {@link Timestamp} 类型返回
     *
     * <p>
     * 由于 H2 数据库的某些原因, 将 {@code TIMESTAMP} 类型的字段转为 {@link Timestamp} 类型时会引发时区错误,
     * 本方法用来消除字段转换时的时区错误
     * </p>
     *
     * <p>
     * 解决方法为, 屏蔽 JDBC 在转换字段时的行为, 从 {@link ResultSet} 中获取原始字段字符串值, 根据正确的时区情况进行转换
     * </p>
     */
    @Override
    public Timestamp getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 获取 TIMESTAMP 字段的字符串值, 转为 Timestamp 对象, 不做时区转换
        // 在本例中, 整个系统使用 UTC 时区, 所以存在数据表字段的时间值已经是 UTC 时间, 无需做任何转换
        return Timestamp.valueOf(cs.getString(columnIndex));
    }
}
