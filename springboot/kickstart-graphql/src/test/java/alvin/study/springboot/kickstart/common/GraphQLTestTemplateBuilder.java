package alvin.study.springboot.kickstart.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import lombok.SneakyThrows;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class GraphQLTestTemplateBuilder {
    private final GraphQLTestTemplate originalGraphQLTestTemplate;

    private final Field fieldResourceLoader;
    private final Field fieldRestTemplate;
    private final Field fieldGraphqlMapping;
    private final Field fieldObjectMapper;

    @SneakyThrows
    public GraphQLTestTemplateBuilder(GraphQLTestTemplate graphQLTestTemplate) {
        this.originalGraphQLTestTemplate = graphQLTestTemplate;

        var clazz = originalGraphQLTestTemplate.getClass();

        this.fieldResourceLoader = clazz.getDeclaredField("resourceLoader");
        this.fieldResourceLoader.setAccessible(true);

        this.fieldRestTemplate = clazz.getDeclaredField("restTemplate");
        this.fieldRestTemplate.setAccessible(true);

        this.fieldGraphqlMapping = clazz.getDeclaredField("graphqlMapping");
        this.fieldGraphqlMapping.setAccessible(true);

        this.fieldObjectMapper = clazz.getDeclaredField("objectMapper");
        this.fieldObjectMapper.setAccessible(true);
    }

    @SneakyThrows
    public GraphQLTestTemplate build() {
        return new GraphQLTestTemplate(
            (ResourceLoader) fieldResourceLoader.get(originalGraphQLTestTemplate),
            (TestRestTemplate) fieldRestTemplate.get(originalGraphQLTestTemplate),
            (String) fieldGraphqlMapping.get(originalGraphQLTestTemplate),
            (ObjectMapper) fieldObjectMapper.get(originalGraphQLTestTemplate));
    }
}
