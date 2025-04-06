package alvin.study.springboot.graphql.app.api.mutation;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.WebTest;
import alvin.study.springboot.graphql.app.service.OrgService;
import alvin.study.springboot.graphql.builder.OrgBuilder;
import alvin.study.springboot.graphql.core.exception.NotFoundException;

class OrgMutationTest extends WebTest {
    @Autowired
    private OrgService orgService;

    @Test
    void createOrg_shouldCreateNewOrg() {
        var input = new OrgMutation.OrgInput("HD");

        var resp = qlTester().documentName("org")
                .operationName("createOrg")
                .variable("input", input)
                .execute();

        var id = resp.path("createOrg.result.id")
                .entity(Long.class).get();

        var actualOrg = orgService.findById(id);

        resp.path("createOrg.result")
                .matchesJson("""
                    {
                        "id": "%d",
                        "name": "%s"
                    }
                    """.formatted(
                    actualOrg.getId(),
                    actualOrg.getName().toUpperCase()));
    }

    @Test
    void updateOrg_shouldUpdateExistOrg() {
        var expectedOrg = newBuilder(OrgBuilder.class).create();

        var input = new OrgMutation.OrgInput("updated_" + expectedOrg.getName());

        var resp = qlTester().documentName("org")
                .operationName("updateOrg")
                .variable("id", expectedOrg.getId())
                .variable("input", input)
                .execute();

        resp.path("updateOrg.result")
                .matchesJson("""
                    {
                        "id": "%d",
                        "name": "UPDATED_%s"
                    }
                    """.formatted(
                    expectedOrg.getId(),
                    expectedOrg.getName().toUpperCase()));
    }

    @Test
    void deleteOrg_shouldDeleteExistOrg() {
        var org = newBuilder(OrgBuilder.class).create();

        var result = qlTester().documentName("org")
                .operationName("deleteOrg")
                .variable("id", org.getId())
                .execute()
                .path("deleteOrg")
                .entity(Boolean.class)
                .get();

        then(result).isTrue();

        thenThrownBy(() -> orgService.findById(org.getId())).isInstanceOf(NotFoundException.class);
    }
}
