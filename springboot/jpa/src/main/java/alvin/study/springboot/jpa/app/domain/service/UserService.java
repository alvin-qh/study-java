package alvin.study.springboot.jpa.app.domain.service;

import alvin.study.springboot.jpa.core.context.Context;
import alvin.study.springboot.jpa.infra.entity.Org;
import alvin.study.springboot.jpa.infra.entity.User;
import alvin.study.springboot.jpa.infra.repository.UserRepository;
import alvin.study.springboot.jpa.util.security.PasswordUtil;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 和用户相关的服务类
 */
@Service
@RequiredArgsConstructor
public class UserService {
    // 注入用户存储对象
    private final UserRepository userRepository;

    // 注入密码工具对象
    private final PasswordUtil passwordUtil;

    // 注入上下文对象
    private final Context context;

    /**
     * 根据所给的条件, 查询 {@link User} 实体的分页结果
     *
     * <p>
     * 要执行较为复杂的查询 (例如动态条件查询), 需要 {@link UserRepository} 接口继承
     * {@link org.springframework.data.jpa.repository.JpaSpecificationExecutor
     * JpaSpecificationExecutor} 接口, 该接口提供了一系列方法进行复杂查询, 并提供了分页和排序的能力.
     * </p>
     *
     * <p>
     * 本服务类使用了
     * {@link UserRepository#findAll(org.springframework.data.jpa.domain.Specification, Pageable)
     * UserRepository.findAll(Specification, Pageable)} 接口方法, 该方法由
     * {@link org.springframework.data.jpa.repository.JpaSpecificationExecutor
     * JpaSpecificationExecutor} 接口提供, 其两个参数分别表示:
     *
     * <ul>
     * <li>
     * 第一个参数为要给 {@link org.springframework.data.jpa.domain.Specification
     * Specification} 接口的对象, 需实现其
     * {@link org.springframework.data.jpa.domain.Specification#toPredicate(jakarta.persistence.criteria.Root,
     * jakarta.persistence.criteria.CriteriaQuery, jakarta.persistence.criteria.CriteriaBuilder)
     * Specification.toPredicate( Root , CriteriaQuery , CriteriaBuilder )} 方法;
     * </li>
     * <li>
     * 第二个参数为要给 {@link Pageable} 接口的对象, 表示分页信息, 可以通过
     * {@link org.springframework.data.domain.PageRequest#of(int, int)
     * PageRequest.of(int page, int size)} 方法简单获得, 表示起始页码 ({@code 0} 表示第 1 页) 和每页记录数
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 结果是一个带分页信息的 {@link User} 对象集合, 分页信息由 {@link Page} 类型对象表示, 包括:
     * <ul>
     * <li>
     * {@link Page#getNumber()} 获取查询结果所在的页码
     * </li>
     * <li>
     * {@link Page#getNumberOfElements()} 获取本次查询的记录数
     * </li>
     * <li>
     * {@link Page#getSize()} 获取要求的每页的记录数
     * </li>
     * <li>
     * {@link Page#getTotalElements()} 获取总记录数
     * </li>
     * <li>
     * {@link Page#getTotalPages()} 获取总页数
     * </li>
     * <li>
     * {@link Page#getContent()} 获取本次查询的结果记录集合
     * </li>
     * </ul>
     * </p>
     *
     * @param account  用户账号
     * @param password 用户密码
     * @param pageable 分页对象
     * @return 查询到的实体对象集合, 数量上满足分页要求
     */
    public Page<User> searchUsers(String account, String password, Pageable pageable) {
        // 获取上下文中存储的 Org 对象, 即组织
        var org = context.<Org>get(Context.ORG);

        // 调用 findAll 方法进行动态查询
        return userRepository.findAll((root, query, cb) -> {
            // 创建查询条件对象, conjunction 方法创建一个永真的查询条件, 即此时的的查询条件为 where 1=1
            // 之所以这么做, 是为了方便后续继续连接各种查询条件
            // var predicate = cb.conjunction();

            // 设置 where 条件: orgId = :orgId
            var predicate = cb.equal(root.get("orgId"), org.getId());

            // 如果 account 参数存在, 则连接 account=:account 查询条件
            if (!Strings.isNullOrEmpty(account)) {
                predicate = cb.and(predicate, cb.equal(root.get("account"), account));
            }

            // 如果 password 参数存在, 则连接 password=:password 查询条件
            if (!Strings.isNullOrEmpty(password)) {
                // 对明文密码加密后作为查询表达式的值
                predicate = cb.and(predicate, cb.equal(root.get("password"), encryptPassword(password)));
            }

            // 返回 where 条件 + order by 的组合结果
            // 如果只需要 where 条件即可, 则可直接返回 predicate 变量
            return query
                // 将查询条件放入 query 的 where 子句中
                .where(predicate)
                // 设置排序表达式
                .orderBy(cb.desc(root.get("id")))
                // 返回复合查询条件
                .getRestriction();
        }, pageable);
    }

    /**
     * 对明文密码进行加密
     *
     * @param password 明文密码
     * @return 密文密码
     */
    @SneakyThrows
    private String encryptPassword(String password) {
        return passwordUtil.encrypt(password);
    }
}
