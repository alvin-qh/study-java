package alvin.study.springboot.graphql.infra.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class EmployeeDepartment implements Serializable {
    private Employee employee;
    private List<Department> departments;
}
