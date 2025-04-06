package alvin.study.springboot.graphql.app.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.IntegrationTest;
import alvin.study.springboot.graphql.builder.OrgBuilder;
import alvin.study.springboot.graphql.core.exception.NotFoundException;

class OrgServiceTest extends IntegrationTest {
    @Autowired
    private OrgService orgService;

    @Test
    void create_shouldCreateOrg() {
        var expectedOrg = newBuilder(OrgBuilder.class).build();

        orgService.create(expectedOrg);

        var actualOrg = orgService.findById(expectedOrg.getId());
        then(actualOrg.getId()).isEqualTo(expectedOrg.getId());
    }

    @Test
    void delete_shouldDeleteExistOrg() {
        var org = newBuilder(OrgBuilder.class).create();

        orgService.delete(org.getId());
        thenThrownBy(() -> orgService.findById(org.getId())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_shouldFindExistOrg() {
        var expectedOrg = newBuilder(OrgBuilder.class).create();

        var actualOrg = orgService.findById(expectedOrg.getId());

        then(actualOrg.getId()).isEqualTo(expectedOrg.getId());
    }

    @Test
    void update_shouldUpdateExistOrg() {
        var org = newBuilder(OrgBuilder.class).create();

        var originOrgName = org.getName();

        org.setName("updated_" + org.getName());
        orgService.update(org);

        org = orgService.findById(org.getId());
        then(org.getName()).isEqualTo("updated_" + originOrgName);
    }
}
