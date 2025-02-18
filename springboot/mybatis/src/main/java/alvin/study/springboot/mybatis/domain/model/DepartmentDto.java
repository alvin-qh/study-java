package alvin.study.springboot.mybatis.domain.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
