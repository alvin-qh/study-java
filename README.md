# Study Java Project

## 1. 工具链

- JDK

  ```bash
  sudo apt install openjdk-17-jdk-headless
  ```

- Gradle

  - 从 <https://gradle.org/releases/> 下载安装包
  - 解压到 `/opt/gradle/gradle-x.x` 目录下
  - 建立一个表示当前版本的软链接:

    ```bash
    ln -s -d /opt/gradle/current /opt/gradle/gradle-x.x
    ```

  - 在 Shell 启动脚本中增加

    ```bash
    export GRADLE_HOME="/opt/gradle/current"
    export PATH="$GRADLE_HOME/bin:$PATH"
    ```

- Maven

  - 从 <https://maven.apache.org/download.cgi> 下载安装包
  - 解压到 `/opt/maven/apache-maven-x.x.x` 目录下
  - 建立一个表示当前版本的软链接

    ```bash
    sudo ln -s -d /opt/maven/current /opt/maven/apache-maven-x.x.x
    ```

  - 在 Shell 启动脚本中增加

    ```bash
    export MAVEN_HOME="/opt/maven/current"
    export PATH="$MAVEN_HOME/bin:$PATH"
    ```

## 2. VSCode 配置

### 2.1. 编辑器配置

```json
"[java]": {
  "editor.defaultFormatter": "redhat.java",
  "editor.rulers": [
    150
  ],
  "editor.codeActionsOnSave": {
    "source.organizeImports": "always",
    "source.fixAll": "always"
  }
}
```

### 2.2. 编译器配置

设置静态导入范围

```json
"java.completion.favoriteStaticMembers": [
  "org.junit.Assert.*",
  "org.junit.Assume.*",
  "org.junit.jupiter.api.Assertions.*",
  "org.junit.jupiter.api.Assumptions.*",
  "org.junit.jupiter.api.DynamicContainer.*",
  "org.junit.jupiter.api.DynamicTest.*",
  "org.mockito.Mockito.*",
  "org.mockito.ArgumentMatchers.*",
  "org.mockito.Answers.*",
  "org.assertj.core.api.Assertions.*",
  "org.assertj.core.api.BDDAssertions.*",
  "org.awaitility.Awaitility.*",
  "org.hamcrest.Matchers.*"
],
```

设置默认 null 检查注解

```json
"java.compile.nullAnalysis.nonnull": [
  "org.jetbrains.annotations.NotNull"
],
"java.compile.nullAnalysis.nullable": [
  "org.jetbrains.annotations.Nullable"
],
```

### 2.3. 格式化和代码检查配置

设置格式化配置文件路径

```json
"java.format.settings.profile": "Eclipse",
"java.format.settings.url": "eclipse-formatter.xml",
```

设置代码检查配置文件路径

```json
"java.implementationsCodeLens.enabled": true,
"java.referencesCodeLens.enabled": true,
"java.settings.url": ".settings/org.eclipse.jdt.core.prefs",
```

### 2.4. 其他配置

排除无效的导入路径

```json
"java.import.exclusions": [
  "**/.gradle/**",
  "**/.history/**",
  "**/.metadata/**",
  "**/.vscode/**",
  "**/archetype-resources/**",
  "**/gradle/**",
  "**/META-INF/maven/**",
  "**/node_modules/**"
],
```

代码生成配置

```json
"java.codeGeneration.hashCodeEquals.useInstanceof": true,
"java.codeGeneration.hashCodeEquals.useJava7Objects": true,
```

源码下载配置

```json
"java.maven.downloadSources": true,
```

### 2.5. 远程配置

以 WSL 远程开发为例

```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-17",
      "path": "/usr/lib/jvm/java-17-openjdk-amd64",
      "sources": "/usr/lib/jvm/java-17-openjdk-amd64/lib/src.zip",
      "javadoc": "https://docs.oracle.com/en/java/javase/17/docs/api/",
      "default": true
    }
  ],
  "java.jdt.ls.java.home": "/usr/lib/jvm/java-17-openjdk-amd64"
}
```

## 附录. 使用 jEnv 管理 JDK

### 1. 安装

#### 1.1. Linux / OS X

```bash
git clone https://github.com/jenv/jenv.git ~/.jenv
```

对于 macOS 也可以通过 Homebrew 进行安装

```bash
brew install jenv
```

#### 1.2. 配置

Bash

```bash
echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.bash_profile
echo 'eval "$(jenv init -)"' >> ~/.bash_profile
```

Zsh

```bash
echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.zshrc
echo 'eval "$(jenv init -)"' >> ~/.zshrc
```

### 2. 使用

添加已安装的 JDK

``` bash
jenv add /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home
  oracle64-1.6.0.39 added

jenv add /Library/Java/JavaVirtualMachines/jdk17011.jdk/Contents/Home
  oracle64-1.7.0.11 added
```

列出已添加的 JDK

```bash
jenv versions
  system
  oracle64-1.6.0.39
* oracle64-1.7.0.11 (set by /Users/xxx/.jenv/version)
```

配置全局 JDK 版本

```bash
jenv global oracle64-1.6.0.39
```

配置当前目录 JDK  版本

```bash
jenv local oracle64-1.6.0.39
```

配置当前 Shell JDK 版本

```bash
jenv shell oracle64-1.6.0.39
```

### 3. Windows 上使用

在 [此处](https://github.com/FelixSelter/JEnv-for-Windows) 下载 jEnv 的 Windows 版本, 执行其中的 `bat` 文件即可
