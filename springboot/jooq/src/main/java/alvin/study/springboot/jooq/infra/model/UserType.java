package alvin.study.springboot.jooq.infra.model;

/**
 * 定义用户类型枚举
 *
 * <p>
 * 该枚举对应 {@code USER} 表的 {@code type} 字段, 要使在生成 Jooq 代码时将字段类型定义为枚举,
 * 需要在生成代码的配置中增加 {@code forcedType} 配置. 参考 {@code pom.xml} 中 jooq generator 插件的
 * {@code generator.database.forcedTypes} 部分或 {@code build.gradle} 中 jooq 配置的
 * {@code configurations.generationTool.generator.database.forcedTypes} 部分
 * </p>
 */
public enum UserType {
    ADMIN, OPERATOR, NORMAL
}
