package alvin.study.springboot.mybatis.infra.handler;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;

import lombok.RequiredArgsConstructor;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

import alvin.study.springboot.mybatis.core.context.Context;
import alvin.study.springboot.mybatis.infra.entity.Org;

/**
 * 租户处理器, 用于为 SQL 语句添加租户信息
 */
@Component
@RequiredArgsConstructor
public class TenantHandler implements TenantLineHandler {
    private final Context context;

    @Override
    public Expression getTenantId() {
        var org = context.<Org>get(Context.ORG);
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
            return false;
        }
    }
}
