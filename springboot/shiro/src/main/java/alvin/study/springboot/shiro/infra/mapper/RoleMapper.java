package alvin.study.springboot.shiro.infra.mapper;

import alvin.study.springboot.shiro.infra.entity.Role;
import alvin.study.springboot.shiro.infra.entity.common.AuditedEntity;
import alvin.study.springboot.shiro.infra.handler.EntityFieldHandler;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 对 {@link Role} 实体进行操作的 Mapper 类型
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
 * AuditedEntity} 类型的实体类型添加必要的字段值
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
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 根据用户 id 查询用户的角色
     *
     * @param userId 用户 id
     * @return 角色列表
     */
    @Select("""
        select r.id, r.name, r.created_at, r.updated_at, r.created_by, r.updated_by
        from role r
        join role_grant rg on rg.role_id = r.id
        where rg.user_or_group_id = #{userId} and rg.type = 'user'
        """)
    List<Role> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户组 id 查询用户的角色
     *
     * @param groupIds 用户组 id
     * @return 角色列表
     */
    @Select("""
        <script>
            <![CDATA[
            select r.id, r.name, r.created_at, r.updated_at, r.created_by, r.updated_by
            from role r
            join role_grant rg on rg.role_id = r.id
            where rg.user_or_group_id in
            ]]>
            <foreach collection="groupIds" item="id" open="(" close=")" separator=",">
                #{id}
            </foreach>
            <![CDATA[
            and rg.type = 'group'
            ]]>
        </script>
        """)
    List<Role> selectByGroupIds(@Param("groupIds") Collection<Long> groupIds);

    /**
     * 根据权限名称获取权限对象
     *
     * @param name 权限名称
     * @return 权限对象
     */
    @Select("""
        select id, name, created_at, updated_at, created_by, updated_by
        from role
        where name = #{name}
        """)
    Optional<Role> selectByName(@Param("name") String name);
}
