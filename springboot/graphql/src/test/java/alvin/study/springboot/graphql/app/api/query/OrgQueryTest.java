package alvin.study.springboot.graphql.app.api.query;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.WebTest;
import alvin.study.springboot.graphql.infra.entity.Org;

public class OrgQueryTest extends WebTest {

    @Test
    void query_shouldReturnOrg() {
        var org = qlTester().documentName("org")
                .operationName("queryOrg")
                .variable("id", currentOrg().getId())
                .execute()
                .path("org")
                .entity(Org.class)
                .get();

        then(org.getId()).isEqualTo(currentOrg().getId());
        then(org.getName()).isEqualTo(currentOrg().getName().toUpperCase());
    }
}
