package alvin.study.springboot.graphql.core.context;

import java.util.HashMap;
import java.util.Map;

public final class Context {
    private Map<String, Object> contextMap = new HashMap<>();

    public <T> T get(String key) {
        return (T) contextMap.get(key);
    }

    public void put(String key, Object value) {
        contextMap.put(key, value);
    }

    public void remove(String key) {
        contextMap.remove(key);
    }

    public void clear() {
        contextMap.clear();
    }
}
