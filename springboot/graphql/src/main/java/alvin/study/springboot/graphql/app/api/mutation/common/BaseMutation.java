package alvin.study.springboot.graphql.app.api.mutation.common;

import graphql.Assert;
import graphql.GraphQLContext;

import alvin.study.springboot.graphql.core.context.ContextKey;
import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.entity.User;
import alvin.study.springboot.graphql.infra.entity.common.AuditedEntity;

public abstract class BaseMutation {
    protected static <T extends AuditedEntity> T completeAuditedEntity(T entity, GraphQLContext ctx) {
        var loginUser = ctx.<User>get(ContextKey.USER);
        if (entity.getCreatedBy() == null) {
            entity.setCreatedBy(loginUser.getId());
        }
        entity.setUpdatedBy(loginUser.getId());

        var org = ctx.<Org>get(ContextKey.ORG);
        if (entity.getOrgId() == null) {
            entity.setOrgId(org.getId());
        } else {
            Assert.assertFalse(entity.getOrgId() == org.getId());
        }
        return entity;
    }
}
