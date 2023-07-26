package alvin.study.springboot.jpa.builder;

import alvin.study.springboot.jpa.infra.entity.Employee;

/**
 * 职员实体构建器类
 */
public class EmployeeBuilder extends Builder<Employee> {
    private String name = FAKER.name().fullName();
    private String email = FAKER.internet().emailAddress();
    private String title = FAKER.job().title();

    /**
     * 设置职员姓名
     */
    public EmployeeBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置职员邮件
     */
    public EmployeeBuilder email(String email) {
        this.email = email;
        return this;
    }

    /**
     * 设置职员职称
     */
    public EmployeeBuilder title(String title) {
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
