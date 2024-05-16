package alvin.study.springboot.aop.domain.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 测试被拦截方法所用的参数对象类型
 */
@Data
@RequiredArgsConstructor
public class Worker {
    private final String name;
    private final String title;
}
