package alvin.study.se.reflect.scan.match;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * {@link Matcher} 接口的一组实现类型以及创建各类 {@link Matcher} 对象的工厂方法
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Matchers {
    /**
     * 返回 {@link Any} 类型的匹配器对象
     *
     * @return {@link Any} 类型的匹配器对象
     */
    public static Matcher<Object> any() {
        return Any.ANY_MATCHER;
    }

    /**
     * 返回 {@link Not} 类型的匹配器对象
     *
     * @param matcher 任意其它 {@link Matcher} 类型对象
     * @return {@link Not} 类型的匹配器对象
     */
    public static <T> Matcher<T> not(Matcher<? super T> matcher) {
        return new Not<>(matcher);
    }

    /**
     * 返回 {@link AnnotatedWith} 类型的匹配器对象
     *
     * @param annotation 要匹配的注解对象
     * @return {@link AnnotatedWith} 类型的匹配器对象
     */
    public static Matcher<AnnotatedElement> annotatedWith(Annotation annotation) {
        return new AnnotatedWith(annotation);
    }

    /**
     * 返回 {@link AnnotatedWith} 类型的匹配器对象
     *
     * @param annotationType 要匹配的超类类型
     * @return {@link AnnotatedWith} 类型的匹配器对象
     */
    public static Matcher<AnnotatedElement> annotatedWith(final Class<? extends Annotation> annotationType) {
        return new AnnotatedWithType(annotationType);
    }

    /**
     * 返回 {@link SubclassesOf} 类型的匹配器对象
     *
     * @param superclass 要匹配的超类类型
     * @return {@link SubclassesOf} 类型的匹配器对象
     */
    public static Matcher<Class<?>> subclassesOf(Class<?> superclass) {
        return new SubclassesOf(superclass);
    }

    /**
     * 返回 {@link Only} 类型的匹配器对象
     *
     * @param value 要匹配的对象值
     * @return {@link Only} 类型的匹配器对象
     */
    public static Matcher<Object> only(Object value) {
        return new Only(value);
    }

    /**
     * 返回 {@link InSubpackage} 类型的匹配器对象
     *
     * @param targetPackageName 要匹配的包名称
     * @return {@link InSubpackage} 类型的匹配器对象
     */
    public static Matcher<Class<?>> inSubpackage(String targetPackageName) {
        return new InSubpackage(targetPackageName);
    }

    /**
     * 返回 {@link AnnotatedWith} 类型的匹配器对象
     *
     * @param value 要匹配的对象引用
     * @return {@link IdenticalTo} 类型的匹配器对象
     */
    public static Matcher<Object> identicalTo(Object value) {
        return new IdenticalTo(value);
    }

    /**
     * 返回 {@link InPackage} 类型的匹配器对象
     *
     * @param targetPackage 要匹配的包对象
     * @return {@link InPackage} 类型的匹配器对象
     */
    public static Matcher<Class<?>> inPackage(Package targetPackage) {
        return new InPackage(targetPackage);
    }

    /**
     * 返回 {@link Returns} 类型的匹配器对象
     *
     * @param returnType 要匹配的返回值类型
     * @return {@link Returns} 类型的匹配器对象
     */
    public static Matcher<Method> returns(Matcher<? super Class<?>> returnType) {
        return new Returns(returnType);
    }

    /**
     * 返回 {@link Is} 类型的匹配器对象
     *
     * @param type 要匹配的超类类型
     * @return {@link Is} 类型的匹配器对象
     */
    public static Matcher<Class<?>> is(Class<?> type) {
        return new Is(type);
    }

    /**
     * 检查所给的注解类型是否具备 {@link Retention @Retention} 注解且 {@code value} 属性为
     * {@link RetentionPolicy#RUNTIME}
     *
     * @param annotationType 注解类型
     */
    private static void checkForRuntimeRetention(Class<? extends Annotation> annotationType) {
        // 获取注解上的 @Retention 注解
        var retention = annotationType.getAnnotation(Retention.class);
        if (retention == null || retention.value() != RetentionPolicy.RUNTIME) {
            throw new IllegalArgumentException(
                String.format("Annotation %s is missing RUNTIME retention",
                    annotationType.getSimpleName()));
        }
    }

    /**
     * 任何情况都可以匹配成功的匹配器
     */
    static final class Any implements Matcher<Object> {
        // 当前类型的常量对象
        static final Matcher<Object> ANY_MATCHER = new Any();

        /**
         * 构造器, 禁止从外部实例化对象
         */
        private Any() {}

        /**
         * 执行匹配操作, 返回匹配结果
         *
         * <p>
         * 无论参数 {@code o} 的值是什么, 都返回匹配成功
         * </p>
         *
         * @return 永远为 {@code true} 表示匹配成功
         */
        @Override
        public boolean matches(Object o) {
            return true;
        }

        @Override
        public String toString() {
            return "any()";
        }

        public Object readResolve() {
            return any();
        }
    }

    /**
     * 对所给的匹配器对象匹配结果求"非"的匹配器类型
     */
    static final class Not<T> implements Matcher<T> {
        // 被代理 Matcher 对象
        private final Matcher<? super T> delegate;

        /**
         * 构造器, 设置被代理的 {@link Matcher} 对象
         *
         * @param delegate 要代理的 {@link Matcher} 对象
         */
        private Not(Matcher<? super T> delegate) {
            if (delegate == null) {
                throw new IllegalArgumentException("delegate");
            }
            this.delegate = delegate;
        }

        /**
         * 执行匹配操作, 返回匹配结果
         *
         * <p>
         * 通过代理的匹配器匹配 {@code t} 参数, 并对结果求"非"
         * </p>
         *
         * @param t 要匹配的对象
         */
        @Override
        public boolean matches(T t) {
            // 对被代理的 Matcher 对象行为求反
            return !delegate.matches(t);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Not<?> o && o.delegate.equals(delegate);
        }

        @Override
        public int hashCode() {
            return -1 * delegate.hashCode();
        }

        @Override
        public String toString() {
            return "not(" + delegate + ")";
        }
    }

    /**
     * 匹配所给 {@link AnnotatedElement} 对象是否具备期待的注解类型
     *
     * <p>
     * 所有可以标记注解的对象, 都实现了 {@link AnnotatedElement} 接口, 例如 {@link Class},
     * {@link java.lang.reflect.Field Field}, {@link Method} 等
     * </p>
     */
    static final class AnnotatedWithType implements Matcher<AnnotatedElement> {
        // 期待的注解类型
        private final Class<? extends Annotation> annotationType;

        /**
         * 构造器, 设置期待的注解类型
         *
         * @param annotationType 期待的注解类型
         */
        AnnotatedWithType(Class<? extends Annotation> annotationType) {
            if (annotationType == null) {
                throw new IllegalArgumentException("annotationType");
            }

            // 检查注解是否为运行时注解
            checkForRuntimeRetention(annotationType);
            this.annotationType = annotationType;
        }

        /**
         * 执行匹配操作, 返回匹配结果
         *
         * <p>
         * 匹配 {@code element} 参数表示的 {@link AnnotatedElement} 对象是否具备期待的注解类型
         * </p>
         *
         * @param element {@link AnnotatedElement} 类型对象
         */
        @Override
        public boolean matches(AnnotatedElement element) {
            return element.isAnnotationPresent(annotationType);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof AnnotatedWithType o && o.annotationType.equals(annotationType);
        }

        @Override
        public int hashCode() {
            return 37 * annotationType.hashCode();
        }

        @Override
        public String toString() {
            return "annotatedWith(" + annotationType.getSimpleName() + ".class)";
        }
    }

    /**
     * 匹配所给 {@link AnnotatedElement} 对象是否具备期待的注解
     *
     * <p>
     * 所有可以标记注解的对象, 都实现了 {@link AnnotatedElement} 接口, 例如 {@link Class},
     * {@link java.lang.reflect.Field Field}, {@link Method} 等
     * </p>
     */
    static final class AnnotatedWith implements Matcher<AnnotatedElement> {
        // 期待的注解对象
        private final transient Annotation annotation;

        /**
         * 构造器, 设置期待的注解对象
         *
         * @param annotation 期待的注解对象
         */
        AnnotatedWith(Annotation annotation) {
            if (annotation == null) {
                throw new IllegalArgumentException("annotation");
            }

            // 检查注解是否为运行时注解
            checkForRuntimeRetention(annotation.annotationType());
            this.annotation = annotation;
        }

        /**
         * 执行匹配操作, 返回匹配结果
         *
         * <p>
         * 匹配 {@code element} 参数表示的 {@link AnnotatedElement} 对象是否具备期待的注解
         * </p>
         *
         * @param element {@link AnnotatedElement} 类型对象
         */
        @Override
        public boolean matches(AnnotatedElement element) {
            // 从所给对象上获取期待类型的注解
            var fromElement = element.getAnnotation(annotation.annotationType());
            // 判断注解是否为期待注解
            return fromElement != null && annotation.equals(fromElement);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof AnnotatedWith o && o.annotation.equals(annotation);
        }

        @Override
        public int hashCode() {
            return 37 * annotation.hashCode();
        }

        @Override
        public String toString() {
            return "annotatedWith(" + annotation + ")";
        }
    }

    /**
     * 匹配所给类型是否是期待类型的子类型
     */
    static final class SubclassesOf implements Matcher<Class<?>> {
        // 期待类型
        private final Class<?> superclass;

        /**
         * 构造器, 设置期待类型
         *
         * @param superclass 期待类型
         */
        SubclassesOf(Class<?> superclass) {
            if (superclass == null) {
                throw new IllegalArgumentException("superclass");
            }
            this.superclass = superclass;
        }

        /**
         * 执行匹配操作, 返回匹配结果
         *
         * <p>
         * 匹配 {@code subclass} 参数表示的类型是否为期待类型的子类型
         * </p>
         *
         * @param subclass 要匹配的类型
         */
        @Override
        public boolean matches(Class<?> subclass) {
            return superclass.isAssignableFrom(subclass);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof SubclassesOf o && o.superclass.equals(superclass);
        }

        @Override
        public int hashCode() {
            return 37 * superclass.hashCode();
        }

        @Override
        public String toString() {
            return "subclassesOf(" + superclass.getSimpleName() + ".class)";
        }
    }

    /**
     * 匹配两个对象是否相等
     */
    static final class Only implements Matcher<Object> {
        // 要比较的对象
        private final transient Object value;

        /**
         * 构造器, 设置待比较的对象
         *
         * @param value 待比较的对象
         */
        Only(Object value) {
            if (value == null) {
                throw new IllegalArgumentException("value");
            }
            this.value = value;
        }

        /**
         * 执行匹配操作, 返回匹配结果
         *
         * <p>
         * 匹配 {@code other} 参数对象是否和期待的对象是否相等 (通过 {@code equals} 方法比较)
         * </p>
         *
         * @param other 要比较的对象
         */
        @Override
        public boolean matches(Object other) {
            return value.equals(other);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Only o && o.value.equals(value);
        }

        @Override
        public int hashCode() {
            return 37 * value.hashCode();
        }

        @Override
        public String toString() {
            return "only(" + value + ")";
        }
    }

    /**
     * 匹配两个对象是否相同
     */
    static final class IdenticalTo implements Matcher<Object> {
        // 要比较的对象
        private final transient Object value;

        /**
         * 构造器, 设置待比较的对象
         *
         * @param value 待比较的对象
         */
        IdenticalTo(Object value) {
            if (value == null) {
                throw new IllegalArgumentException("value");
            }
            this.value = value;
        }

        /**
         * 执行匹配操作, 返回匹配结果
         *
         * <p>
         * 匹配 {@code other} 参数对象是否和期待的对象相同 (通过 {@code =} 比较)
         * </p>
         *
         * @param other 要比较的对象
         */
        @Override
        public boolean matches(Object other) {
            return value == other;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof IdenticalTo o && o.value == value;
        }

        @Override
        public int hashCode() {
            return 37 * System.identityHashCode(value);
        }

        @Override
        public String toString() {
            return "identicalTo(" + value + ")";
        }
    }

    /**
     * 匹配一个类型的包名是否和期待包名一致
     */
    static final class InPackage implements Matcher<Class<?>> {
        // 期待的包对象
        private final transient Package targetPackage;

        /**
         * 构造器, 设置期待的包对象
         *
         * @param targetPackage 期待的包对象
         */
        InPackage(Package targetPackage) {
            if (targetPackage == null) {
                throw new IllegalArgumentException("targetPackage");
            }
            this.targetPackage = targetPackage;
        }

        /**
         * 执行匹配操作, 返回匹配结果
         *
         * <p>
         * 匹配 {@code c} 参数表示类型的包是否和期待的包一致
         * </p>
         *
         * @param c 要匹配的类型对象
         */
        @Override
        public boolean matches(Class<?> c) {
            return c.getPackage().equals(targetPackage);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof InPackage o && o.targetPackage.equals(targetPackage);
        }

        @Override
        public int hashCode() {
            return 37 * targetPackage.hashCode();
        }

        @Override
        public String toString() {
            return "inPackage(" + targetPackage.getName() + ")";
        }

        public Object readResolve() {
            return inPackage(getClass().getClassLoader().getDefinedPackage(targetPackage.getName()));
        }
    }

    /**
     * 匹配一个类型是否位于期待的包中
     */
    static final class InSubpackage implements Matcher<Class<?>> {
        // 期待的包名称
        private final String targetPackageName;

        /**
         * 构造器, 设置期待的包名称
         *
         * @param targetPackageName 期待的包名称
         */
        InSubpackage(String targetPackageName) {
            this.targetPackageName = targetPackageName;
        }

        /**
         * 执行匹配操作, 返回匹配结果
         *
         * <p>
         * 匹配 {@code c} 参数表示的类型是否位于期待的包中
         * </p>
         *
         * @param c 要匹配的类型对象
         */
        @Override
        public boolean matches(Class<?> c) {
            // 获取所给类型所在的包
            var classPackageName = c.getPackage().getName();
            // 判断类所在的包是否为期待的包, 或是所期待包的子包
            return classPackageName.equals(targetPackageName) || classPackageName.startsWith(targetPackageName + ".");
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof InSubpackage o && o.targetPackageName.equals(targetPackageName);
        }

        @Override
        public int hashCode() {
            return 37 * targetPackageName.hashCode();
        }

        @Override
        public String toString() {
            return "inSubpackage(" + targetPackageName + ")";
        }
    }

    /**
     * 匹配一个方法的返回类型是否和期望类型
     */
    static final class Returns implements Matcher<Method> {
        // 期待的返回值类型
        private final Matcher<? super Class<?>> returnType;

        /**
         * 构造器, 设置期待的返回值类型
         *
         * @param returnType 期待的返回值类型
         */
        Returns(Matcher<? super Class<?>> returnType) {
            if (returnType == null) {
                throw new IllegalArgumentException("returnType");
            }
            this.returnType = returnType;
        }

        /**
         * 执行匹配操作, 返回匹配结果
         *
         * <p>
         * 匹配 {@code m} 参数表示的方法返回值类型是否和期待的返回值类型一致
         * </p>
         *
         * @param m 要匹配的方法对象
         */
        @Override
        public boolean matches(Method m) {
            return returnType.matches(m.getReturnType());
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Returns o && o.returnType.equals(returnType);
        }

        @Override
        public int hashCode() {
            return 37 * returnType.hashCode();
        }

        @Override
        public String toString() {
            return "returns(" + returnType + ")";
        }
    }

    /**
     * 匹配类型是否符合预期
     */
    static final class Is implements Matcher<Class<?>> {
        // 期待的类型对象
        private final Class<?> type;

        /**
         * 构造器, 设置期待的类型对象
         *
         * @param type 期待的类型对象
         */
        Is(Class<?> type) {
            this.type = type;
        }

        /**
         * 执行匹配操作, 返回匹配结果
         *
         * <p>
         * 匹配 {@code other} 参数是否和期待的类型匹配
         * </p>
         *
         * @param other 要匹配的类型对象
         */
        @Override
        public boolean matches(Class<?> other) {
            return type == other;
        }

        @Override
        public String toString() {
            return "is(" + type.getSimpleName() + ")";
        }
    }
}
