package alvin.study.future.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 测试异步任务的实体类型
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class User {
    private final long id;
    private final String name;
}
