package alvin.study.se.reflect.scan;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarFile;

import alvin.study.se.reflect.scan.match.Matcher;

/**
 * 类型扫描器
 *
 * <p>
 * 扫描指定范围内的所有类, 获取符合条件的类集合
 * </p>
 */
public final class ClassScanner {
    // 类文件扩展名长度, 即 '.class', 共 6 个字符
    private static final int EXTENSION_LENGTH = 6;

    // class 文件后缀
    private static final String CLASS_FILE_EXT = ".class";

    // package-info 类名
    private static final String PACKAGE_INFO_CLASS_NAME = "package-info";

    // 指定匹配器对象
    private final Matcher<? super Class<?>> matcher;

    /**
     * 构造器, 指定扫描所用的匹配器
     *
     * @param matcher 匹配器 {@link Matcher} 对象
     */
    private ClassScanner(final Matcher<? super Class<?>> matcher) {
        this.matcher = matcher;
    }

    /**
     * 直接通过 {@link Matcher} 对象进行
     *
     * @param matcher 匹配器对象
     * @return {@link ClassScanner} 对象
     */
    public static ClassScanner matching(Matcher<? super Class<?>> matcher) {
        return new ClassScanner(matcher);
    }

    /**
     * 将 {@link URL} 对象转为路径
     *
     * <p>
     * 这一步主要是为了跨平台, 不同平台的路径分隔符不同 (例如 Linux 为 {@code /} 而 Windows 为 {@code \})
     * </p>
     *
     * @param url {@link URL} 对象, 表示资源的定位
     * @return 转换成的路径
     */
    private static String toPath(URL url) {
        // 从资源 URL 中获取路径名, 该路径
        var path = url.getPath();

        var buf = new StringBuilder();
        // 遍历路径字符, 进行跨平台处理
        for (int i = 0, length = path.length(); i < length; i++) {
            var c = path.charAt(i);
            if (c == '/') {
                // 将路径分隔符转为跨平台分隔符
                buf.append(File.separatorChar);
            } else if (c == '%' && i < length - 2) {
                // 对于编码字符, 处理为 Java UNICODE 字符 (char 字符)
                buf.append((char) Integer.parseInt(path.substring(++i, ++i + 1), 16));
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * 根据包路径名, 从当前的类加载器中获取对应的资源 URL
     *
     * @param packageDirName 包路径名
     * @return 对应的类加载器资源 URL 的迭代器
     */
    private static Enumeration<URL> findPackageDirs(String packageDirName) {
        try {
            // 获取当前线程的类加载器, 并将包路径读取为资源 URL
            return Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        } catch (IOException e) {
            throw new PackageScanFailedException("Could not read from package directory: " + packageDirName, e);
        }
    }

    /**
     * 通过资源 URL 获取 {@link JarFile} 对象
     *
     * @param packageDir Jar 文件的资源 URL
     * @return {@link JarFile} 对象
     */
    private static JarFile packageDirToJarFile(URL packageDir) {
        try {
            return ((JarURLConnection) packageDir.openConnection()).getJarFile();
        } catch (IOException e) {
            throw new PackageScanFailedException("Could not read from jar url: " + packageDir, e);
        }
    }

    /**
     * 指定要扫描的包
     *
     * <p>
     * 即指定要扫描类的范围, 只对指定包之下的类进行扫描
     * </p>
     *
     * @param packages 要扫描的包对象数组
     * @return 扫描到的类的 {@link Set} 集合
     * @throws PackageScanFailedException 在读取 {@code .class} 文件或者 {@code .jar}
     *                                    文件时发生错误
     * @throws IllegalStateException      当在加载类时发生了一些非常偶然的错误
     */
    public Set<Class<?>> in(Package... packages) {
        var packs = Arrays.stream(packages).map(Package::getName).toArray(String[]::new);
        return in(packs);
    }

    /**
     * 指定要扫描的包
     *
     * <p>
     * 即指定要扫描类的范围, 只对指定包之下的类进行扫描
     * </p>
     *
     * @param packageNames 要扫描的包名称数组
     * @return 扫描到的类的 {@link Set} 集合
     * @throws PackageScanFailedException 在读取 {@code .class} 文件或者 {@code .jar}
     *                                    文件时发生错误
     */
    public Set<Class<?>> in(String... packageNames) {
        // 存放结果的 Set 集合
        var classes = new LinkedHashSet<Class<?>>();

        // 遍历所有的包名称
        for (var packageName : packageNames) {
            // 将包名转为目录名, 目录间用 '/' 字符分隔
            var packageDirName = packageName.replace('.', '/');

            // 根据包目录名获取包资源 URLs
            var dirs = findPackageDirs(packageDirName);

            // 遍历包资源 URLs
            while (dirs.hasMoreElements()) {
                var url = dirs.nextElement();

                // 获取资源的定义
                var protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    // 如果资源是当前文件系统资源, 则从当前文件系统中查找结果
                    findClassesInDirPackage(packageName, toPath(url), true, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果资源是 '.jar' 资源, 则从 jar 文件中查找结果
                    // 获取 JarFile 对象
                    var jar = packageDirToJarFile(url);

                    // 遍历 JarFile 中的每个节点
                    var entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        var entry = entries.nextElement();
                        // 获取节点名称
                        var name = entry.getName();
                        // 去掉最前面的 '/' 字符
                        if (name.startsWith("/")) {
                            name = name.substring(1);
                        }
                        // 判断节点名以所给包名为前缀, 表示该节点在预期的包下
                        if (name.startsWith(packageDirName)) {
                            // 查找最后一个 '/' 分隔符, 之后为类名, 之前为包名 (含子包名)
                            var idx = name.lastIndexOf('/');
                            if (idx != -1) {
                                // 重写计算包名, 含子包名
                                packageName = name.substring(0, idx).replace('/', '.');
                            }

                            // 判断节点名称是否为类节点
                            if (name.endsWith(CLASS_FILE_EXT) && !entry.isDirectory()) {
                                var className = name.substring(idx + 1, name.length() - EXTENSION_LENGTH);
                                if (!PACKAGE_INFO_CLASS_NAME.equalsIgnoreCase(className)) {
                                    // 加入结果
                                    add(packageName, classes, className);
                                }
                            }
                        }
                    }
                }
            }
        }
        return classes;
    }

    /**
     * 在包路径下查找所有符合匹配规则的类
     *
     * @param packageName 要查找的包名, 表示在该包下查找类
     * @param packagePath {@code packageName} 参数对应的包路径
     * @param recursive   是否递归查找, 即是否对包下的子包进行查找
     * @param classes     存放查找结果的 {@link Set} 集合
     */
    private void findClassesInDirPackage(
            String packageName,
            String packagePath,
            boolean recursive,
            Set<Class<?>> classes) {
        // 根据路径获取相关的 File 对象
        var dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            // 如果路径不存在或不是目录, 测中断此路径处理
            return;
        }

        // 查找该路径下的所有子 FIle 对象
        var files = dir.listFiles(file -> (recursive && file.isDirectory()) || file.getName().endsWith(CLASS_FILE_EXT));
        if (files != null) {
            // 遍历所有子 File 对象
            for (var file : files) {
                if (file.isDirectory()) {
                    // 如果该 File 对象表示目录, 则递归处理该目录下的内容
                    findClassesInDirPackage(packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classes);
                } else {
                    var fileName = file.getName();
                    if (fileName.endsWith(CLASS_FILE_EXT)) {
                        // 如果文件名以 '.class' 为扩展名, 则将此类加入到结果集合中
                        var className = fileName.substring(0, fileName.length() - EXTENSION_LENGTH);
                        if (!PACKAGE_INFO_CLASS_NAME.equalsIgnoreCase(className)) {
                            add(packageName, classes, className);
                        }
                    }
                }
            }
        }
    }

    /**
     * 将找到的类加入到结果集合中
     *
     * @param packageName 类所在的包名
     * @param classes     存储结果的 {@link Set} 集合
     * @param className   要加入结果集合的类名称
     */
    private void add(String packageName, Set<Class<?>> classes, String className) {
        try {
            // 通过类名称获取 Class 对象
            var clazz = Class.forName(packageName + '.' + className);
            // 判断该类是否和所给的匹配器匹配
            if (matcher.matches(clazz)) {
                // 如果匹配, 将该类加入结果集合
                classes.add(clazz);
            }
        } catch (ClassNotFoundException | NoClassDefFoundError ignore) {
            // 忽略此异常, 或在此打印日志
        }
    }
}
