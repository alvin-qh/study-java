package alvin.study.springboot.mybatis.infra.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import alvin.study.springboot.mybatis.infra.entity.Org;

/**
 * 对 {@link Org} 实体进行操作的 Mapper 类型
 *
 * <p>
 * Mapper 类型的作用是将数据表和 Java 代码进行映射, 根据所给的 SQL 语句和名称映射规则, 对 Entity 对象进行增删改查操作.
 * 本例中演示了通过原生 mybatis 提供的方式编写 Mapper 类型
 * </p>
 *
 * <p>
 * Mapper 类型是一个接口类型, 只需定义方法和方法的注解, 无需实现方法, mybatis 会根据提供的信息自动产生 SQL 语句并执行
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
public interface OrgMapper {
    /**
     * 通过 {@code id} 值查询 {@link Org} 实体对象
     *
     * <p>
     * {@link Select @Select} 注解表示该方法是要执行一条查询 SQL 语句, 需要指定 {@code select} 语句
     * </p>
     *
     * <p>
     * SQL 语句中的 <code>#{param}</code> 表示一个名为 {@code param} 的参数, 该参数需要在接口方法的参数列表中提供,
     * 通过 {@link Param @Param} 注解提供匹配的参数名
     * </p>
     *
     * <p>
     * {@link Options @Options} 注解指定了数据库操作的配置, 包括:
     * <ul>
     * <li>{@code useCache = true} 表示本次查询会使用缓存</li>
     * </ul>
     * </p>
     *
     * @param id {@code id} 值, 即 {@link Org} 实体的主键
     * @return {@link Org} 对象的 {@link Optional} 对象
     */
    @Select("select id, name, created_at, updated_at from org where deleted = 0 and id = #{id}")
    @Options(useCache = true)
    Optional<Org> selectById(@Param("id") Long id);

    /**
     * 依据名称查询实体
     *
     * <p>
     * {@link Select @Select} 注解表示该方法是要执行一条查询 SQL 语句, 需要指定 {@code select} 语句
     * </p>
     *
     * <p>
     * SQL 语句中的 <code>#{param}</code> 表示一个名为 {@code param} 的参数, 该参数需要在接口方法的参数列表中提供,
     * 通过 {@link Param @Param} 注解提供匹配的参数名
     * </p>
     *
     * <p>
     * {@link Options @Options} 注解指定了数据库操作的配置, 包括:
     * <ul>
     * <li>{@code useCache = true} 表示本次查询会使用缓存</li>
     * </ul>
     * </p>
     *
     * @param name 组织名
     * @return {@link Optional} 对象, {@link Optional#isPresent()} 方法返回 {@code true}
     *         表示查询成功
     */
    @Select("select id, name, created_at, updated_at from org where deleted = 0 and name = #{name}")
    @Options(useCache = true)
    Optional<Org> selectByName(@Param("name") String name);

    /**
     * 插入一条 {@link Org} 实体对象到数据表
     *
     * <p>
     * {@link Insert @Insert} 注解表示该方法要执行一条插入数据语句, 需指定 {@code insert} 语句
     * </p>
     *
     * <p>
     * SQL 语句中的 <code>#{param}</code> 表示一个名为 {@code param} 的参数, 该参数需要在接口方法的参数列表中提供,
     * 通过 {@link Param @Param} 注解提供匹配的参数名. 如果传递的参数不具备 {@link Param @Param} 注解, 则 SQL
     * 语句中的 <code>#{param}</code> 参数表示一个对象属性名, 即认为方法参数传递的是一个对象
     * </p>
     *
     * <p>
     * {@link Options @Options} 注解指定了数据库操作的配置, 包括:
     * <ul>
     * <li>{@code useGeneratedKeys = true} 表示数据插入后将主键返回实体对象</li>
     * <li>{@code keyColumn = &quot;id&quot;} 表示和数据表主键对应的字段名</li>
     * <li>{@code keyProperty = &quot;id&quot;} 表示和实体对象对应的 {@code id} 属性名</li>
     * <li>{@code flushCache = FlushCachePolicy.TRUE} 表示操作完毕后刷新缓存</li>
     * </ul>
     * </p>
     *
     * @param entity {@link Org} 实体对象
     * @return 本次操作映像数据表的行数
     */
    @Insert("insert into org (name, deleted, created_at, updated_at) values (#{name}, 0, now(), now())")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id", flushCache = FlushCachePolicy.TRUE)
    int insert(Org entity);

    /**
     * 更新 {@link Org} 实体对象的变化到数据表
     *
     * <p>
     * {@link Update @Update} 注解表示该方法要执行一条更新数据语句, 需指定 {@code update} 语句
     * </p>
     *
     * <p>
     * SQL 语句中的 <code>#{param}</code> 表示一个名为 {@code param} 的参数, 该参数需要在接口方法的参数列表中提供,
     * 通过 {@link Param @Param} 注解提供匹配的参数名. 如果传递的参数不具备 {@link Param @Param} 注解, 则 SQL
     * 语句中的 <code>#{param}</code> 参数表示一个对象属性名, 即认为方法参数传递的是一个对象
     * </p>
     *
     * <p>
     * {@link Options @Options} 注解指定了数据库操作的配置, 包括:
     * <ul>
     * <li>{@code flushCache = FlushCachePolicy.TRUE} 表示操作完毕后刷新缓存</li>
     * </ul>
     * </p>
     *
     * @param entity {@link Org} 实体对象
     * @return 本次操作映像数据表的行数
     */
    @Update("update org set name=#{name}, updated_at=now() where id=#{id}")
    @Options(flushCache = FlushCachePolicy.TRUE)
    int update(Org entity);

    /**
     * 删除 {@link Org} 实体对象对应的数据表记录
     *
     * <p>
     * {@link Delete @Delete} 注解表示该方法要执行一条删除数据语句, 需指定 {@code delete} 语句
     * </p>
     *
     * <p>
     * SQL 语句中的 <code>#{param}</code> 表示一个名为 {@code param} 的参数, 该参数需要在接口方法的参数列表中提供,
     * 通过 {@link Param @Param} 注解提供匹配的参数名. 如果传递的参数不具备 {@link Param @Param} 注解, 则 SQL
     * 语句中的 <code>#{param}</code> 参数表示一个对象属性名, 即认为方法参数传递的是一个对象
     * </p>
     *
     * <p>
     * {@link Options @Options} 注解指定了数据库操作的配置, 包括:
     * <ul>
     * <li>{@code flushCache = FlushCachePolicy.TRUE} 表示操作完毕后刷新缓存</li>
     * </ul>
     * </p>
     *
     * @param entity {@link Org} 实体对象
     * @return 本次操作映像数据表的行数
     */
    @Delete("update org set deleted = 1 where id = #{id}")
    @Options(flushCache = FlushCachePolicy.TRUE)
    int delete(Org entity);
}
