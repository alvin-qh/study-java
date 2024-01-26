package alvin.study.springboot.mybatis.infra.mapper;

import java.security.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.jdbc.SQL;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;

import alvin.study.springboot.mybatis.conf.ModelMapperConfig;
import alvin.study.springboot.mybatis.conf.MyBatisConfig;
import alvin.study.springboot.mybatis.infra.entity.Department;
import alvin.study.springboot.mybatis.infra.entity.Employee;
import alvin.study.springboot.mybatis.infra.entity.common.AuditedEntity;
import alvin.study.springboot.mybatis.infra.entity.common.TenantedEntity;
import alvin.study.springboot.mybatis.infra.handler.EntityFieldHandler;
import alvin.study.springboot.mybatis.infra.handler.TimestampTypeHandler;

/**
 * 该 Mapper 类型通过 {@code classpath:/mapper/DepartmentMapper.xml} 来设定要执行的 SQL 语句
 *
 * <p>
 * 要指定 mybatis 搜索 {@code xml} 文件的位置, 需要在 {@code classpath:/application.yml} 中指定
 * {@code mybatis(-plus).mapper-locations=classpath:mapper/*.xml} 配置项
 * </p>
 *
 * <p>
 * Mapper 类型的作用是将数据表和 Java 代码进行映射, 根据所给的 SQL 语句和名称映射规则, 对 Entity 对象进行增删改查操作.
 * 本例中演示了通过 mybatis-plus 提供的方式编写 Mapper 类型, 和原生 mybatis 方式不同, 需要继承
 * {@link BaseMapper} 接口来扩展 mybatis-plus 提供的扩展方法, 包括:
 * <ul>
 * <li>
 * {@link BaseMapper#insert(Object)} 插入一个实体对象
 * </li>
 * <li>
 * {@link BaseMapper#update(Object, com.baomidou.mybatisplus.core.conditions.Wrapper)
 * BaseMapper.update(Object, Wrapper)} 更新实体对象
 * </li>
 * <li>
 * {@link BaseMapper#exists(com.baomidou.mybatisplus.core.conditions.Wrapper)
 * BaseMapper.exists(Wrapper)} 查询实体对象是否存在
 * </li>
 * <li>
 * {@link BaseMapper#deleteById(java.io.Serializable)
 * BaseMapper.deleteById(Serializable)} 根据主键删除一个实体对象
 * </li>
 * <li>
 * {@link BaseMapper#selectById(java.io.Serializable)
 * BaseMapper.selectById(Serializable)} 根据主键删除一个实体对象
 * </li>
 * <li>
 * {@link BaseMapper#selectCount(com.baomidou.mybatisplus.core.conditions.Wrapper)
 * BaseMapper.selectCount(Wrapper)} 根据条件查询实体的数量
 * </li>
 * <li>
 * {@link BaseMapper#selectList(com.baomidou.mybatisplus.core.conditions.Wrapper)
 * BaseMapper.selectList(Wrapper)} 根据条件查询一个实体集合
 * </li>
 * <li>
 * 其它预定义查询方法请参考: {@link BaseMapper} 超类对象
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * 另外, 继承了 {@link BaseMapper} 后, 也为 Mapper 接口提供了"自动填充"的能力, 即对于一个实体对象进行增删改查操作时,
 * 可以通过一个 Handler 类型对要操作的对象做预处理, 以便完成一些公共操作而无需在每个 Mapper 中单独定义. 参考:
 * {@link EntityFieldHandler EntityFieldHandler}
 * 类型的定义, 该类型会对于所有继承自 {@link AuditedEntity
 * AuditedEntity} 或 {@link TenantedEntity
 * TenantedEntity} 类型的实体类型添加必要的字段值
 * </p>
 *
 * <p>
 * {@link CacheNamespace @CacheNamespace} 注解表示允许在当前的命名空间上启用缓存
 * <p>
 *
 * <p>
 * 要使 Mapper 类型生效, 和 Spring 集成, 需要配置 Mapper 类型的位置, 参见:
 * {@link org.mybatis.spring.annotation.MapperScan @MapperScan} 注解, 通过
 * {@code basePackages} 属性制定 Mapper 类型所在的包名
 * </p>
 */
@Mapper
@CacheNamespace
public interface DepartmentMapper extends BaseMapper<Department> {
    /**
     * 根据部门名称查询部门实体对象
     *
     * <p>
     * 具体的查询配置参考: {@code classpath:/mapper/DepartmentMapper.xml} 下
     * {@code select #id="selectByNameWithParentAndChildren"} 节点配置
     * </p>
     *
     * @param name 要查询的部门名称
     * @return 符合条件的部门实体
     */
    Optional<Department> selectByNameWithParentAndChildren(@Param("name") String name);

    /**
     * 根据部门名称查询部门及其相关员工
     *
     * <p>
     * 具体的查询配置参考: {@code classpath:/mapper/DepartmentMapper.xml} 下
     * {@code select #id="selectByNameWithEmployees"} 节点配置
     * </p>
     *
     * @param name 要查询的部门名称
     * @return 符合条件的部门实体
     */
    Optional<Department> selectByNameWithEmployees(@Param("name") String name);

    /**
     * 持久化一个部门实体对象
     *
     * <p>
     * 具体的查询配置参考: {@code classpath:/mapper/DepartmentMapper.xml} 下
     * {@code insert #id="insert"} 节点配置
     * </p>
     *
     * <p>
     * 该方法并不是必要的, 因为 {@link BaseMapper} 中已经定义了 {@link BaseMapper#insert(Object)} 方法,
     * 这里只是演示如何通过 xml 文件定义这类查询语句
     * </p>
     *
     * @param entity 要持久化的实体对象
     * @return 影响的行数
     */
    @Override
    int insert(Department entity);

    /**
     * 根据名称查询一个部门实体对象
     *
     * <p>
     * 可以通过 {@link Select @Select} 注解来书写 SQL 语句, 并通过 {@link ResultMap @ResultMap}
     * 注解来指定实体和数据表字段的映射关系
     * </p>
     *
     * <p>
     * 本例中使用到了 {@code classpath:/mapper/DepartmentMapper.xml} 下
     * {@code resultMap #id="departmentResultMap"}
     * </p>
     *
     * @param name 部门名称
     * @return 部门实体对象的 {@link Optional} 包装对象
     */
    @Select("""
        select id, org_id, name, deleted, created_by, created_at, updated_by, updated_at
        from department
        where name=#{name}
        """)
    @ResultMap("departmentResultMap")
    Optional<Department> selectByName(@Param("name") String name);

    /**
     * 根据传入的 {@link Employee} 实体查询其对应的 {@link Department} 实体集合
     *
     * <p>
     * 本方法演示了如何执行原生 SQL, {@link SqlRunner} 类型用于执行原生 SQL, 而 {@link SQL} 类型用于安全的产生原生
     * SQL 语句
     * </p>
     *
     * <p>
     * 注意, 在使用 {@link SqlRunner} 直接执行原生 SQL 语句获取结果时, 如果查询的结果集中含有
     * {@link org.apache.ibatis.type.JdbcType#TIMESTAMP JdbcType.TIMESTAMP} 类型字段,
     * 则会转换为 {@link Timestamp} 类型值, 在这个转换过程中, 可能会有时区转换的错误问题
     * </p>
     *
     * <p>
     * 在当期系统中, 时区定义为 UTC 时区, 所以写入数据库的值已经是 UTC 时区值, 但由于某些原因, MyBatis 会进行额外的时区转换,
     * 导致最终获取的结果中时间发生偏差
     * </p>
     *
     * <p>
     * 解决方法是, 通过全局的 {@link org.apache.ibatis.type.TypeHandler TypeHandler}
     * 对指定类型的字段进行转换, 从而消除原本的转换错误. 参考:
     * {@link TimestampTypeHandler TimestampTypeHandler}
     * 以及其配置方法 {@link MyBatisConfig#propertiesCustomizer()
     * MyBatisConfig.propertiesCustomizer()} 方法
     * </p>
     *
     * <p>
     * {@link SqlRunner} 执行原生 SQL 语句时, 返回的是基于 {@link Map} 对象的结果, 可以通过
     * {@link org.modelmapper.ModelMapper ModelMapper} 类型将其转换为目标实体类型 (例如本例中应该转化为
     * {@code List<Department>}) 类型对象, {@link org.modelmapper.ModelMapper
     * ModelMapper} 对象的设置请参考:
     * {@link ModelMapperConfig#modelMapper()
     * ModelMapperConfig.modelMapper()} 方法, 其中定义的类型转换器可以完成 {@code Map} 到实体对象的正确转换
     * </p>
     *
     * <p>
     * 要使用 {@link SqlRunner}, 需要在 {@code classpath:application.yml} 文件中设置
     * {@code mybatis-plus.global-config.enable-sql-runner = true}
     * </p>
     *
     * @param employee {@link Employee} 实体对象
     * @return 查询结果, 以一个 {@link Map} 对象的集合表示
     */
    default List<Map<String, Object>> selectByEmployee(Employee employee) {
        // 拼装原生 SQL 语句
        var sql = new SQL()
            .SELECT("d.id, d.org_id, d.name, d.deleted, d.created_at, d.created_by, d.updated_at, d.updated_by")
            .FROM("department d")
            .JOIN("department_employee de on d.id = de.department_id")
            .JOIN("employee e on e.deleted = 0 and e.id = de.employee_id")
            .WHERE("d.deleted = 0 and e.id = {0}");

        // 通过 SqlRunner 对象执行原生 SQL 语句, 返回 Map 类型结果
        var runner = SqlRunner.db(Department.class);
        return runner.selectList(sql.toString(), employee.getId());
    }
}
