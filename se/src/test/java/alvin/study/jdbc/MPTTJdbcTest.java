package alvin.study.jdbc;

import static org.assertj.core.api.BDDAssertions.then;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.zaxxer.hikari.HikariDataSource;

import alvin.study.jdbc.datasource.ConnectionHolder;
import alvin.study.jdbc.datasource.DataSourceBuilder;
import alvin.study.jdbc.flyway.Migration;
import alvin.study.jdbc.mptt.model.MPTT;
import alvin.study.jdbc.mptt.repository.MPTTRepository;

class MPTTJdbcTest {
    private static DataSource dataSource;

    private ConnectionHolder connectionHolder;

    private MPTTRepository repository = new MPTTRepository();

    @BeforeAll
    static void beforeAll() {
        dataSource = DataSourceBuilder.newBuilder()
                .url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL;NON_KEYWORDS=USER")
                .username("dev")
                .password("password")
                .build();

        new Migration(dataSource).migrate();
    }

    @AfterAll
    static void afterAll() {
        if (dataSource instanceof HikariDataSource ds) {
            if (!ds.isClosed()) {
                ds.close();
            }
        }
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        connectionHolder = new ConnectionHolder();
        connectionHolder.initialize(dataSource);
    }

    @AfterEach
    void afterEach() throws SQLException {
        connectionHolder.close();
    }

    @Test
    void buildTree_should() throws SQLException {
        connectionHolder.beginTransaction();
        try {
            var root = MPTT.builder().name("ROOT").lft(1).rht(2).build();

            repository.createNode(null, root);
            then(root.getId()).isNotNull();

            connectionHolder.commit();
        } catch (Exception e) {
            connectionHolder.rollback();
        }
    }
}
