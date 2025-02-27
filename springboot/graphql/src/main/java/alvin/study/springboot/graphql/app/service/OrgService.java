package alvin.study.springboot.graphql.app.service;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.core.exception.InputException;
import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.mapper.OrgMapper;

/**
 * 组织服务类, 用于 {@link Org} 类型数据操作
 */
@Component
@RequiredArgsConstructor
public class OrgService {
    private final OrgMapper orgMapper;

    /**
     * 根据组织 {@code ID} 查询 {@link Org} 类型组织实体对象
     *
     * @param id 组织 {@code ID}
     * @return {@link Org} 类型组织实体对象的 {@link Optional} 包装对象
     */
    @Transactional(readOnly = true)
    public Org findById(long id) {
        return Optional.ofNullable(orgMapper.selectById(id))
                .orElseThrow(() -> new InputException("org_not_exist"));
    }

    /**
     * 创建 {@link Org} 类型组织实体对象
     *
     * @param org {@link Org} 类型组织实体对象
     */
    @Transactional
    public void create(Org org) {
        orgMapper.insert(org);
    }

    /**
     * 更新 {@link Org} 类型组织实体对象
     *
     * @param id  组织 {@code ID}
     * @param org {@link Org} 类型组织实体对象的 {@link Optional} 包装对象
     */
    @Transactional
    public void update(Org org) {
        if (orgMapper.updateById(org) == 0) {
            throw new InputException("org_not_exist");
        }
    }

    /**
     * 删除 {@link Org} 类型组织实体
     *
     * @param id 组织实体 {@code ID} 值
     * @return {@code true} 表示删除成功, {@code false} 表示删除失败
     */
    @Transactional
    public boolean delete(long id) {
        return orgMapper.deleteById(id) > 0;
    }
}
