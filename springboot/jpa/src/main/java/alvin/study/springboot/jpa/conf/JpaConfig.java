package alvin.study.springboot.jpa.conf;

import java.util.Optional;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.jpa.core.context.Context;
import alvin.study.springboot.jpa.infra.entity.User;
import alvin.study.springboot.jpa.infra.entity.common.AuditedEntity;

/**
 * JAP 相关配置类
 *
 * <p>
 * {@link EntityScan @EntityScan} 注解表示要扫描所有注解为
 * {@link jakarta.persistence.Entity @Entity} 的实体类型的包范围
 * </p>
 *
 * <p>
 * 该类型实现了
 * {@link AuditorAware#getCurrentAuditor()} 接口, 用于为数据实体对象中的审计字段获取相关的用户
 * {@code id}, 即 {@code created_by} 和 {@code updated_by} 字段的值
 * </p>
 *
 * <p>
 * 从上下文对象 {@link Context} 中获取当前登录用户的 {@code id}, 即可作为审计用户 {@code id}
 * </p>
 *
 * <p>
 * {@link EnableTransactionManagement @EnableTransactionManagement}
 * 注解表示启动默认的事务管理器
 * </p>
 *
 * @see AuditedEntity AuditedEntity
 * @see org.springframework.data.jpa.domain.support.AuditingEntityListener
 *      AuditingEntityListener
 */
@Configuration("conf/jpa")
@EntityScan(basePackages = {
    "alvin.study.springboot.jpa.infra.entity"
})
@EnableJpaAuditing
@RequiredArgsConstructor
@EnableTransactionManagement
public class JpaConfig implements AuditorAware<Long> {
    // 注入每次请求的上下文对象
    private final Context context;

    /**
     * 获取审计字段 (即数据的创建人 {@code created_by} 和修改人 {@code updated_by}) 对应的用户 {@code id}
     */
    @Override
    public Optional<Long> getCurrentAuditor() {
        // 如果不存在上下文, 则返回空
        if (context == null) {
            return Optional.empty();
        }

        try {
            // 获取上下文中的登陆用户信息
            User user = context.getOrDefault(Context.USER, null);
            if (user == null) {
                return Optional.empty();
            }
            // 返回用户 id 作为审计人员 id
            return Optional.ofNullable(user.getId());
        } catch (BeanCreationException ignore) {
            return Optional.empty();
        }
    }
}
