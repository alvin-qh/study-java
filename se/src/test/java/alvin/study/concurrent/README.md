# 异步调用

当前包下的若干测试方法演示了 Java 中的异步工具包的使用

## 1. 概述

- [ThreadPoolTest.java](./ThreadPoolTest.java) 文件中演示了如何使用线程池执行异步操作, 包括:
  - 通过 `ArrayBlockingQueue` 类型构建
  - 通过 `SynchronousQueue` 队列
- [TimerScheduleTest.java](./TimerScheduleTest.java) 文件中演示了
