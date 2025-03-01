package alvin.study.springboot.graphql.app.dataloader.common;

import org.dataloader.BatchLoaderEnvironment;

import alvin.study.springboot.graphql.IntegrationTest;

public abstract class DataloaderTest extends IntegrationTest {
    protected BatchLoaderEnvironment buildBatchLoaderEnvironment() {
        return BatchLoaderEnvironment.newBatchLoaderEnvironment()
                .context(context())
                .build();
    }
}
