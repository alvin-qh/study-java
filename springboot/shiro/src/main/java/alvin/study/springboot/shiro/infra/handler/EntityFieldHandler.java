package alvin.study.springboot.shiro.infra.handler;

import java.time.Instant;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.shiro.util.ThreadContext;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.shiro.infra.entity.User;

/**
 * 声明实体字段的自动填充处理类
 *
 * <p>
 * 自定填充指的是在实体进行 {@code Insert} 或 {@code Update} 操作时自动对指定字段进行填充
 * </p>
 *
 * <p>
 * 要触发自动填充需要满足以下两个条件:
 * <ol>
 * <li>
 * 实体类型要填充的字段需要标记
 * {@link com.baomidou.mybatisplus.annotation.TableField @TableField} 注解并且设置其
 * {@code fill} 属性, 参考 {@link com.baomidou.mybatisplus.annotation.FieldStrategy
 * FieldStrategy} 枚举
 * </li>
 * <li>
 * 操作实体的 Mapper 类需要继承 {@link com.baomidou.mybatisplus.core.mapper.BaseMapper
 * BaseMapper} 类型
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * 处理字段自动填充的方法也有两个, 即负责对插入操作进行处理的
 * {@link MetaObjectHandler#insertFill(MetaObject)} 方法以及对更新操作进行处理的
 * {@link MetaObjectHandler#updateFill(MetaObject)} 方法
 * </p>
 */
@Component
@RequiredArgsConstructor
public class EntityFieldHandler implements MetaObjectHandler {
    private static final String FIELD_UPDATED_BY = "updatedBy";
    private static final String FIELD_UPDATED_AT = "updatedAt";
    private static final String FIELD_CREATED_BY = "createdBy";
    private static final String FIELD_CREATED_AT = "createdAt";

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户 {@link User} 对象
     */
    private static User getCurrentLoginUser() {
        var subject = ThreadContext.getSubject();
        if (subject == null || subject.getPrincipals() == null) {
            return null;
        }
        return (User) subject.getPrincipals().getPrimaryPrincipal();
    }

    /**
     * 对插入的实体对象字段进行填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        var now = Instant.now();

        // 对待插入的对象填充 createdAt 和 updatedAt 两个字段设置为当前时间
        strictInsertFill(metaObject, FIELD_CREATED_AT, Instant.class, now);
        strictInsertFill(metaObject, FIELD_UPDATED_AT, Instant.class, now);

        // 获取实体对象的 createdBy 字段值
        var createdBy = getFieldValByName(FIELD_CREATED_BY, metaObject);
        if (createdBy == null || createdBy.equals(0L)) {
            // 如果实体对象未设置 createdBy 字段值, 则将其设置为当前登录用户 id 值
            var user = getCurrentLoginUser();
            if (user != null && user.getId() != null) {
                strictInsertFill(metaObject, FIELD_CREATED_BY, Long.class, user.getId());
            }
        }

        // 获取实体对象的 updatedBy 字段值
        var updatedBy = getFieldValByName(FIELD_UPDATED_BY, metaObject);
        if (updatedBy == null || updatedBy.equals(0L)) {
            // 如果实体对象未设置 updatedBy 字段值, 则将其设置为当前登录用户 id 值
            var user = getCurrentLoginUser();
            if (user != null && user.getId() != null) {
                strictInsertFill(metaObject, FIELD_UPDATED_BY, Long.class, user.getId());
            }
        }
    }

    /**
     * 对更新的实体对象字段进行填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        var now = Instant.now();
        // 将实体的 updatedAt 字段设置为当前时间
        strictUpdateFill(metaObject, FIELD_UPDATED_AT, Instant.class, now);

        var user = getCurrentLoginUser();
        if (user != null && user.getId() != null) {
            // 则实体的 updatedBy 字段设置为当前登录用户 id 值
            strictUpdateFill(metaObject, FIELD_UPDATED_BY, Long.class, user.getId());
        }
    }
}
