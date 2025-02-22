package alvin.study.springboot.jooq.infra.model;

import java.time.LocalDate;

import lombok.Data;
import lombok.experimental.Accessors;

import alvin.study.springboot.jooq.infra.converter.EmployeeInfoConverter;

/**
 * 雇员信息类型
 *
 * <p>
 * 该类型对应 {@code EMPLOYEE} 表的 {@code info} 字段, 要使在生成 Jooq 代码时将字段类型定义为 JSON,
 * 需要在生成代码的配置中增加 {@code forcedType} 配置. 参考 {@code pom.xml} 中 jooq generator 插件的
 * {@code generator.database.forcedTypes} 部分或 {@code build.gradle} 中 jooq 配置的
 * {@code configurations.generationTool.generator.database.forcedTypes} 部分
 * </p>
 *
 * <p>
 * 该字段的转换方法请参考:
 * {@link EmployeeInfoConverter
 * EmployeeInfoConverter} 类型
 * </p>
 */
@Data
@Accessors(chain = true)
public class EmployeeInfo {
    // 性别
    private Gender gender;

    // 生日
    private LocalDate birthday;

    // 电话号码
    private String telephone;
}
