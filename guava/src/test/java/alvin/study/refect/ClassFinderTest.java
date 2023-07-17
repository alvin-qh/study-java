package alvin.study.refect;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link ClassFinder} 类型
 */
class ClassFinderTest {
    /**
     * 测试 {@link ClassFinder#inPackage(String)} 方法
     */
    @Test
    void inPackage_shouldFindClassInPackageByPackageName() throws IOException {
        // 查找 alvin.study.future 包下所有类
        var classes = ClassFinder.inPackage("alvin.study.future");

        // 确认结果正确
        then(classes)
            .isNotEmpty()
            .hasSize(20)
            .contains(alvin.study.future.Counter.class, alvin.study.future.model.User.class);
    }
}
