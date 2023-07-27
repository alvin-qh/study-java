# 定义 CMake 最小版本号
cmake_minimum_required(VERSION 3.10)

# 定义项目信息
project(jni_demo  # 项目名称
    LANGUAGES CXX # 所用的语言, CXX 表示 C++
    VERSION 1.0   # 项目版本号
    DESCRIPTION "CXX library for JNI demo" # 项目描述
)

# 设置编译变量
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# 设置 JNI 路径
if(CMAKE_SYSTEM_NAME MATCHES "Linux")
    set(JNI_H
        /usr/lib/jvm/java-17-openjdk-amd64/include
        /usr/lib/jvm/java-17-openjdk-amd64/include/linux)
elseif(CMAKE_SYSTEM_NAME MATCHES "Darwin")
    set(JNI_H
        /usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home/include
        /usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home/include/darwin)
endif()

message("The JNI header path is: ${JNI_H}")

# 包含 JDK 头文件
include_directories(
    ${JNI_H}
)

# 设置代码集
set(jni_demo_src
    jni_demo.cpp
)

# 设置编译标识
set(CMAKE_CXX_FLAGS
    "${CMAKE_CXX_FLAGS} -pthread"
)

# 设置动态库编译, 并设置要编译到动态库中的源码集
add_library(jni_demo_lib SHARED
    ${jni_demo_src}
)

# 设置编译目标要包含的头文件路径
target_include_directories(jni_demo_lib PRIVATE .)

# 设置编译目标属性
set_target_properties(jni_demo_lib PROPERTIES
    OUTPUT_NAME "jni_demo_lib"  # 库文件名
    VERSION 1.0   # 库版本
    SOVERSION 0
)

# 设置库文件输出路径
set(LIBRARY_OUTPUT_PATH
    ../target
)
