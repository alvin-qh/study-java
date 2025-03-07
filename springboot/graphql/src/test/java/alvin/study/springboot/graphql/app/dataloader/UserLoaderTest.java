package alvin.study.springboot.graphql.app.dataloader;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.graphql.IntegrationTest;
import alvin.study.springboot.graphql.builder.UserBuilder;
import alvin.study.springboot.graphql.infra.entity.User;
import alvin.study.springboot.graphql.infra.mapper.UserMapper;

public class UserLoaderTest extends IntegrationTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    void apply_shouldLoadUsers() {
        User user1, user2, user3;

        try (var ignore = beginTx(false)) {
            user1 = newBuilder(UserBuilder.class).create();

            user2 = newBuilder(UserBuilder.class).create();

            user3 = newBuilder(UserBuilder.class).create();
        }

        var loader = new UserLoader(userMapper);
        var result = loader.apply(
            Set.of(user1.getId(), user2.getId(), user3.getId()), null);

        var map = result.block();
        then(map.get(user1.getId()).getId()).isEqualTo(user1.getId());
        then(map.get(user2.getId()).getId()).isEqualTo(user2.getId());
        then(map.get(user3.getId()).getId()).isEqualTo(user3.getId());
    }
}
