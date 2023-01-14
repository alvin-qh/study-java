package alvin.study.builder;

import java.util.concurrent.atomic.AtomicInteger;

import alvin.study.infra.entity.Employee;

/**
 * 职员实体构建器类
 */
public class EmployeeBuilder extends Builder<Employee> {
    private final static AtomicInteger SEQUENCE = new AtomicInteger();

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

        return fillOrgId(employee);
    }
}
