package alvin.study.springboot.mybatis.domain.model;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import alvin.study.springboot.mybatis.infra.entity.Gender;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInfoDto implements Serializable {
    // 生日
    private LocalDate birthday;

    // 性别
    private Gender gender;

    // 电话号码
    private String telephone;
}
