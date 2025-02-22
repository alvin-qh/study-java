package alvin.study.springboot.graphql.app.service;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.infra.entity.Org;
import alvin.study.springboot.graphql.infra.mapper.OrgMapper;

/**
 * 组织服务类
 */
@Component
@RequiredArgsConstructor
public class OrgService {
    /**
     * 注入 {@link OrgMapper} 类型
     */
    private final OrgMapper orgMapper;

    /**
     * 根据组织 id 名查询用户信息
     *
     * @param id 组织 id
     * @return 组织实体的 {@link Optional} 包装对象
     */
    @Transactional(readOnly = true)
    public Optional<Org> findById(long id) {
        return Optional.ofNullable(orgMapper.selectById(id));
    }

    /**
     * 创建一个 {@link Org} 实体对象
     *
     * @param org {@link Org} 对象
     */
    @Transactional
    public void create(Org org) {
        orgMapper.insert(org);
    }

    /**
     * 更新一个 {@link Org} 实体对象
     *
     * @param id  组织 id
     * @param org {@link Org} 对象
     */
    @Transactional
    public Optional<Org> update(long id, Org org) {
        var originalOrg = orgMapper.selectById(id);
        if (originalOrg == null) {
            return Optional.empty();
        }

        originalOrg.setName(org.getName());

        if (orgMapper.updateById(originalOrg) > 0) {
            return Optional.of(originalOrg);
        }

        return Optional.empty();
    }

    /**
     * 删除一个 {@link Org} 实体
     *
     * @param id 实体 id
     * @return 是否删除
     * @return 是否删除
     */
    @Transactional
    public boolean delete(long id) {
        return orgMapper.deleteById(id) > 0;
    }
}
