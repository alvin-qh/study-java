package alvin.study.springboot.jooq.infra.converter.common;

import org.jooq.Converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.base.Strings;

import lombok.SneakyThrows;

import alvin.study.springboot.jooq.util.bean.SpringBeanUtil;

/**
 * 将 JSON 类型和 Object 类型互转的转换类
 *
 * <p>
 * 该类型用于数据表为 JSON 类型字段, 且对应一个 Pojo 类型对象的情况
 * </p>
 */
public abstract class JacksonConverter<T> implements Converter<String, T> {
    /**
     * 将数据表字段类型值转为 Record 字段类型值
     */
    @Override
    @SneakyThrows
    public T from(String databaseObject) {
        if (Strings.isNullOrEmpty(databaseObject)) {
            return null;
        }

        var objectMapper = SpringBeanUtil.getBean(ObjectMapper.class);
        return objectMapper.readValue(databaseObject, toType());
    }

    /**
     * 将 Record 字段类型值转为数据表字段类型值
     */
    @Override
    @SneakyThrows
    public String to(Object userObject) {
        if (userObject == null) {
            return "";
        }

        var objectMapper = SpringBeanUtil.getBean(ObjectMapper.class);
        return objectMapper.writeValueAsString(userObject);
    }

    /**
     * 获取数据表字段类型
     */
    @Override
    public Class<String> fromType() {
        return String.class;
    }
}
