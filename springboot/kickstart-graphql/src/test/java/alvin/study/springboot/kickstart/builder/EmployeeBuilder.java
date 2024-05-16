package alvin.study.springboot.kickstart.builder;

import alvin.study.springboot.kickstart.infra.entity.Employee;
import alvin.study.springboot.kickstart.infra.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 职员实体构建器类
 */
public class EmployeeBuilder extends Builder<Employee> {
    private final static AtomicInteger SEQUENCE = new AtomicInteger();

    @Autowired
    private EmployeeMapper mapper;

    private String name = "Employee" + SEQUENCE.incrementAndGet();
    private String email = String.format("employee%d@fakemail.com", SEQUENCE.incrementAndGet());
    private String title = "Staff";
    private Map<String, ?> info = Map.of();

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

    /**
     * 设置员工信息
     */
    public EmployeeBuilder withInfo(Map<String, ?> info) {
        this.info = info;
        return this;
    }

    @Override
    public Employee build() {
        var employee = new Employee();
        employee.setName(name);
        employee.setEmail(email);
        employee.setTitle(title);
        employee.setInfo(info);

        return fillOrgId(employee);
    }

    @Override
    public Employee create() {
        var entity = build();
        mapper.insert(entity);
        return entity;
    }
}
