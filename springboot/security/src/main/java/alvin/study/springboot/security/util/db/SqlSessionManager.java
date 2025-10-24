package alvin.study.springboot.security.util.db;

import java.sql.Connection;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * 用于 mybatis 的 {@link SqlSession} 对象管理器
 *
 * <p>
 * mybatis 通过 {@link SqlSession} 管理数据库连接, 通过
 * {@link SqlSessionUtils#getSqlSession(org.apache.ibatis.session.SqlSessionFactory)
 * SqlSessionUtils.getSqlSession(SqlSessionFactory)} 方法获取 {@link SqlSession} 对象,
 * 使用完毕后需要通过
 * {@link SqlSessionUtils#closeSqlSession(SqlSession, org.apache.ibatis.session.SqlSessionFactory)
 * SqlSessionUtils.closeSqlSession(SqlSession, SqlSessionFactory)} 方法关闭获取的
 * {@link SqlSession} 对象
 * </p>
 *
 * <p>
 * 上述方法中的参数可以通过 {@link SqlSessionTemplate#getSqlSessionFactory()},
 * {@link SqlSessionTemplate#getExecutorType()} 和
 * {@link SqlSessionTemplate#getPersistenceExceptionTranslator()} 这三个方法获得
 * </p>
 *
 * <p>
 * {@link SqlSessionTemplate#getConnection()} 获取的数据库连接无法直接使用,
 * {@link SqlSessionTemplate} 对象会自动关闭获取的 {@link Connection} 对象, 所以需要通过
 * {@link SqlSessionUtils} 对象获得
 * </p>
 */
@Component
@RequiredArgsConstructor
public class SqlSessionManager {
    /**
     * 注入 {@link SqlSessionTemplate} 对象, 该对象用于管理 {@link SqlSession} 对象, 也可以根据标识来执行
     * mybatis 定义的 SQL 语句
     */
    @Autowired
    private final SqlSessionTemplate sqlSessionTemplate;

    /**
     * 获取 {@link SqlSession} 对象
     *
     * @return {@link SqlSessionHolder} 对象, 用于管理 {@link SqlSession} 对象
     */
    public SqlSessionHolder build() {
        // 获取 SqlSession 对象
        var sqlSession = SqlSessionUtils.getSqlSession(
            sqlSessionTemplate.getSqlSessionFactory(),
            sqlSessionTemplate.getExecutorType(),
            sqlSessionTemplate.getPersistenceExceptionTranslator());

        return new SqlSessionHolder(sqlSession);
    }

    /**
     * 持有 {@link SqlSession} 对象的类型
     *
     * <p>
     * 该类型实现了 {@link AutoCloseable} 类型, 即当当前对象 {@code close} 的时候来释放资源
     * </p>
     */
    @RequiredArgsConstructor
    public class SqlSessionHolder implements AutoCloseable {
        // 记录 SqlSession 对象
        private final SqlSession sqlSession;

        /**
         * 自动关闭
         *
         * <p>
         * 当当前对象使用完毕后, 自动调用该方法关闭 {@link SqlSession} 对象
         * </p>
         *
         * <p>
         * 具体关闭 {@link SqlSession} 是通过
         * {@link SqlSessionUtils#closeSqlSession(SqlSession, org.apache.ibatis.session.SqlSessionFactory)
         * SqlSessionUtils.closeSqlSession(SqlSession, SqlSessionFactory)} 进行
         * </p>
         */
        @Override
        public void close() {
            SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionTemplate.getSqlSessionFactory());
        }

        /**
         * 获取 {@link SqlSession} 对象
         *
         * @return {@link SqlSession} 对象
         */
        public SqlSession getSqlSession() { return sqlSession; }

        /**
         * 获取 {@link Connection} 对象
         *
         * <p>
         * 通过 {@link SqlSession#getConnection()} 方法获取数据库连接对象
         * </p>
         *
         * @return {@link Connection} 对象
         */
        public Connection getConnection() { return sqlSession.getConnection(); }
    }
}
