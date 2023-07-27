package alvin.study.se.timing;

import com.google.common.annotations.VisibleForTesting;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间转换工具类
 *
 * <p>
 * 基本概念:
 * <ul>
 * <li>
 * {@link Instant}: UTC 时间, 标准格式为 {@code 2021-09-11T00:00:00.000T}
 * </li>
 * <li>
 * {@link LocalDateTime}: 本地时间, 不带时区信息, 只是一个时间数值, 标准格式为
 * {@code 2021-09-11T08:00:00.000}
 * </li>
 * <li>
 * {@link OffsetDateTime}: 带有时区偏移量的时间, 即不带有具体时区信息, 只是一个相对于 UTC 时区的偏移, 标准格式为
 * {@code 2021-09-11T08:00:00.000+08:00}
 * </li>
 * <li>
 * {@link ZonedDateTime}: 带有时区的时间, 带有具体的时区信息和时区偏移量的时间, 标准格式为
 * {@code 2021-09-11T08:00:00.000[Asia/Shanghai]}
 * </li>
 * <li>
 * {@link Date}: Java 早期的日期时间对象, 为一个 UTC 的 Unix Timestamp (自1970年1月1日起的毫秒数),
 * 并带有时区信息
 * </li>
 * <li>
 * {@link Calendar}: Java 的日历对象, 用于计算年月日
 * </li>
 * </u>
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Times {
    /**
     * 用于定义获取当前时间的计时器对象
     *
     * <p>
     * 使用该对象的目的是防止在测试时获取当前时间会得到不断变化的值, 无法进行断言, 此时可以定义一个固定的 {@link Clock} 对象解决此问题
     * </p>
     */
    private static Clock clock = Clock.systemDefaultZone();

    /**
     * 获取当前时间的 UTC 时间
     *
     * <p>
     * 根据 {@link #clock} 字段的情况返回结果
     * </p>
     *
     * @return 当前时间的 UTC 时间
     */
    private static Instant utcNow() {
        if (clock == null) {
            return Instant.now();
        }
        return Instant.now(clock);
    }

    /**
     * 将 {@link ZoneId} 对象转换为 {@link ZoneOffset} 对象
     *
     * <p>
     * {@link ZoneOffset} 是 {@link ZoneId} 的超类, 可以直接转换, 反之需要通过计算偏移量完成转换
     * </p>
     *
     * @param zoneId {@link ZoneId} 对象
     * @return 转换后的 {@link ZoneOffset} 对象
     */
    public static ZoneOffset toZoneOffset(ZoneId zoneId) {
        // 根据当前的 UTC 时间, 计算所给的 ZoneId 对象对于 UTC 时区的偏移量, 得到 ZoneOffSet 对象
        return zoneId.getRules().getOffset(utcNow());
    }

    /**
     * 将 {@link LocalDateTime} 对象根据所给的时区转为 {@link Instant} 对象, 即 UTC 时间
     *
     * @param local  {@link LocalDateTime} 对象, 本地时间
     * @param zoneId {@code local} 参数表示的时间所在的时区
     * @return {@link Instant} 对象, 表示 UTC 时间
     */
    public static Instant toInstant(LocalDateTime local, ZoneId zoneId) {
        return toInstant(ZonedDateTime.of(local, zoneId));
    }

    /**
     * 将 {@link ZonedDateTime} 转为 {@link Instant} 对象, 即 UTC 时间
     *
     * @param zoned {@link ZonedDateTime} 对象, 带有时区信息的
     * @return {@link Instant} 对象, 即 UTC 时间
     */
    public static Instant toInstant(ZonedDateTime zoned) {
        return zoned.toInstant();
    }

    /**
     * 将 {@link OffsetDateTime} 转为 {@link Instant} 对象, 即 UTC 时间
     *
     * @param offset {@link OffsetDateTime} 对象, 带有时区信息的
     * @return {@link Instant} 对象, 即 UTC 时间
     */
    public static Instant toInstant(OffsetDateTime offset) {
        return offset.toInstant();
    }

    /**
     * 将 {@link Date} 转为 {@link Instant} 对象, 即 UTC 时间
     *
     * @param date   {@link Date} 对象
     * @param zoneId 时区对象
     * @return {@link Instant} 对象, 即 UTC 时间
     */
    public static Instant toInstant(Date date, ZoneId zoneId) {
        return toInstant(ZonedDateTime.ofInstant(date.toInstant(), zoneId));
    }

    /**
     * 将 {@link LocalDateTime} 时间对象根据所给的时区转换为 {@link ZonedDateTime} 对象
     *
     * <p>
     * 转化后, 相当于给 {@link LocalDateTime} 对象所表示的时间给予了一个时区, 即:
     *
     * <pre>
     * 2021-09-11T00:00:00.000 => 2021-09-11T00:00:00.000+08:00[Asia/Shanghai]
     * </pre>
     * </p>
     *
     * @param local  本地时间对象
     * @param zoneId 时区对象
     * @return 具备时区的时间对象
     */
    public static ZonedDateTime toZoned(LocalDateTime local, ZoneId zoneId) {
        return ZonedDateTime.of(local, zoneId);
    }

    /**
     * 将 {@link Instant} 时间对象根据所给的时区转换为 {@link ZonedDateTime} 对象
     *
     * <p>
     * 转化后, 相当于将 UTC 时间转为指定时区的时间, 即:
     *
     * <pre>
     * 2021-09-11T00:00:00.000Z => 2021-09-11T08:00:00.000+08:00[Asia/Shanghai]
     * </pre>
     * </p>
     *
     * @param instant UTC 时间对象
     * @param zoneId  时区对象
     * @return 具备时区的时间对象
     */
    public static ZonedDateTime toZoned(Instant instant, ZoneId zoneId) {
        return ZonedDateTime.ofInstant(instant, zoneId);
    }

    /**
     * 将 {@link Date} 时间对象转化为 {@link ZonedDateTime} 时间对象
     *
     * <p>
     * 该转化会根据 {@link Date} 对象内部存储的 Unix Timestamp 进行, 和 {@link Date} 对象在当前系统输出的时间无关
     * </p>
     *
     * @param date   {@link Date} 时间对象
     * @param zoneId 时区对象
     * @return 具备时区信息的实践对象
     */
    public static ZonedDateTime toZoned(Date date, ZoneId zoneId) {
        var local = LocalDateTime.ofInstant(date.toInstant(), zoneId);
        return local.atZone(zoneId);
    }

    /**
     * 将 {@link ZonedDateTime} 对象的时区变更到另一个时区
     *
     * <p>
     * 更换时区并不会改变时间的绝对值, 只是将时区进行了变更, 为了保证时间绝对值不变, 所以变更后的时间相对值会发生变化, 即:
     *
     * <pre>
     * 2021-09-11T00:00:00.000+00:00[UTC] =>
     * 2021-09-11T08:00:00.000+08:00[Asia/Shanghai]
     * </pre>
     * <p>
     * 转换前后, 时区发生了变更, 且时间增加了 8 小时, 但时间的绝对值不变
     * </p>
     *
     * @param zoned  {@link ZonedDateTime} 对象
     * @param zoneId 新时区对象
     * @return 更换时区后的 {@link ZonedDateTime} 对象
     */
    public static ZonedDateTime changeZone(ZonedDateTime zoned, ZoneId zoneId) {
        return zoned.withZoneSameInstant(zoneId);
    }

    /**
     * 将 {@link ZonedDateTime} 对象的时区替换到另一个时区
     *
     * <p>
     * 替换时区会导致时间的绝对值发生变化, 即:
     *
     * <pre>
     * 2021-09-11T00:00:00.000+00:00[UTC] =>
     * 2021-09-11T00:00:00.000+08:00[Asia/Shanghai]
     * </pre>
     * <p>
     * 转换前后, 时区发生了变更, 且时间的绝对值增加了 8 小时, 也发生了变化
     * </p>
     *
     * @param zoned  {@link ZonedDateTime} 对象
     * @param zoneId 新时区对象
     * @return 更换时区后的时间对象
     */
    public static ZonedDateTime replaceZone(ZonedDateTime zoned, ZoneId zoneId) {
        return zoned.withZoneSameLocal(zoneId);
    }

    /**
     * 获取 {@link Date} 对象记录的时区信息
     *
     * <p>
     * 尽管 {@link Date} 对象存储的是 UTC 时间戳, 但其记录的时区信息仍会影响到 格式化 和 获取时间 的结果
     * 可以通过该方法获取到 Date 对象所设置的时区信息
     * </p>
     *
     * @param date {@link Date} 对象
     */
    public static ZoneId zoneIdFromDate(Date date) {
        var cal = Calendar.getInstance();
        cal.setTime(date);
        return ZoneId.of(cal.getTimeZone().getID());
    }

    /**
     * 设置时间日期计算所依赖的计时器对象
     *
     * <p>
     * 重新设置计时器对象, 可以通过 {@link Clock#fixed(Instant, ZoneId)}
     * 设置一个固定时间以避免测试时因为时间不断变化导致无法断言
     * </p>
     */
    @VisibleForTesting
    static void setClock(Clock clock) {
        Times.clock = clock;
    }
}
