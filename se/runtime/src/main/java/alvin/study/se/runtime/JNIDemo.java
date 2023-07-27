package alvin.study.se.runtime;

import java.io.IOException;

/**
 * 演示如何通过 JNI 加载 C++ 库
 */
public class JNIDemo {
    // 加载 libjni_demo_lib.so 动态库
    static {
        try {
            var os = System.getProperty("os.name");
            if (os.contains("Linux")) {
                LoadLibrary.load("libjni_demo_lib.so");
            } else if (os.contains("Mac")) {
                LoadLibrary.load("libjni_demo_lib.dylib");
            } else {
                throw new UnsupportedOperationException("Invalid OS");
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * 本地方法 (即由 C++ 动态库提供的方法)
     *
     * @param number 要转换的数字
     * @return 字符串转换为的字符串
     */
    public native String itoa(int number);
}
