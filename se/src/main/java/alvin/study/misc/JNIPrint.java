package alvin.study.misc;

public class JNIPrint {
    static {
        System.loadLibrary("native");
    }

    public native void print(String text);
}
