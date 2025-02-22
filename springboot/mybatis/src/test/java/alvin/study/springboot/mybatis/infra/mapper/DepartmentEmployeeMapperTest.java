package alvin.study.springboot.mybatis.infra.mapper;

import static org.assertj.core.api.BDDAssertions.then;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.mybatis.IntegrationTest;
import alvin.study.springboot.mybatis.builder.DepartmentBuilder;
import alvin.study.springboot.mybatis.builder.DepartmentEmployeeBuilder;
import alvin.study.springboot.mybatis.builder.EmployeeBuilder;
import alvin.study.springboot.mybatis.infra.entity.DepartmentEmployee;

/**
 * 测试 {@link DepartmentEmployeeMapper} 类型
 */
class DepartmentEmployeeMapperTest extends IntegrationTest {
    // 注入 DepartmentEmployeeMapper 对象
    @Autowired
    private DepartmentEmployeeMapper mapper;

    /**
     * 测试通过 {@link com.baomidou.mybatisplus.annotation.Version @Version}
     * 注解的字段对实体进行乐观锁控制
     *
     * <p>
     * 乐观锁的使用参考 {@link DepartmentEmployee} 实体的 {@code DepartmentEmployee.version} 字段
     * </p>
     */
    @Test
    @Transactional
    void version_shouldVersionFieldWorked() {
        // 创建职员实体
        var employee = newBuilder(EmployeeBuilder.class).create();

        // 创建两个部门实体
        var department1 = newBuilder(DepartmentBuilder.class).create();
        var department2 = newBuilder(DepartmentBuilder.class).create();

        // 设置部门职员关系
        var relationship = newBuilder(DepartmentEmployeeBuilder.class)
                .withDepartmentId(department1.getId())
                .withEmployeeId(employee.getId())
                .create();

        // 根据 id 获取部门职员关系, 查询两次
        var relationship1 = mapper.selectById(relationship.getId());
        clearSessionCache(); // 注意要清理一次缓存, 否则两次查询将获得同一个对象

        var relationship2 = mapper.selectById(relationship.getId());
        then(relationship1).isNotSameAs(relationship2);

        // 确认此时版本号为 0, 表示尚未进行更新操作
        then(relationship1.getVersion()).isZero();
        then(relationship2.getVersion()).isZero();

        // 对 relationship1 对象进行更新, 此时 version = 0 字段和数据表相符, 更新成功
        relationship1.setDepartmentId(department2.getId());
        var rows = mapper.updateById(relationship1);
        then(rows).isOne();
        // 此时版本号 +1, 表示实体已经被更新一次
        then(relationship1.getVersion()).isOne();

        // 对 relationship2 对象进行更新, 此时 version = 0 字段和数据表不相符, 更新失败
        relationship2.setDepartmentId(department2.getId());
        rows = mapper.updateById(relationship2);
        then(rows).isZero();
    }
}
