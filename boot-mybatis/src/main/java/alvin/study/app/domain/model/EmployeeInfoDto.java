package alvin.study.app.domain.model;

import java.io.Serializable;
import java.time.LocalDate;

import alvin.study.infra.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
