package alvin.study.springboot.jpa.infra.entity;

import alvin.study.springboot.jpa.IntegrationTest;
import alvin.study.springboot.jpa.builder.DepartmentBuilder;
import alvin.study.springboot.jpa.builder.OrgBuilder;
import alvin.study.springboot.jpa.builder.UserBuilder;
import alvin.study.springboot.jpa.core.context.Context;
import alvin.study.springboot.jpa.core.context.CustomRequestAttributes;
import alvin.study.springboot.jpa.core.context.WebContext;
import alvin.study.springboot.jpa.infra.entity.common.TenantedEntityListener;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import lombok.SneakyThrows;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TenantingTest extends IntegrationTest {
    // 注入 JPA 实体管理器对象
    @PersistenceContext
    private EntityManager em;

    @Test
    void tenanting_shouldTenantedFilterWorked() {
        var orgs = new ArrayList<Org>();

        try (var ignore = beginTx(false)) {
            for (var i = 0; i < 10; i++) {
                var org = newBuilder(OrgBuilder.class).create();
                orgs.add(org);
            }

            for (var org : orgs) {
                newBuilder(UserBuilder.class).withOrgId(org.getId()).create();
            }
        }

        for (var org : orgs) {
            var context = CustomRequestAttributes.register(new WebContext());
            context.set(Context.ORG, org);

            try (var ignore = beginTx(true)) {
                var session = em.unwrap(Session.class);
                session.enableFilter("tenantFilter").setParameter("orgId", org.getId());

                var users = em.createQuery("from User", User.class).getResultList();
                then(users).hasSize(1);
            } finally {
                CustomRequestAttributes.unregister();
            }
        }
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
    @SuppressWarnings("all")
    void tenanting_shouldNotPersistEntityWithoutOrgContext() {
        // 清除当前请求上下文存储内容
        CustomRequestAttributes.unregister();

        // 持久化实体对象, 确认抛出 PersistenceException 异常
        var ex = (Throwable) assertThrows(PersistenceException.class, () -> {
            try (var ignore = beginTx(false)) {
                newBuilder(DepartmentBuilder.class).create();
            }
        });

        // 抛出的异常向上溯源, 直到找到 JdbcSQLIntegrityConstraintViolationException 异常
        while (ex != null && !(ex instanceof JdbcSQLIntegrityConstraintViolationException)) {
            ex = ex.getCause();
        }

        // 确认该异常是因为 orgId 字段为 null 导致
        then(ex.getMessage()).startsWith("NULL not allowed for column \"ORG_ID\"");

        // 本次持久化操作成功
        try (var ignore = beginTx(false)) {
            newBuilder(DepartmentBuilder.class)
                // 设置 orgId 字段
                .withOrgId(currentOrg().getId())
                .create();
        }
    }
}
