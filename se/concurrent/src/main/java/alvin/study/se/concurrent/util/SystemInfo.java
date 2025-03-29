package alvin.study.se.concurrent.util;

/**
 * 系统信息工具类
 */
public final class SystemInfo {
    private SystemInfo() {}

    /**
     * 获取 CPU 数量
     *
     * @return CPU 数量
     */
    public static int cpuCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 获取内存总量
     *
     * @return 内存总量
     */
    public static long totalMemory() {
        return Runtime.getRuntime().totalMemory();
    }
}
