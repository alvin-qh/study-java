package alvin.study.springboot.graphql.app.dataloader;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.app.dataloader.common.DataloaderTest;
import alvin.study.springboot.graphql.builder.OrgBuilder;
import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.mapper.OrgMapper;

public class OrgLoaderTest extends DataloaderTest {
    @Autowired
    private OrgMapper orgMapper;

    @Test
    void apply_shouldLoadOrgs() {
        Org org1, org2, org3;

        try (var ignore = beginTx(false)) {
            org1 = newBuilder(OrgBuilder.class).create();
            org2 = newBuilder(OrgBuilder.class).create();
            org3 = newBuilder(OrgBuilder.class).create();
        }

        var loader = new OrgLoader(orgMapper);
        var result = loader.apply(
            Set.of(org1.getId(), org2.getId(), org3.getId()),
            buildBatchLoaderEnvironment());

        var map = result.block();
        then(map.get(org1.getId()).getId()).isEqualTo(org1.getId());
        then(map.get(org2.getId()).getId()).isEqualTo(org2.getId());
        then(map.get(org3.getId()).getId()).isEqualTo(org3.getId());
    }
}
