package alvin.study.springboot.spel.util;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.stereotype.Component;

/**
 * 模板解析器类型
 *
 * <p>
 * 所谓模板, 即可以同时包含表达式和其它字符, 例如 <code>"The name is: #{name}"</code>, 其中
 * <code>#{...}</code> 中包含的为表达式, 其余作为字符串原样输出
 * </p>
 */
@Component
@RequiredArgsConstructor
public class TemplatedExpressionParser {
    /**
     * 注入解析器对象
     *
     * <p>
     * 参考 {@code SpelConfig.expressionParser()} 方法
     * </p>
     */
    private final ExpressionParser parser;

    /**
     * 解析模板字符串
     *
     * <p>
     * 通过 {@link ParserContext#TEMPLATE_EXPRESSION} 参数定义了用于模板解析的对象, 模板配置如下:
     * </p>
     *
     * <pre>
     * public class TemplateParserContext {
     *     &#64;Override
     *     public boolean isTemplate() {
     *         // 返回 true 表示将输入的字符串作为模板而非表达式来看待
     *         return true;
     *     }
     *
     *     &#64;Override
     *     public String getExpressionPrefix() {
     *         // 模板中表示表达式的开始的标识符
     *         return "#{";
     *     }
     *
     *     &#64;Override
     *     public String getExpressionSuffix() {
     *         // 模板中表示表达式结束的标识
     *         return "}";
     *     }
     * }
     * </pre>
     *
     * @param expression 表达式字符串
     * @return 表达式对象
     */
    @SneakyThrows
    public Expression parseExpression(String expression) {
        // 通过 ParserContext 对象指定模板配置, 并解析模板
        return parser.parseExpression(expression, ParserContext.TEMPLATE_EXPRESSION);
    }
}
