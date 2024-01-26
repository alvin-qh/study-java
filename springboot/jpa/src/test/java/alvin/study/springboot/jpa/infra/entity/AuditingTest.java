package alvin.study.springboot.jpa.infra.entity;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.springboot.jpa.IntegrationTest;
import alvin.study.springboot.jpa.builder.DepartmentBuilder;
import alvin.study.springboot.jpa.builder.UserBuilder;
import alvin.study.springboot.jpa.core.context.Context;
import alvin.study.springboot.jpa.core.context.CustomRequestAttributes;
import alvin.study.springboot.jpa.infra.entity.common.AuditedEntity;
import alvin.study.springboot.jpa.infra.entity.common.TenantedEntityListener;
import lombok.SneakyThrows;

/**
 * 测试实体类型的审计字段是否生效
 *
 * @see AuditedEntity
 * @see alvin.study.common.Context
 */
class AuditingTest extends IntegrationTest {
    // 注入请求上下文管理对象
    @Autowired
    private Context context;

    /**
     * 测试在请求上下文中 {@link alvin.study.common.Context Context} 对象生效的情况下,
     * 审计字段的情况
     *
     * <p>
     * 在超类 {@link IntegrationTest#beforeEach IntegrationTest.beforeEach}
     * 方法中, 为当前请求上下文注册了 {@link alvin.study.common.Context Context} 对象,
     * 并在对象中注册了 {@link User} 和 {@link Org} 两个对象, 表示当前登录的用户和其所在的组织 (租户)
     * </p>
     *
     * @see alvin.study.common.Context
     * @see CustomRequestAttributes#register(Context)
     */
    @Test
    void auditing_shouldAuditingWithContext() {
        // 确认注入的 context 对象即超类中产生的 currentContext() 返回的对象
        then(context).isEqualTo(currentContext());

        // 获取当前登录的用户和其组织对象
        var currentUser = (User) context.get(Context.USER);
        var currentOrg = (Org) context.get(Context.ORG);

        Department department;

        // 实例化一个新实体对象, 该实体对象具备审计字段支持
        try (var ignore = beginTx(false)) {
            department = newBuilder(DepartmentBuilder.class).create();
        }

        // 确认 4 个审计字段都被填充, 且和当前 User 和 Org 对象一致
        then(department.getCreatedAt()).isNotNull();
        then(department.getUpdatedAt()).isNotNull();
        then(department.getCreatedBy()).isEqualTo(currentUser.getId());
        then(department.getUpdatedBy()).isEqualTo(currentUser.getId());
        then(department.getOrgId()).isEqualTo(currentOrg.getId());
    }

    /**
     * 测试在更新实体对象时, 只会更新 {@code updatedAt} 和 {@code updatedBy} 字段
     *
     * <p>
     * 在超类 {@link AuditedEntity AuditedEntity} 实体类中,
     * {@code createdAt} 和 {@code createdBy} 两个字段注解为
     * {@code @Column(..., updatable = false)}, 表示不进行更新操作, 所以对实体进行更像操作时, 只会涉及到
     * {@code updatedAt} 和 {@code updatedBy} 这两个字段
     * </p>
     *
     * @see AuditedEntity
     * @see javax.persistence.Column#updatable()
     */
    @Test
    void auditing_shouldUpdateModifyUpdatedAt() throws Exception {
        Department department;

        // 在当前上下文中持久化实体对象
        try (var ignore = beginTx(false)) {
            department = newBuilder(DepartmentBuilder.class).create();
        }

        // 休眠 1 秒
        Thread.sleep(1000);

        User user;
        // 持久化一个新的用户实体
        try (var ignore = beginTx(false)) {
            user = newBuilder(UserBuilder.class).create();
        }

        // 将上下文中的用户对象换掉
        context.set(Context.USER, user);

        // 进行一次实体的更新操作
        try (var ignore = beginTx(false)) {
            department = refreshEntity(department);
            department.setName("HR");
        }

        // 确认实体的创建时间和更新时间不同, 相差超过 1 秒
        var duration = Duration.between(department.getCreatedAt(), department.getUpdatedAt());
        then(duration.getSeconds()).isGreaterThanOrEqualTo(1);

        // 确认实体的创建人和更新人 id 不同
        then(department.getCreatedBy()).isNotEqualTo(department.getUpdatedBy());
    }

    /**
     * 测试当 {@link alvin.study.common.Context Context} 对象不存在时, 实体对象操作的情况
     *
     * <p>
     * 按照
     * {@link TenantedEntityListener#touchForCreate(Object)
     * TenantedEntityListener.touchForCreate(Object)} 方法中的定义, 当持久化的实体的 {@code orgId}
     * 为 {@code null} 且 {@link alvin.study.common.Context Context} 对象中存在
     * {@link Org} 对象时, 会对实体的 {@code orgId} 进行设置, 设置为当前组织 (租户) id
     * </p>
     */
    @Test
    @SneakyThrows
    void auditing_shouldAuditingWithoutContext() {
        // 从请求上下文中删除登录用户信息
        context.remove(Context.USER);

        Department department;

        // 持久化实体对象
        try (var ignore = beginTx(false)) {
            department = newBuilder(DepartmentBuilder.class).create();
        }

        // 确认 createdBy 和 updatedBy 两个字段未被设置
        then(department.getCreatedBy()).isNull();
        then(department.getUpdatedBy()).isNull();
    }
}
