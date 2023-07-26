package alvin.study.springboot.mvc.core.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Web 访问上下文
 *
 * <p>
 * 该上下文用于存储 HTTP 请求的上下文, 通过一个 Map 存储 Key/Value, 在请求的后续操作中可以取得存储的值
 * </p>
 *
 * @see Context
 */
public class WebContext implements Context {
    private Map<String, Object> attributes = new HashMap<>();

    @Override
    public String name() {
        return "Web Context";
    }

    @Override
    public boolean has(String name) {
        return attributes.containsKey(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String name, T defaultValue) {
        return (T) this.attributes.getOrDefault(name, defaultValue);
    }

    @Override
    public void set(String name, Object value) {
        attributes.put(name, value);
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
