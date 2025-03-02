package alvin.study.springboot.graphql.app.api.query;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.WebTest;
import alvin.study.springboot.graphql.builder.UserBuilder;

public class UserQueryTest extends WebTest {
    @Test
    void query_shouldFindUserById() {
        var expectedUser = newBuilder(UserBuilder.class)
                .withOrgId(currentOrg().getId())
                .withAuditorId(currentUser().getId())
                .create();

        qlTester().documentName("user")
                .operationName("queryUser")
                .variable("id", expectedUser.getId())
                .execute()
                .path("user")
                .matchesJson("""
                        {
                            "id": "%d",
                            "orgId": "%d",
                            "org": {
                                "id": "%d",
                                "name": "%s"
                            },
                            "account": "%s",
                            "group": "%s",
                            "createdBy": "%d",
                            "updatedBy": "%d",
                            "createdAt": "%s",
                            "updatedAt": "%s",
                            "createdByUser": {
                                "id": "%d",
                                "account": "%s"
                            },
                            "updatedByUser": {
                                "id": "%d",
                                "account": "%s"
                            }
                        }
                    """.formatted(
                    expectedUser.getId(),
                    expectedUser.getOrgId(),
                    expectedUser.getOrgId(),
                    currentOrg().getName().toUpperCase(),
                    expectedUser.getAccount(),
                    expectedUser.getGroup().name(),
                    currentUser().getId(),
                    currentUser().getId(),
                    formatDatetime(expectedUser.getCreatedAt()),
                    formatDatetime(expectedUser.getUpdatedAt()),
                    currentUser().getId(),
                    currentUser().getAccount(),
                    currentUser().getId(),
                    currentUser().getAccount()));
    }
}
