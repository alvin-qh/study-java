package alvin.study.springboot.jpa.app.domain.service;

import jakarta.persistence.criteria.Join;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.jpa.core.context.Context;
import alvin.study.springboot.jpa.infra.entity.Department;
import alvin.study.springboot.jpa.infra.entity.Org;
import alvin.study.springboot.jpa.infra.repository.DepartmentRepository;

/**
 * 和用户相关的服务类
 */
@Service
@RequiredArgsConstructor
public class DepartmentService {
    // 注入部门存储对象
    private final DepartmentRepository departmentRepository;

    // 注入上下文对象
    private final Context context;

    /**
     * 根据所给的条件, 查询 {@link Department} 实体的分页结果
     *
     * <p>
     * 要执行较为复杂的查询 (例如动态条件查询), 需要 {@link DepartmentRepository} 接口继承
     * {@link org.springframework.data.jpa.repository.JpaSpecificationExecutor
     * JpaSpecificationExecutor} 接口, 该接口提供了一系列方法进行复杂查询, 并提供了分页和排序的能力.
     * </p>
     *
     * <p>
     * 本服务类使用了
     * {@link DepartmentRepository#findAll(org.springframework.data.jpa.domain.Specification, Pageable)
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
     * Specification.toPredicate(Root, CriteriaQuery, CriteriaBuilder)} 方法;
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
     * 结果是一个带分页信息的 {@link Department} 对象集合, 分页信息由 {@link Page} 类型对象表示, 包括:
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
     * @param name      要查询的部门名称前缀
     * @param childName 要查询的子部门名称前缀
     * @param pageable  分页信息
     * @return 查询结果 {@link Department} 的分页结果
     */
    @SuppressWarnings("unchecked")
    public Page<Department> searchSubDepartments(String name, String childName, Pageable pageable) {
        // 获取上下文中存储的 Org 对象, 即组织
        var org = context.<Org>get(Context.ORG);

        // 调用 repository 方法进行动态查询
        return departmentRepository.findAll((root, query, cb) -> {
            // 指定 fetch 操作 (即 join fetch)
            var fetch = root.<Department, Department>fetch("children");

            // 上一步返回的 Fetch 类型对象同时也是 Join 类型对象 (实现了多个接口)
            assert (fetch instanceof Join);
            // 转为 Join 类型对象
            var join = (Join<Department, Department>) fetch;

            // 产生一个空查询条件, 即 where 1=1, 便于后续组合条件
            // var predicate = cb.conjunction();

            // 设置 where 条件: orgId = :orgId
            var predicate = cb.equal(root.get("orgId"), org.getId());

            if (!Strings.isNullOrEmpty(name)) {
                // 增加 where 条件: name = :name + "%"
                predicate = cb.and(predicate, cb.like(root.get("name"), name + "%"));
            }

            if (!Strings.isNullOrEmpty(childName)) {
                // 增加 where 条件: children.name = :name + "%"
                predicate = cb.and(predicate, cb.like(join.get("name"), childName + "%"));
            }

            // 由于使用了 join fetch 操作, 所以 distinct 是必要的
            return query.distinct(true).where(predicate).getRestriction();
        }, pageable);
    }
}
