package alvin.study.springboot.graphql.core.graphql.relay;

import static org.assertj.core.api.BDDAssertions.then;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.IntegrationTest;

public class PaginationTest extends IntegrationTest {
    @Value("${spring.data.web.pageable.default-page-size}")
    private int defaultPageSize;

    @Value("${spring.data.web.pageable.max-page-size}")
    private int maxPageSize;

    @Autowired
    private Pagination pagination;

    @Test
    void getDefaultPageSize_shouldGetDefaultPageSize() {
        var size = pagination.getDefaultPageSize();
        then(size).isEqualTo(defaultPageSize);
    }

    @Test
    void getMaxPageSize_shouldGetMaxPageSize() {
        var size = pagination.getMaxPageSize();
        then(size).isEqualTo(maxPageSize);
    }

    @Test
    void newBuilder_shouldBuildPageObjectByFirstAndAfter() {
        var page = pagination.newBuilder()
                .withFirst(10)
                .withAfter(Cursors.makeCursor(100))
                .build();

        then(page.offset()).isEqualTo(100);
        then(page.getCurrent()).isEqualTo(11);
        then(page.getSize()).isEqualTo(10);
    }

    @Test
    void newBuilder_shouldBuildPageObjectByLastAndBefore() {
        var page = pagination.newBuilder()
                .withLast(10)
                .withBefore(Cursors.makeCursor(100))
                .build();

        then(page.offset()).isEqualTo(90);
        then(page.getCurrent()).isEqualTo(10);
        then(page.getSize()).isEqualTo(10);
    }

    @Test
    void newBuilder_shouldBuildPageObjectByOffsetAndLimit() {
        var page = pagination.newBuilder()
                .withLimit(10)
                .withOffset(100)
                .build();

        then(page.offset()).isEqualTo(100);
        then(page.getCurrent()).isEqualTo(11);
        then(page.getSize()).isEqualTo(10);
    }

    @Test
    void newBuilder_shouldBuildPageObjectWithOrderItems() {
        var page = pagination.newBuilder()
                .withLimit(10)
                .withOffset(100)
                .withOrder("-id,+time")
                .build();

        then(page.orders()).hasSize(2);

        then(page.orders().get(0).getColumn()).isEqualTo("id");
        then(page.orders().get(0).isAsc()).isFalse();

        then(page.orders().get(1).getColumn()).isEqualTo("time");
        then(page.orders().get(1).isAsc()).isTrue();
    }
}
