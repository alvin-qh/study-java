package alvin.study.springboot.mybatis.infra.mapper;

import java.util.Collection;

import org.apache.ibatis.annotations.Param;

public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {
    Integer deleteAll();

    Integer insertAllBatch(@Param("list") Collection<T> entities);
}
