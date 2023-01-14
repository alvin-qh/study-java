package alvin.study.infra.converter;

import alvin.study.infra.converter.common.JacksonConverter;
import alvin.study.infra.model.EmployeeInfo;

/**
 * 将 {@link EmployeeInfo} 和 JSON 互转的转换类型
 */
public class EmployeeInfoConverter extends JacksonConverter<EmployeeInfo> {
    /**
     * 获取 Record 字段类型
     */
    @Override
    public Class<EmployeeInfo> toType() {
        return EmployeeInfo.class;
    }
}
