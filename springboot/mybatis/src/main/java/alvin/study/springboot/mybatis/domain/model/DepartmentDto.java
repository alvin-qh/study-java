package alvin.study.springboot.mybatis.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 部门的 DTO 类型
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto implements Serializable {
    /**
     * 部门 id
     */
    private Long id;

    /**
     * 部门名称
     */
    private String name;
}
