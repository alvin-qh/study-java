package alvin.study.springboot.mybatis.infra.mapper;

import alvin.study.springboot.mybatis.infra.entity.Employee;
import alvin.study.springboot.mybatis.infra.entity.common.AuditedEntity;
import alvin.study.springboot.mybatis.infra.entity.common.TenantedEntity;
import alvin.study.springboot.mybatis.infra.handler.EntityFieldHandler;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
public interface EmployeeMapper extends BaseMapper<Employee> {
    /**
     * 根据职员名称获取职员信息
     *
     * <p>
     * 具体的查询配置参考: {@code classpath:/mapper/EmployeeMapper.xml} 下
     * {@code select #id="selectByNameWithDepartments"} 节点配置
     * </p>
     *
     * @param name 要查询的部门名称
     * @return 符合条件的部门实体
     */
    Optional<Employee> selectByNameWithDepartments(@Param("name") String name);

    /**
     * 通过一组员工姓名批量查询员工信息
     *
     * <p>
     * 具体的查询配置参考: {@code classpath:/mapper/EmployeeMapper.xml} 下
     * {@code select #id="selectBatchNames"} 节点配置
     * </p>
     *
     * @param names 员工姓名集合
     * @return 复合条件的员工列表
     */
    List<Employee> selectBatchNames(@Param("names") Collection<String> names);

    /**
     * 分页根据条件查询结果
     *
     * <p>
     * 要使用 MyBatis-Plus 提供的分页插件, 需要满足如下条件:
     * <ul>
     * <li>
     * 启用分页插件, 参考: {@code MyBatisConfig.interceptor()} 方法, 在其中注入了分页查询的 Inner Interceptor 对象
     * </li>
     * <ul>
     * 在查询方法定义中, 为第一个参数定义 {@link IPage} 接口的参数, 可以由
     * {@link com.baomidou.mybatisplus.extension.plugins.pagination.Page Page} 对象来定义
     * </ul>
     * <ul>
     * 查询方法的返回值可以为 {@link List} 或 {@link IPage} 类型, 后者可以返回完整的分页信息
     * </ul>
     * </ul>
     * </p>
     *
     * <p>
     * 对于一些更简单的情况, 可以通过
     * {@link BaseMapper#selectPage(IPage, com.baomidou.mybatisplus.core.conditions.Wrapper)
     * BaseMapper.selectPage(IPage, Wrapper)} 方法来进行查询, 通过
     * {@link com.baomidou.mybatisplus.core.toolkit.Wrappers#lambdaQuery(Class)
     * Wrappers.lambdaQuery(Class)} 方法动态产生查询条件
     * </p>
     *
     * <p>
     * 具体的查询配置参考: {@code classpath:/mapper/EmployeeMapper.xml} 下
     * {@code select #id="selectBySearch"} 节点配置
     * </p>
     *
     * @param page           {@link IPage} 分页对象
     * @param name           员工姓名条件
     * @param email          员工电子邮件条件
     * @param title          员工职称条件
     * @param departmentName 员工所属部门名称条件
     * @return 分页结果
     */
    IPage<Employee> selectBySearch(
        IPage<Employee> page,
        @Param("name") String name,
        @Param("email") String email,
        @Param("title") String title,
        @Param("departmentName") String departmentName);
}
