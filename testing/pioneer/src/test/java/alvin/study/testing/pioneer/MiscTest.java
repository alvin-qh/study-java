package alvin.study.testing.pioneer;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.ServiceLoader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ExpectedToFail;
import org.junitpioneer.jupiter.Issue;
import org.junitpioneer.jupiter.IssueProcessor;
import org.junitpioneer.jupiter.IssueTestSuite;
import org.junitpioneer.jupiter.ReportEntry;
import org.junitpioneer.jupiter.ReportEntry.PublishCondition;
import org.junitpioneer.jupiter.Stopwatch;

/**
 * 定义一个简单的 {@link IssueProcessor} 接口实现类, 用于显示本次测试相关 {@link Issue @Issue} 注解标识的 issues 代码,
 * 参考: {@link MiscTest#issue_shouldReportIssueCode()} 方法
 */
class SimpleIssueProcessor implements IssueProcessor {
    /**
     * 处理 {@link Issue @Issue} 注解的测试方法集合
     *
     * @param issueTestSuites {@link Issue @Issue} 注解的测试方法集合
     */
    @Override
    public void processTestResults(List<IssueTestSuite> issueTestSuites) {
        // 显示所有测试的 issueId 值
        for (var issue : issueTestSuites) {
            System.out.println(issue.issueId());
        }
    }
}

/**
 * 测试 Pioneer 库的其它功能
 */
class MiscTest {
    @BeforeAll
    static void beforeAll() {
        ServiceLoader.load(SimpleIssueProcessor.class);
    }

    /**
     * 报告测试方法的性能指标
     *
     * <p>
     * {@link Stopwatch @Stopwatch} 用于在测试报告中列举测试花费的时间, 单位为 {@code ms}
     * </p>
     */
    @Test
    @Stopwatch
    void measuring_shouldRecordMeasuringRunTime() throws Exception {
        Thread.sleep(1000);
    }

    /**
     * 通过 {@link ReportEntry @ReportEntry} 注解可以定义本测试方法在测试报告中的显示内容， 包括 {@code key} 表示的测试方法索引
     * 和 {@code value} 表示的测试结果说明
     *
     * <p>
     * JUnit 中可以通过注入 {@link org.junit.jupiter.api.TestReporter TestReporter} 类型参数, 并通过
     * {@link org.junit.jupiter.api.TestReporter#publishEntry(String, String) TestReporter.publishEntry(String, String)}
     * 方法完成此工作, {@link ReportEntry @ReportEntry} 注解只是简化了此工作, 将报告代码和测试代码分离
     * </p>
     *
     * <p>
     * {@code }
     * </p>
     */
    @Test
    @ReportEntry(key = "test-reporting", value = "The test reporting output demo", when = PublishCondition.ALWAYS)
    void reporting_shouldReportTestUseCertainKeyAndValue() {}

    /**
     * 通过 {@link Issue @Issue} 注解在测试报告中包含一个 issues 代码, 以表示此测试方法是针对某个具体的缺陷
     *
     * <p>
     * 需要通过实现一个监听器类型来监听所有注解 {@link Issue @Issue} 的测试方法, 参考: {@link SimpleIssueProcessor} 类型
     * </p>
     */
    @Test
    @Issue("ERR-1001")
    void issue_shouldReportIssueCode() {}

    /**
     * {@link ExpectedToFail @ExpectedToFail} 注解表示该测试方法有失败的预期, 即使失败了也不要在结果中显示
     *
     * <p>
     * 有时候为了复现某些错误而编写必然失败的异常, {@link ExpectedToFail @ExpectedToFail} 注解可以避免这类测试影响测试结果
     * </p>
     */
    @Test
    @ExpectedToFail("implementation bugs in some functions")
    void expectedToFail_shouldNotFailed() {
        fail();
    }
}
