package alvin.study.springboot.graphql.infra.mapper;

import java.util.Collection;

import org.apache.ibatis.annotations.Param;

/**
 * 定义通用的 {@link org.apache.ibatis.annotations.Mapper} 接口
 */
public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {
    /**
     * 删除所有记录
     *
     * @return 删除记录数
     */
    Integer deleteAll();

    /**
     * 批量插入记录
     *
     * @param entities 记录列表
     * @return 插入记录数
     */
    Integer insertAllBatch(@Param("list") Collection<T> entities);
}
