package alvin.study.springboot.mybatis.infra.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 雇员明细信息类型
 *
 * <p>
 * 该类型对象作为 {@link Employee} 实体类型的字段, 以 JSON 字符串格式存入数据表, 具体参见 {@link Employee} 类型
 * </p>
 */
@Data
@Accessors(chain = true)
public class EmployeeInfo implements Serializable {
    // 生日
    private LocalDate birthday;

    // 性别
    private Gender gender;

    // 电话号码
    private String telephone;
}
