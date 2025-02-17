package alvin.study.springboot.jpa.infra.repository;

import alvin.study.springboot.jpa.infra.entity.Org;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * 组织实体 {@link Org} 类型的存储操作接口类
 *
 * <p>
 * JPA 可以根据接口方法名自动产生所需的 SQL, 需要令当前接口继承 {@link CrudRepository} 接口, 表示当前接口用于增删查改操作
 * </p>
 *
 * <p>
 * {@link CrudRepository} 接口的泛型参数表示要操作的实体类型和实体类型的主键类型
 * </p>
 */
public interface OrgRepository extends CrudRepository<Org, Long> {
    /**
     * 依据组织名称查询组织实体
     *
     * @param name 组织名
     * @return {@link Optional} 对象, {@link Optional#isPresent()} 方法返回 {@code true}
     *         表示查询成功
     */
    Optional<Org> findByName(String name);
}
