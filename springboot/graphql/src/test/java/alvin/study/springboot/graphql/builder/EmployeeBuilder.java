package alvin.study.springboot.graphql.builder;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.springboot.graphql.infra.entity.Employee;
import alvin.study.springboot.graphql.infra.mapper.EmployeeMapper;

/**
 * 职员实体构建器类
 */
public class EmployeeBuilder extends Builder<Employee> {
    private final static AtomicInteger SEQUENCE = new AtomicInteger();

    private final Map<String, Object> info = Map.of(
        "gender", "MALE",
        "birthday", LocalDate.parse("1981-03-17"),
        "telephone", "13999999011");

    @Autowired
    private EmployeeMapper mapper;

    private String name = "Employee" + SEQUENCE.incrementAndGet();
    private String email = String.format("employee%d@fakemail.com", SEQUENCE.incrementAndGet());
    private String title = "Staff";

    /**
     * 设置职员姓名
     */
    public EmployeeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置职员邮件
     */
    public EmployeeBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    /**
     * 设置职员职称
     */
    public EmployeeBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Employee build() {
        var employee = new Employee();
        employee.setName(name);
        employee.setEmail(email);
        employee.setTitle(title);
        employee.setInfo(info);

        return complete(employee);
    }

    @Override
    public Employee create() {
        var entity = build();
        mapper.insert(entity);
        return entity;
    }
}
