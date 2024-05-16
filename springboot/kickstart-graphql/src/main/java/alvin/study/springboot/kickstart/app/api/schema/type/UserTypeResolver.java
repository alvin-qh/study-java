package alvin.study.springboot.kickstart.app.api.schema.type;

import alvin.study.springboot.kickstart.app.api.schema.type.common.AuditedResolver;
import alvin.study.springboot.kickstart.app.api.schema.type.common.TenantedResolver;
import alvin.study.springboot.kickstart.core.graphql.annotation.Resolver;

/**
 * 对 {@link UserType} 类型补充 {@code createdByUser}, {@code updatedByUser} 以及
 * {@code org} 字段
 */
@Resolver
public class UserTypeResolver implements AuditedResolver<UserType>, TenantedResolver<UserType> { }
