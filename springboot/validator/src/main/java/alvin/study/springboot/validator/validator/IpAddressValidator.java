package alvin.study.springboot.validator.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.google.common.base.Strings;

/**
 * 和校验注解关联的校验执行类
 *
 * <p>
 * 校验类需实现 {@link ConstraintValidator} 接口, 其泛型参数为<code><校验注解类型, 要校验的字段类型></code>
 * </p>
 *
 * <p>
 * 本类型定义了对 {@link IpAddress @IpAddress} 注解进行校验, 且该注解注解的字段类型为 {@link String}
 * </p>
 */
class IpAddressValidator implements ConstraintValidator<IpAddress, String> {
    // 用于判断 IPv4 格式的正则表达式
    private static final Pattern PATTERN_IPV4 = Pattern
            .compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");

    // 用于判断 IPv6 格式的正则表达式
    private static final Pattern PATTERN_IPV6 = Pattern.compile("^([a-fA-F]{1,4}:){7}[a-fA-F]{1,4}$");

    // 定义此校验的注解对象
    private IpAddress annotation;

    /**
     * 初始化
     *
     * <p>
     * 用于字段 (或参数) 验证的注解会通过该函数传入. 如果需要获取注解上定义的属性值, 需要重写该方法并保持传入的注解对象
     * </p>
     *
     * @see jakarta.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(IpAddress constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    /**
     * 验证被注解的对象 (字段, 参数等) 是否正确
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 对空值不做校验
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }

        // 获取 ip 地址的版本号
        var ver = annotation.version();

        // 按 ipv4 格式校验
        if (ver == 4) {
            return checkAsIpv4(value);
        }

        // 按 ipv6 格式校验
        if (ver == 6) {
            return checkAsIpv6(value);
        }

        throw new IllegalArgumentException("version must be 4 or 6");
    }

    /**
     * 校验所给的值是否符合 ipv4 格式
     *
     * @param value 待校验的值
     * @return 是否正确
     */
    private boolean checkAsIpv4(String value) {
        var matcher = PATTERN_IPV4.matcher(value);
        try {
            if (!matcher.matches()) {
                return false;
            }

            for (int i = 1; i <= 4; i++) {
                int octet = Integer.parseInt(matcher.group(i));
                if (octet > 255) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 校验所给的值是否符合 ipv6 格式
     *
     * @param value 待校验的值
     * @return 是否正确
     */
    private boolean checkAsIpv6(String value) {
        var matcher = PATTERN_IPV6.matcher(value);
        return matcher.matches();
    }
}
