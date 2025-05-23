package alvin.study.springboot.mybatis.builder;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.springboot.mybatis.infra.entity.Employee;
import alvin.study.springboot.mybatis.infra.entity.EmployeeInfo;
import alvin.study.springboot.mybatis.infra.entity.Gender;
import alvin.study.springboot.mybatis.infra.mapper.EmployeeMapper;

/**
 * 职员实体构建器类
 */
public class EmployeeBuilder extends Builder<Employee> {
    private final static AtomicInteger SEQUENCE = new AtomicInteger();
    private final EmployeeInfo info = new EmployeeInfo()
            .setGender(Gender.MALE)
            .setBirthday(LocalDate.parse("1981-03-17"))
            .setTelephone("13999999011");
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

        return fillOrgId(employee);
    }

    @Override
    public Employee create() {
        var entity = build();
        mapper.insert(entity);
        return entity;
    }
}
