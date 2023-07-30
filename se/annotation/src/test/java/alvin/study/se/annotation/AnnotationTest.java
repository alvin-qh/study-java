package alvin.study.se.annotation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 用于测试注解的类型
 *
 * <p>
 * {@link A @A} 注解注解在类上, 可以通过
 * {@link AnnotationUtil#getClassAnnotation(Class, Class)} 方法获取到
 * </p>
 *
 * <p>
 * {@link B @B} 注解注解在字段上, 可以通过
 * {@link AnnotationUtil#getFieldAnnotation(Class, String, Class)} 方法获取到
 * </p>
 *
 * <p>
 * {@link C @C} 注解注解在字段上, 可以通过
 * {@link AnnotationUtil#getMethodAnnotation(Class, String, Class)} 方法获取到
 * </p>
 */
@A("TypeAnnotation")
class AnnotationDemo {
    /**
     * 测试在字段上设置注解
     */
    @B("FieldAnnotation")
    private String field;

    /**
     * 测试在方法上设置注解
     */
    @C({ "MethodAnnotation-B1", "MethodAnnotation-B2" })
    public void method() { }
}

/**
 * 测试 {@link AnnotationUtil} 获取不同位置的注解对象
 */
class AnnotationTest {
    /**
     * 测试获取类型上的注解
     *
     * <p>
     * 测试 {@link AnnotationUtil#getClassAnnotation(Class, Class)} 方法, 获取类注解
     * </p>
     */
    @Test
    void getClassAnnotation_shouldGetAnnotationOnClass() {
        var mayAnno = AnnotationUtil.getClassAnnotation(AnnotationDemo.class, A.class);
        then(mayAnno).isPresent();

        var anno = mayAnno.get();
        then(anno.value()).isEqualTo("TypeAnnotation");
    }

    /**
     * 测试获取类字段上的注解
     *
     * <p>
     * 测试 {@link AnnotationUtil#getFieldAnnotation(Class, String, Class)} 方法, 获取字段注解
     * </p>
     */
    @Test
    void getFieldAnnotation_shouldGetAnnotationOnField() {
        var annos = AnnotationUtil.getFieldAnnotation(AnnotationDemo.class, "field", B.class);

        var anno = annos.get(0);
        then(anno.value()).isEqualTo("FieldAnnotation");
    }

    /**
     * 测试获取类方法上的注解
     *
     * <p>
     * 测试 {@link AnnotationUtil#getMethodAnnotation(Class, String, Class)} 方法,
     * 获取字段注解
     * </p>
     */
    @Test
    void getMethodAnnotation_shouldGetAnnotationsOnMethod() {
        var annos = AnnotationUtil.getMethodAnnotation(AnnotationDemo.class, "method", C.class);

        var anno = annos.get(0);
        then(anno.value()).containsExactly("MethodAnnotation-B1", "MethodAnnotation-B2");
    }

}
