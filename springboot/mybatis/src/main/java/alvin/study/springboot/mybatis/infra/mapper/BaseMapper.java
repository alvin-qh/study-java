package alvin.study.springboot.mybatis.infra.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Collection;

@SuppressWarnings("MybatisXMapperMethodInspection")
public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {
    Integer deleteAll();

    Integer insertAllBatch(@Param("list") Collection<T> entities);
}
