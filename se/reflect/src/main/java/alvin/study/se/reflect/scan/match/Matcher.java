package alvin.study.se.reflect.scan.match;

import java.io.Serializable;

/**
 * 匹配器对象
 */
public interface Matcher<T> extends Serializable {
    /**
     * 执行匹配操作, 返回匹配结果
     *
     * @param t 要匹配的对象
     * @return 如果所给参数符合匹配规则则返回 {@code true}, 否则返回 {@code false}
     */
    boolean matches(T t);

    /**
     * 对当前 {@link Matcher} 和所给的另一个 {@link Matcher} 对象进行"与"运算
     *
     * @param other 进行"与"运算的另一个 {@link Matcher} 对象
     * @return 返回一个表示"与"运算的 {@link Matcher} 对象
     */
    default Matcher<T> and(Matcher<? super T> other) {
        return new AndMatcher<>(this, other);
    }

    /**
     * 对当前 {@link Matcher} 和所给的另一个 {@link Matcher} 对象进行"或"运算
     *
     * @param other 进行"或"运算的另一个 {@link Matcher} 对象
     * @return 返回一个表示"或"运算的 {@link Matcher} 对象
     */
    default Matcher<T> or(Matcher<? super T> other) {
        return new OrMatcher<>(this, other);
    }

    /**
     * 对两个 {@link Matcher} 对象进行"与"运算的 {@link Matcher} 类型
     */
    static final class AndMatcher<T> implements Matcher<T> {
        // 要进行"与"运算的两个 Matcher 对象
        private final Matcher<? super T> a;
        private final Matcher<? super T> b;

        /**
         * 构造器, 设置要进行"与"运算的的两个 {@link Matcher} 对象
         *
         * @param a 第一个 {@link Matcher} 对象
         * @param b 第二个 {@link Matcher} 对象
         */
        AndMatcher(Matcher<? super T> a, Matcher<? super T> b) {
            this.a = a;
            this.b = b;
        }

        /**
         * 执行匹配操作, 返回匹配结果
         *
         * <p>
         * 当前类型包含两个 {@link Matcher} 对象, 该方法返回这两个对象匹配结果的"与"运算结果
         * </p>
         */
        @Override
        public boolean matches(T t) {
            return a.matches(t) && b.matches(t);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof AndMatcher<?> o && o.a.equals(a) && o.b.equals(b);
        }

        @Override
        public int hashCode() {
            return 41 * (a.hashCode() ^ b.hashCode()); // SUPPRESS
        }

        @Override
        public String toString() {
            return "and(" + a + ", " + b + ")";
        }
    }

    /**
     * 对两个 {@link Matcher} 对象进行"或"运算的 {@link Matcher} 类型
     */
    static final class OrMatcher<T> implements Matcher<T> {
        // 要进行"或"运算的两个 Matcher 对象
        private final Matcher<? super T> a;
        private final Matcher<? super T> b;

        /**
         * 构造器, 设置要进行"或"运算的的两个 {@link Matcher} 对象
         *
         * @param a 第一个 {@link Matcher} 对象
         * @param b 第二个 {@link Matcher} 对象
         */
        OrMatcher(Matcher<? super T> a, Matcher<? super T> b) {
            this.a = a;
            this.b = b;
        }

        /**
         * 执行匹配操作, 返回匹配结果
         *
         * <p>
         * 当前类型包含两个 {@link Matcher} 对象, 该方法返回这两个对象匹配结果的"或"运算结果
         * </p>
         */
        @Override
        public boolean matches(T t) {
            return a.matches(t) || b.matches(t);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof OrMatcher<?> o && o.a.equals(a) && o.b.equals(b);
        }

        @Override
        public int hashCode() {
            return 37 * (a.hashCode() ^ b.hashCode()); // SUPPRESS
        }

        @Override
        public String toString() {
            return "or(" + a + ", " + b + ")";
        }
    }
}
