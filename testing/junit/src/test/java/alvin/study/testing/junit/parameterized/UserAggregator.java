package alvin.study.testing.junit.parameterized;

import alvin.study.testing.testcase.model.User;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

/**
 * 将 csv 数据聚合成 {@link User} 对象的数据聚合类型
 */
public class UserAggregator implements ArgumentsAggregator {
    @Override
    public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context)
            throws ArgumentsAggregationException {
        // 通过 ArgumentsAccessor 对象获取 csv 数据的前两列, 形成 User 对象
        return new User(accessor.getInteger(0), accessor.getString(1));
    }
}
