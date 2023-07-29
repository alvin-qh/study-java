/**
 * 通过 {@link org.hibernate.annotations.FilterDef @FilterDef} 注解, 定义过滤器的名称和用于过滤的参数名和类型
 *
 * <p>
 * 在 {@code package-info.java} 中定义, 即在当前包下的所有类型都应用该注解
 * </p>
 *
 * <p>
 * 如果要同时定义多个 {@link org.hibernate.annotations.FilterDef @FilterDef} 注解, 可以通过
 * {@link org.hibernate.annotations.FilterDefs @FilterDefs} 注解一次性定义
 * </p>
 */

// @FilterDefs({
//     @FilterDef(name = "tenantFilter", parameters = { @ParamDef(name = "orgId", type = Long.class) })
// })
@FilterDef(name = "tenantFilter", parameters = { @ParamDef(name = "orgId", type = Long.class) })
package alvin.study.springboot.jpa.infra.entity;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
