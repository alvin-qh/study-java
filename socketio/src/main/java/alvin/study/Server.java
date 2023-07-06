package alvin.study;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Server {
    private static final String PROP_PORT = "jetty.port";
    private static final String PROP_HOST = "jetty.host";
    private static final String PROP_CONTEXT_PATH = "jetty.context.path";
    private static final String PROP_THREAD_POOL_MIN = "jetty.threadpool.min";
    private static final String PROP_THREAD_POOL_MAX = "jetty.threadpool.max";
    private static final String PROP_IDLE_TIME = "jetty.threadpool.idle";
    private static final String PROP_QUEUE_SIZE = "jetty.queue.size";

    public static void main(String[] args) {
        try {
            new Server().startJetty();
        } catch (Exception e) {
            log.error("Cannot start jetty server", e);
            System.exit(1);
        }
    }

    private void startJetty() throws Exception {
        var server = createServer();

        server.setHandler(createHandlerChain());
        server.addConnector(createConnector(server));
        server.start();
        server.join();
    }

    private Connector createConnector(org.eclipse.jetty.server.Server server) {
        var conn = new ServerConnector(server);

        conn.setHost(readSystemPropertyAsString(PROP_HOST, "0.0.0.0"));
        conn.setPort(readSystemPropertyAsInt(PROP_PORT, 8080));
        return conn;
    }

    private Handler createHandlerChain() {
        var baseHandler = new WebAppContext();

        baseHandler.setContextPath(readSystemPropertyAsString(PROP_CONTEXT_PATH, ""));
        baseHandler.setWar(Server.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm());

        var zipHandler = new GzipHandler();

        zipHandler.setHandler(baseHandler);
        zipHandler.setIncludedMimeTypes(
            "text/html",
            "text/plain",
            "text/css",
            "text/xml",
            "application/json",
            "application/javascript",
            "application/xhtml+xml",
            "application/xml",
            "image/svg+xml");

        return zipHandler;
    }

    private org.eclipse.jetty.server.Server createServer() {
        var threadPoolMax = readSystemPropertyAsInt(PROP_THREAD_POOL_MAX, 200);
        var threadPoolMin = readSystemPropertyAsInt(PROP_THREAD_POOL_MIN, 8);

        var threadPool = new QueuedThreadPool(
            threadPoolMax,
            threadPoolMin,
            readSystemPropertyAsInt(PROP_IDLE_TIME, 60000),
            new BlockingArrayQueue<>(readSystemPropertyAsInt(PROP_QUEUE_SIZE, threadPoolMax), threadPoolMin));

        return new org.eclipse.jetty.server.Server(threadPool);
    }

    private static int readSystemPropertyAsInt(String propertyName, int defaultValue) {
        var value = System.getProperty(propertyName);
        if (Strings.isNullOrEmpty(value)) {
            return defaultValue;
        }

        return Integer.parseInt(value);
    }

    private static String readSystemPropertyAsString(String propertyName, String defaultValue) {
        var value = System.getProperty(propertyName);
        if (Strings.isNullOrEmpty(value)) {
            return defaultValue;
        }

        return value;
    }
}
