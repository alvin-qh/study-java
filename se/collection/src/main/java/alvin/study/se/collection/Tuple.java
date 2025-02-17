package alvin.study.se.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class Tuple<E> implements Iterable<E> {
    private final List<E> elements;

    private Tuple(E[] elements) {
        this.elements = List.of(elements);
    }

    private Tuple(Collection<E> elements) {
        this.elements = List.copyOf(elements);
    }

    @SafeVarargs
    public static <T> Tuple<T> of(T... elements) {
        return new Tuple<>(elements);
    }

    public static <T> Tuple<T> of(Collection<T> elements) {
        return new Tuple<>(elements);
    }

    public E get(int index) {
        return elements.get(index);
    }

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public boolean contains(Object o) {
        return elements.contains(o);
    }

    public Object[] toArray() {
        return elements.toArray();
    }

    public <T> T[] toArray(T[] a) {
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
