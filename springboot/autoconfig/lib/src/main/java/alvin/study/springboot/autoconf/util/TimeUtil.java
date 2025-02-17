package alvin.study.springboot.autoconf.util;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtil {
    private static Clock clock = Clock.systemUTC();

    private final ZoneId zoneId;

    public TimeUtil(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public TimeUtil(String zoneName) {
        this(ZoneId.of(zoneName));
    }

    public static Clock changeClock(Clock clock) {
        var oldClock = TimeUtil.clock;
        TimeUtil.clock = clock;
        return oldClock;
    }

    public ZoneId getZoneId() { return zoneId; }

    public LocalDateTime localNow() {
        return LocalDateTime.ofInstant(utcNow(), zoneId);
    }

    public Instant utcNow() {
        return Instant.now(TimeUtil.clock);
    }
}
