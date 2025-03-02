package alvin.study.springboot.graphql.app.api.query;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.WebTest;

public class OrgQueryTest extends WebTest {
    @Test
    void query_shouldReturnOrg() {
        qlTester().documentName("org")
                .operationName("queryOrg")
                .variable("id", currentOrg().getId())
                .execute()
                .path("org")
                .matchesJson("""
                        {
                            "id": "%d",
                            "name": "%s",
                            "createdAt": "%s",
                            "updatedAt": "%s"
                        }
                    """.formatted(
                    currentOrg().getId(),
                    currentOrg().getName().toUpperCase(),
                    formatDatetime(currentOrg().getCreatedAt()),
                    formatDatetime(currentOrg().getUpdatedAt())));
    }
}
