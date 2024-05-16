package alvin.study.springboot.jpa.infra.repository;

import alvin.study.springboot.jpa.infra.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 演示如何通过 JPQL 来执行更新和删除操作的存储操作接口类
 *
 * <p>
 * 除了由 Spring JPA 自动生成 SQL 外, 对于一些特定情况, 可以通过 JPQL 自定义查询, 修改和删除实体的操作
 * </p>
 *
 * <p>
 * 自定义的 JPQL 语句通过 {@link Query @Query} 注解设置, {@code value} 为标准的 JPQL 语句
 * </p>
 *
 * <p>
 * 对于要进行 {@code update} 或 {@code delete} 操作的情况, 还需要额外增加
 * {@link Modifying @Modifying} 注解, 表示被注解的方法需要对数据进行修改
 * </p>
 */
public interface ModifyingRepository extends CrudRepository<User, Long> {
    /**
     * 通过 {@code User.account} 字段查询 {@link User} 对象的集合
     *
     * <p>
     * JPQL 中的 {@code ?1} 表示一个参数占位符, 方法执行时会将第一个参数 {@code account} 的值取代 {@code ?1}
     * 占位符
     * </p>
     *
     * @param account {@code User.account} 字段值
     * @return 包含符合查询条件的 {@link User} 实体对象的集合
     */
    @Query("from User u where u.account=?1")
    List<User> findAllByAccount(String account);

    /**
     * 通过匹配 {@code User.id} 字段的值, 来更新对应对象的 {@code User.password} 字段
     *
     * <p>
     * JPQL 中的 {@code ?1, ?2} 表示两个参数占位符, 方法执行时会将第一个参数 {@code id} 的值取代 {@code ?1},
     * 第二个参数 {@code password} 取代 {@code ?2}
     * </p>
     *
     * <p>
     * 对于要进行 {@code update} 或 {@code delete} 操作的情况, 还需要额外增加
     * {@link Modifying @Modifying} 注解, 表示被注解的方法需要对数据进行修改
     * </p>
     *
     * @param id       {@code User.id} 的值
     * @param password 要更新的 {@code User.password} 字段值
     */
    @Query("update User u set u.password=?2 where id=?1")
    @Modifying
    void updatePasswordById(Long id, String password);

    /**
     * 通过匹配 {@code User.account} 字段的值来删除对应的 {@link User} 实体对象
     *
     * <p>
     * {@code :account} 是一个命名占位符, 当方法执行时, 会用 {@link Param @Param}
     * 注解标记为相同名称的参数值取代该占位符
     * </p>
     *
     * <p>
     * 注意: {@link User} 对象本身通过
     * {@link org.hibernate.annotations.SQLDelete @SQLDelete} 注解指定了软删除规则, 即通过
     * {@code deleted} 字段来标识一个实体是否被删除.
     * 但本方法标注的 HQL 明确表示要删除 {@code User} 实体, 所以通过该方法执行的删除操作并不是软删除.
     * 如果仍需要进行软删除, 则不应该使用 {@code delete} 语句, 改为 {@code update} 语句对 {@code deleted}
     * 字段进行操作即可
     * </p>
     *
     * <p>
     * 对于要进行 {@code update} 或 {@code delete} 操作的情况, 还需要额外增加
     * {@link Modifying @Modifying} 注解, 表示被注解的方法需要对数据进行修改
     * </p>
     *
     * @param account {@code User.account} 的值
     */
    @Query("delete User u where u.account=:account")
    @Modifying
    void deleteAllByAccount(@Param("account") String account);
}
