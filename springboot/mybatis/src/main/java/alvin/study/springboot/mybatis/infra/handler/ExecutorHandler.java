package alvin.study.springboot.mybatis.infra.handler;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import lombok.extern.slf4j.Slf4j;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * Mybatis 的 {@link Executor} 类型的拦截器
 *
 * <p>
 * Mybatis 用于处理查询的四个类型包括: {@link Executor} 类型,
 * {@link org.apache.ibatis.executor.statement.StatementHandler
 * StatementHandler} 类型,
 * {@link org.apache.ibatis.executor.parameter.ParameterHandler
 * ParameterHandler} 类型 以及 {@link ResultHandler} 类型
 * </p>
 *
 * <p>
 * Mybatis 为上述四个类型通过 AOP 设置了拦截器, 可以通过 {@link Intercepts @Intercepts}
 * 注解设置要拦截的方法的签名
 * <ul>
 * <li>
 * {@link Signature @Signature} 注解用来定义要拦截的目标方法的签名
 * <ul>
 * <li>
 * {@code type} 属性用来指定要拦截的方法所在的类型
 * </li>
 * <li>
 * {@code method} 指定要拦截的方法名称
 * </li>
 * <li>
 * {@code args} 指定要拦截的方法的参数列表
 * </li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 本例中拦截了
 * {@link Executor#query(MappedStatement, Object, RowBounds, ResultHandler, CacheKey, BoundSql)}
 * 和 {@link Executor#query(MappedStatement, Object, RowBounds, ResultHandler)}
 * 方法
 * </p>
 *
 * <p>
 * 通过 {@code jSQLParser} 框架, 可以将 {@link BoundSql} 中的 SQL 语句进行修改, 再通过
 * </p>
 *
 * <p>
 * 该拦截器会影响到 Mybatis 分页插件的正确执行, 而拦截器本身并未有实际功能, 所以取消了
 * {@link org.springframework.stereotype.Component @Component}
 * 注解, 令该拦截器不生效
 * </p>
 */
@Slf4j
// @Component
@Intercepts({
    @Signature(type = Executor.class,
               method = "query",
               args = {
                   MappedStatement.class,
                   Object.class,
                   RowBounds.class,
                   ResultHandler.class,
                   CacheKey.class,
                   BoundSql.class }),
    @Signature(type = Executor.class,
               method = "query",
               args = {
                   MappedStatement.class,
                   Object.class,
                   RowBounds.class,
                   ResultHandler.class }),
})
public class ExecutorHandler implements Interceptor {
    /**
     * 执行拦截方法
     */
    @Override
    public Object intercept(Invocation invocation) throws Exception {
        // 获取调用方法的对象
        var executor = (Executor) invocation.getTarget();
        log.info("[ExecutorHandler] The executor is: {}", executor);

        // 获取被拦截的方法对象
        var method = invocation.getMethod();
        log.info("[ExecutorHandler] The method is: {}({})", method.getName(),
            formatParameterType(method.getParameterTypes()));

        // 获取调用方法的参数
        var args = invocation.getArgs();

        // 获取拦截方法的参数列表
        var mappedStatement = (MappedStatement) args[0];
        var parameter = args[1];
        var rowBounds = (RowBounds) args[2];
        var resultHandler = (ResultHandler<?>) args[3];

        CacheKey cacheKey;
        BoundSql boundSql;
        if (args.length > 4) {
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        } else {
            boundSql = mappedStatement.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
        }
        log.info("[ExecutorHandler] The parameters are: {}, {}, {}, {}, {}, {}",
            mappedStatement, parameter, rowBounds, resultHandler, cacheKey, boundSql);

        // 获取此次执行的 SQL 语句
        var sql = boundSql.getSql();
        log.info("[ExecutorHandler] Bounded SQL is: {}", sql);

        // 解析 SQL 语句并进行修改
        var statement = CCJSqlParserUtil.parse(sql);
        var selectBody = ((PlainSelect) statement).getSelectItems();
        log.info("[ExecutorHandler] Select Body is: {}", selectBody);

        // 用新的 SQL 语句产生新的 BoundSql 对象
        var newBoundSql = new BoundSql(
            mappedStatement.getConfiguration(),
            statement.toString(),
            boundSql.getParameterMappings(),
            boundSql.getParameterObject());

        // 将新的 BoundSql 对象设置到参数列表中
        if (args.length > 4) {
            args[5] = newBoundSql;
        } else {
            // 将额外的参数进行设置
            for (var mapping : boundSql.getParameterMappings()) {
                var prop = mapping.getProperty();
                if (boundSql.hasAdditionalParameter(prop)) {
                    newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
                }
            }

            // 将新的 BoundSql 对象设置到新的 MappedStatement 对象中
            var newMappedStatement = newMappedStatement(mappedStatement, _ -> newBoundSql);

            // 将新的 MappedStatement 对象设置到参数列表中
            args[0] = newMappedStatement;
        }

        // 继续执行原方法
        return invocation.proceed();
    }

    /**
     * 根据现有的 {@link MappedStatement} 对象构建一个新的 {@link MappedStatement} 对象
     *
     * @param ms           现有的 {@link MappedStatement} 对象
     * @param newSqlSource 要修改的 SQL 对象
     * @return 新的 {@link MappedStatement} 对象
     */
    private MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        var builder = new MappedStatement.Builder(
            ms.getConfiguration(),
            ms.getId(),
            newSqlSource,
            ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());

        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(ms.getKeyProperties()[0]);
        }

        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    /**
     * 获取参数列表类型, 转为字符串
     *
     * @param parameterTypes 参数类型列表
     * @return 转为的字符串
     */
    private String formatParameterType(Class<?>[] parameterTypes) {
        var b = new StringBuilder();
        for (var p : parameterTypes) {
            if (p != parameterTypes[0]) {
                b.append(", ");
            }
            b.append(p.getSimpleName());
        }
        return b.toString();
    }

    /**
     * 将当前拦截器包装为插件返回
     */
    @Override
    public Object plugin(Object target) {
        // 只对符合当前类型 (需要拦截) 的情况进行包装
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }
}
