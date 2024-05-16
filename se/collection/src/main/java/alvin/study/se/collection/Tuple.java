package alvin.study.se.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Tuple<E> implements Iterable<E> {
    private final List<E> elements;

    private Tuple(E[] elements) {
        this.elements = List.of(elements);
    }

    private Tuple(Collection<E> elements) {
        this.elements = List.copyOf(elements);
    }

    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public static <T> @NotNull Tuple<T> of(T... elements) {
        return new Tuple<>(elements);
    }

    @Contract("_ -> new")
    public static <T> @NotNull Tuple<T> of(Collection<T> elements) {
        return new Tuple<>(elements);
    }

    public E get(int index) {
        return elements.get(index);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() { return elements.isEmpty(); }

    public boolean contains(Object o) {
        return elements.contains(o);
    }

    @Contract(pure = true)
    public Object @NotNull [] toArray() {
        return elements.toArray();
    }

    public <T> T @NotNull [] toArray(T[] a) {
        return elements.toArray(a);
    }

    public boolean containsAll(Collection<E> c) {
        if (c == null) {
            c = List.of();
        }
        return elements.containsAll(c);
    }

    public List<E> toList() {
        return elements;
    }
}
