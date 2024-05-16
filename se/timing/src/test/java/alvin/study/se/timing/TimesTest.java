package alvin.study.se.timing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link Times} 类型, 进行各类时间计算
 */
class TimesTest {
    /**
     * 在每次测试前执行
     */
    @BeforeEach
    void beforeEach() {
        // 设置 Times 中的 clock 字段为一个固定时间
        // 之后获取当前时间均会返回该定义的时间
        Times.setClock(Clock.fixed(Instant.parse("2021-09-11T08:00:00Z"), ZoneOffset.UTC));
    }

    /**
     * 在每次测试后执行
     */
    @AfterEach
    void afterEach() {
        // 恢复 Times 中的 clock 字段值
        Times.setClock(Clock.systemDefaultZone());
    }

    /**
     * 测试 {@link Times#toZoneOffset(ZoneId)} 方法
     */
    @Test
    void toZoneOffset_shouldConvertZoneIdToZoneOffset() {
        // 定义一个东八区的 ZoneId 对象
        var zoneId = ZoneId.of("Asia/Shanghai");
        // 将 ZoneId 对象转为 ZoneOffset 对象
        var zoneOffset = Times.toZoneOffset(zoneId);

        // 确认转换结果表示一个 东八区 的时区
        then(zoneOffset.getTotalSeconds()).isEqualTo(3600 * 8);
    }

    /**
     * 测试 {@link Times#toInstant(LocalDateTime, ZoneId)} 方法
     */
    @Test
    void toInstant_shouldConvertLocalDateTimeToInstant() {
        // 定义一个本地时间
        var local = LocalDateTime.parse("2021-09-11T08:00:00.000");

        // 将本地时间以 UTC 时区转为 UTC 时间
        var instant = Times.toInstant(local, ZoneOffset.UTC);
        // 确认转换前后时间一致
        then(instant).hasToString("2021-09-11T08:00:00Z");

        // 将本地时间以东八区时区转为 UTC 时间
        instant = Times.toInstant(local, ZoneId.of("Asia/Shanghai"));
        // 确认转换前后时间相差 8 小时
        then(instant).hasToString("2021-09-11T00:00:00Z");
    }

    /**
     * 测试 {@link Times#toInstant(ZonedDateTime)} 方法
     */
    @Test
    void toInstant_shouldConvertZonedDateTimeToInstant() {
        // 定义一个东八区时间
        var zoned = ZonedDateTime.parse("2021-09-11T12:00:00+08:00[Asia/Shanghai]");

        // 将东八区时区转为 UTC 时间
        var instant = Times.toInstant(zoned);
        // 确认转换前后时间相差 8 小时
        then(instant).hasToString("2021-09-11T04:00:00Z");
    }

    /**
     * 测试 {@link Times#toInstant(OffsetDateTime)} 方法
     */
    @Test
    void toInstant_shouldConvertOffsetDateTimeToInstant() {
        // 定义一个东八区时间
        var zoned = OffsetDateTime.parse("2021-09-11T12:00:00+08:00");

        // 将东八区时区转为 UTC 时间
        var instant = Times.toInstant(zoned);
        // 确认转换前后时间相差 8 小时
        then(instant).hasToString("2021-09-11T04:00:00Z");
    }

    /**
     * 测试 {@link Times#toInstant(java.util.Date, ZoneId) Times.toInstant(Date,
     * ZoneId)} 方法
     */
    @Test
    void toInstant_shouldConvertDateToInstant() {
        // 实例化一个日历对象
        var cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        // 注意, 月份必须从 0 开始, 0 表示 1 月份, 所以 8 表示 9 月
        cal.set(2021, Calendar.SEPTEMBER, 11, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // 将 Date 对象转为 UTC 时间
        var instant = Times.toInstant(cal.getTime(), ZoneId.of("Asia/Shanghai"));
        // 确认转换前后时间相差 8 小时
        then(instant).hasToString("2021-09-11T00:00:00Z");
    }

    /**
     * 测试 {@link Times#toZoned(LocalDateTime, ZoneId)} 方法
     */
    @Test
    void toZoned_shouldConvertLocalDateTimeToZoneDateTime() {
        // 定义一个 东八区 时区
        var zoneId = ZoneId.of("Asia/Shanghai");
        // 定义一个本地时间
        var local = LocalDateTime.parse("2021-09-11T12:00:00.000");

        // 将本地时间以东八区时区转为时区时间
        var zoned = Times.toZoned(local, zoneId);
        // 确认转换结果带有时区信息
        then(zoned).hasToString("2021-09-11T12:00+08:00[Asia/Shanghai]");
    }

    /**
     * 测试 {@link Times#toZoned(Instant, ZoneId)} 方法
     */
    @Test
    void toZoned_shouldConvertInstantToZonedDateTime() {
        // 定义一个 东八区 时区
        var zoneId = ZoneId.of("Asia/Shanghai");
        // 定义一个 UTC 时间
        var instant = Instant.parse("2021-09-11T00:00:00Z");

        // 将 UTC 时间以东八区时区转为时区时间
        var zoned = Times.toZoned(instant, zoneId);
        // 确认转换结果带有时区信息
        then(zoned).hasToString("2021-09-11T08:00+08:00[Asia/Shanghai]");
    }

    /**
     * 测试 {@link Times#toZoned(java.util.Date, ZoneId) Times.toZoned(Date, ZoneId)}
     * 方法
     */
    @Test
    void toZoned_shouldConvertDateToZoneDateTime() {
        // 实例化一个日历对象
        var cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        // 注意, 月份必须从 0 开始, 0 表示 1 月份, 所以 8 表示 9 月
        cal.set(2021, Calendar.SEPTEMBER, 11, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // 将 Date 时间以东八区时区转为时区时间
        var zoned = Times.toZoned(cal.getTime(), ZoneId.of("Asia/Shanghai"));
        then(zoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).isEqualTo("2021-09-11T08:00:00+08:00");
    }

    /**
     * 测试 {@link Times#changeZone(ZonedDateTime, ZoneId)} 方法
     */
    @Test
    void changeZone_shouldChangeZoneIdToOther() {
        // 定义一个 UTC 时区的时间对象
        var zonedUtc = ZonedDateTime.parse("2021-09-11T00:00:00Z");

        // 将 UTC 时区时间对象的时区转为东八区时区
        var zonedCts = Times.changeZone(zonedUtc, ZoneId.of("Asia/Shanghai"));
        // 确认时间的时区发生了变化
        then(zonedCts).hasToString("2021-09-11T08:00+08:00[Asia/Shanghai]");
    }

    /**
     * 测试 {@link Times#replaceZone(ZonedDateTime, ZoneId)} 方法
     */
    @Test
    void replaceZone_shouldReplaceZoneIdToOther() {
        // 定义一个 UTC 时区的时间对象
        var zonedUtc = ZonedDateTime.parse("2021-09-11T00:00:00Z");

        // 更换时区属性
        var zonedCts = Times.replaceZone(zonedUtc, ZoneId.of("Asia/Shanghai"));
        // 确认时区发生了变更
        then(zonedCts).hasToString("2021-09-11T00:00+08:00[Asia/Shanghai]");
    }

    /**
     * 测试 {@link Times#zoneIdFromDate(java.util.Date) Times.zoneIdFromDate(Date)} 方法
     */
    @Test
    void zoneIdFromDate_shouldGetZoneIdFromData() {
        // 实例化一个日历对象
        var cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        // 注意, 月份必须从 0 开始, 0 表示 1 月份, 所以 8 表示 9 月
        cal.set(2021, Calendar.SEPTEMBER, 11, 0, 0, 0); // month start from 0 in Date
        cal.set(Calendar.MILLISECOND, 0);

        // 从 Date 对象获取时区信息
        var zoned = Times.zoneIdFromDate(cal.getTime());
        // 确认时区信息正确
        then(zoned.getId()).isEqualTo("Asia/Shanghai");
    }
}
