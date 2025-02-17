package alvin.study.se.collection;

import java.util.Map;
import java.util.Objects;

/**
 * 表示一对值的类型
 *
 * <p>
 * 该类型包含两个值 {@code left} 和 {@code right}
 * </p>
 *
 * <p>
 * 该类型从 {@link Map.Entry} 类型继承, 所以也可以作为 {@link Map} 对象的键值对对象
 * </p>
 */
public final class Pair<K, V> implements Map.Entry<K, V> {
    private final K left;
    private V right;

    public Pair(K left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <K, V> Pair<K, V> of(K left, V right) {
        return new Pair<>(left, right);
    }

    public K getLeft() {
        return getKey();
    }

    public V getRight() {
        return getValue();
    }

    @Override
    public K getKey() {
        return left;
    }

    @Override
    public V getValue() {
        return right;
    }

    @Override
    public V setValue(V value) {
        var old = this.right;
        this.right = value;
        return old;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair<?, ?> other) {
            return left.equals(other.left) && right.equals(other.right);
        }
        if (obj instanceof Map.Entry<?, ?> other) {
            return left.equals(other.getKey()) && right.equals(other.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 55 ^ Objects.hashCode(this.left) ^ Objects.hashCode(this.right);
    }

    @Override
    public String toString() {
        return String.format("%s=%s", left, right);
    }
}
