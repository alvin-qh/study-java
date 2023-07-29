# 使用 Java JNI

## 1. 生成头文件

编写具备 `native` 关键字的 Java 类, 并对其进行编译生成 `.h` 头文件, 例如:

```java
package alvin.study.misc;

public class JNIDemo {
    public native String itoa(int number);
}
```

进入该 Java 文件所在文件夹, 执行:

```bash
javac -h . JNIDemo.java
```

会在该路径下生成所需的 C++ 头文件

将该头文件内容复制到 C++ 开发环境下的头文件中, 例如 [jni_demo.h](./jni_demo.h) 文件中

## 2. 生成对应的 cpp 文件

对应该头文件, 提供对应的 `.cpp` 文件, 实现头文件中指定的函数, 例如 [jni-demo.cpp](./jni_demo.cpp) 文件

## 3. 编写 CMakeList.txt 编译文件

提供 `CMakeList.txt` 文件指定编译为动态库 (`.so`) 文件, 参考 [CMakeLists.txt](./CMakeLists.txt) 文件

## 4. 编译动态库

在当前目录下建立 `build` 文件夹和 `target` 文件夹, 进入 `build` 文件夹执行

```bash
cmake ..
make
```

将会在 `target` 文件夹下生成对应的 `.so` 文件

## 5. 将动态库打包

将生成的 `libjni_demo_lib.so` 动态库文件复制到 `src/main/resources/native` 路径下, 最终通过 Maven 或 Gradle 打包在一起

注意, 如果使用 Maven 进行打包, 且要将 `so` 这种二进制文件作为 Java 资源保存, 需要使用 `maven-surefire-plugin` 插件来确保
Maven 不会对资源内容进行转码, 即

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-resources-plugin</artifactId>
  <configuration>
    <encoding>UTF-8</encoding>
    <nonFilteredFileExtensions>
      <nonFilteredFileExtension>so</nonFilteredFileExtension>
    </nonFilteredFileExtensions>
  </configuration>
</plugin>
```

至于如何将 `so` 文件从 Java 资源中释放出来,
请参考 [alvin.study.se.runtime.LoadLibrary.java](../src/main/java/alvin/study/misc/LoadLibrary.java) 文件
