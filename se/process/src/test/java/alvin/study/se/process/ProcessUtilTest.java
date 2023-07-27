package alvin.study.se.process;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;

/**
 * 测试 {@link ProcessUtil} 进程操作工具类
 */
class ProcessUtilTest {
    /**
     * 测试启动一个进程
     *
     * <p>
     * 通过 {@link ProcessUtil#exec(String...)} 方法启动一个进程, 之后通过
     * {@link ProcessUtil#fetchOutput(Process, long)} 方法获取进程的标准输出内容
     * </p>
     */
    @Test
    @EnabledOnOs({ OS.MAC, OS.LINUX })
    void exec_shouldStartProcess() throws Exception {
        // 启动进程
        var process = ProcessUtil.exec("echo", "Hello World");

        // 获取进程执行的标准输出内容
        var output = ProcessUtil.fetchOutput(process, 1000);
        // 确认标准输出内容
        then(output).isEqualTo("Hello World\n");
    }

    /**
     * 测试 {@link ProcessUtil#kill(long, boolean)} 终止指定进程
     *
     * <p>
     * 对于 macOS 系统, 可能需要额外安装 {@code watch} 软件包, 即
     *
     * <pre>
     * brew install watch
     * </pre>
     * </p>
     */
    @Test
    @EnabledOnOs({ OS.MAC, OS.LINUX })
    void kill_shouldTerminalProcess() throws Exception {
        // 启动一个持续执行的进程
        var process = ProcessUtil.exec("watch", "-n 1", "ps");

        // 根据进程 pid 查找进程信息
        var mayInfo = ProcessUtil.process(process.pid());
        then(mayInfo).isPresent();

        // 确认进程信息中包含正确的命令行参数
        // 在 Linux 系统下, 命令行参数为: /usr/bin/watch -n 1 ps
        then(mayInfo.get().getCommandLine()).contains("watch -n 1 ps");

        // 终止进程
        var killed = ProcessUtil.kill(mayInfo.get().getPid(), true);
        then(killed).isTrue();

        // 再次根据进程 id 查询进程, 确认无法查询到
        await().atMost(1, TimeUnit.SECONDS)
            .until(() -> ProcessUtil.process(process.pid()), Optional::isEmpty);
    }

    /**
     * 测试 {@link ProcessUtil#allProcesses(java.util.function.Predicate)
     * ProcessUtil.allProcesses(Predicate)} 方法, 获取符合条件的所有进程信息对象
     */
    @Test
    @EnabledOnOs({ OS.MAC, OS.LINUX })
    void allProcesses_shouldListAllProcesses() throws Exception {
        // 启动一个持续执行的进程
        var process = ProcessUtil.exec("watch", "-n 1", "ps");

        // 获取符合条件的进程集合
        var infos = ProcessUtil.allProcesses(pi -> pi.getCommandLine().endsWith("watch -n 1 ps"));
        // 确认找到进程信息
        then(infos).hasSize(1);
        // 确认查找结果符合预期
        then(infos.get(0).getPid()).isEqualTo(process.pid());
        then(infos.get(0).getCommandLine()).endsWith("watch -n 1 ps");

        // 终止进程
        ProcessUtil.kill(process.pid(), true);
    }

    /**
     * 测试 {@link ProcessUtil#children(long, java.util.function.Predicate)
     * ProcessUtil.children(long, Predicate)} 方法, 获取符合条件的子进程信息对象
     */
    @Test
    void children_shouldGetChildProcesses() throws Exception {
        // 启动一个持续执行的进程, 该进程为当前 java 进程的子进程
        var process = ProcessUtil.exec("watch", "-n 1", "ps");

        // 获取当前进程信息
        var info = ProcessUtil.current();
        then(info.getCommand()).endsWith("/bin/java");

        // 获取当前进程的子进程
        var children = ProcessUtil.children(info.getPid(), ProcessUtil.ProcessInfo::isAlive);
        // 确认子进程已被获取
        then(children).hasSize(1);
        // 确认获取的子进程符合预期
        then(children.get(0).getCommandLine()).endsWith("watch -n 1 ps");

        // 终止子进程
        ProcessUtil.kill(process.pid(), true);
    }
}
