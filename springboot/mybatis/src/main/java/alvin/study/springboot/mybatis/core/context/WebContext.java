package alvin.study.springboot.mybatis.core.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Web 访问上下文
 *
 * @see Context
 */
public class WebContext implements Context {
    private Map<String, Object> attributes = new HashMap<>();

    @Override
    public String name() {
        return "Web Context";
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOrDefault(String key, T defaultValue) {
        return (T) this.attributes.getOrDefault(key, defaultValue);
    }

    @Override
    public boolean has(String key) {
        return attributes.containsKey(key);
    }

    @Override
    public void set(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public void remove(String name) {
        attributes.remove(name);
    }

    @Override
    public void clear() {
        var old = attributes;
        attributes = new HashMap<>();
        old.clear();
    }

    @Override
    public String toString() {
        return attributes.toString();
    }
}
