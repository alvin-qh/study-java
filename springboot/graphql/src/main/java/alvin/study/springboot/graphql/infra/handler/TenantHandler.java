package alvin.study.springboot.graphql.infra.handler;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;

import lombok.RequiredArgsConstructor;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;

import alvin.study.springboot.graphql.app.context.ContextKey;
import alvin.study.springboot.graphql.core.context.ContextHolder;
import alvin.study.springboot.graphql.infra.entity.Org;

/**
 * 租户处理器, 用于为 SQL 语句添加租户信息
 */
@Component
@RequiredArgsConstructor
public class TenantHandler implements TenantLineHandler {
    @Override
    public Expression getTenantId() {
        var ctx = ContextHolder.getValue();
        if (ctx == null) {
            return new NullValue();
        }
        var org = ctx.<Org>get(ContextKey.KEY_ORG);
        return new LongValue(org.getId());
    }

    @Override
    public String getTenantIdColumn() { return "org_id"; }

    @Override
    public boolean ignoreTable(String tableName) {
        switch (tableName) {
        case "org":
            return true;
        default:
            if (ContextHolder.getValue() == null) {
                return true;
            }
            return false;
        }
    }
}
