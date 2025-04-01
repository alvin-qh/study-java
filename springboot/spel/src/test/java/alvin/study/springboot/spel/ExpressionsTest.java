package alvin.study.springboot.spel;

import static org.assertj.core.api.Assertions.atIndex;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.BDDAssertions.then;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.assertj.core.api.InstanceOfAssertFactories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.spel.infra.model.Group;
import alvin.study.springboot.spel.infra.model.User;
import alvin.study.springboot.spel.util.TemplatedExpressionParser;

/**
 * 对各类 SpEL 进行测试
 *
 * <p>
 * {@link ActiveProfiles @ActiveProfiles} 注解用于指定活动配置名, 通过指定为 {@code "test"},
 * 令所有注解为 {@link org.springframework.context.annotation.Profile @Profile}
 * 的配置类生效, 且配置文件 {@code classpath:/application-test.yml} 生效
 * </p>
 *
 * <p>
 * {@link SpringBootTest @SpringBootTest} 注解表示这是一个 Spring Boot 相关的测试, 其
 * {@code classes} 属性指定了该测试相关的配置类
 * </p>
 *
 * <p>
 * {@link ContextConfiguration @ContextConfiguration} 注解用于指定测试上下文配置
 * </p>
 */
@ActiveProfiles("test")
@SpringBootTest
@ContextConfiguration
@SuppressWarnings("unchecked")
public class ExpressionsTest {
    /**
     * 注入模板解析对象
     */
    @Autowired
    private TemplatedExpressionParser parser;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 测试常量表达式
     */
    @Test
    void literal_shouldExecute() {
        // 字符串常量
        var expression = parser.parseExpression("#{ 'Hello' }");
        then(expression.getValue(String.class)).isEqualTo("Hello");

        // decimal 常量
        expression = parser.parseExpression("#{ 123.456E+23 }");
        then(expression.getValue(BigDecimal.class)).isEqualTo(BigDecimal.valueOf(123.456E+23));

        // 整数常量
        expression = parser.parseExpression("#{ 0x7FFFFFFF }");
        then(expression.getValue(Long.class)).isEqualTo(2147483647L);

        // 负数常量
        expression = parser.parseExpression("#{ -100 }");
        then(expression.getValue(Integer.class)).isEqualTo(-100);

        // 布尔类型常量
        expression = parser.parseExpression("#{ true }");
        then(expression.getValue(Boolean.class)).isTrue();

        // null 值常量
        expression = parser.parseExpression("#{ null }");
        then(expression.getValue()).isNull();
    }

    /**
     * 测试字符串表达式
     */
    @Test
    void string_shouldExecute() {
        // 通过 + 连接字符串
        var expression = parser.parseExpression("#{ 'Hello' + ' World' }");
        var str = expression.getValue(String.class);
        then(str).isEqualTo("Hello World");

        // 在表达式内调用字符串对象方法 (其它类型对象方法类似) 分隔字符串
        expression = parser.parseExpression("#{ 'Hello World'.split(' ') }");
        var array = expression.getValue(String[].class);
        then(array).containsExactly("Hello", "World");

        // 在表达式内调用字符串对象方法 (其它类型对象方法类似) 连接字符串
        expression = parser.parseExpression("#{ 'Hello World'.concat('!') }");
        str = expression.getValue(String.class);
        then(str).isEqualTo("Hello World!");

        // 在表达式内调用字符串对象方法 (其它类型对象方法类似) 将字符串改为大写
        expression = parser.parseExpression("#{ 'Hello World'.toUpperCase() }");
        str = expression.getValue(String.class);
        then(str).isEqualTo("HELLO WORLD");
    }

    /**
     * 测试 {@link org.springframework.expression.EvaluationContext EvaluationContext}
     * 接口类型在表达式计算中的用途
     *
     * <p>
     * 如果要在表达式中使用 root 对象, 需要一个
     * {@link org.springframework.expression.EvaluationContext EvaluationContext}
     * 接口对象, 该对象用于在表达式计算期间解析表达式中用到的对象引用
     * </p>
     *
     * <p>
     * {@link SimpleEvaluationContext} 类型实现了
     * {@link org.springframework.expression.EvaluationContext EvaluationContext}
     * 接口的一个子集, 不包括 Java 类型引用, 构造函数和 Bean 对象引用等等, 它还要求明确选择对表达式中属性和方法的支持级别
     * </p>
     *
     * <p>
     * {@link SimpleEvaluationContext#setVariable(String, Object)} 方法用于通过一个名称设置一个变量,
     * 在表达式中可以通过 {@code #名称} 来访问该变量
     * </p>
     *
     * <p>
     * {@link org.springframework.expression.Expression#getValue(org.springframework.expression.EvaluationContext, Object, Class)
     * Expression.getValue(EvaluationContext, Object, Class)} 方法的第二个参数用于传递一个
     * {@code root} 对象, 在表达式中可以通过 {@code #root.<属性名>} 来访问 {@code root} 对象的属性; 也可以省略
     * {@code #root} 前缀, 直接使用 {@code <属性名>} 来访问对象属性
     * </p>
     */
    @Test
    void simpleEvaluationContext_shouldExecute() {
        // 实例化 SimpleEvaluationContext 对象
        var simpleCtx = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        // 实例化 root 对象
        var root = User.builder().id(1L).name("Alvin").birthday(LocalDate.of(1981, 3, 17)).build();

        // 向 SimpleEvaluationContext 对象中设置变量
        simpleCtx.setVariable("num", 100);

        // 执行表达式获取设置变量的值并进行计算, 其中:
        // #num 用于访问上下文中的 num 变量
        // #root 用于访问 root 对象, 即 User 对象的属性, #root 可以省略, 直接通过 name 访问 User.name 属性
        var expression = parser.parseExpression("#{ #root.name }: #{ #num ^ 2 }");
        var num = expression.getValue(simpleCtx, root, String.class);
        then(num).isEqualTo("Alvin: 10000");
    }

    /**
     * 测试 {@link org.springframework.expression.EvaluationContext EvaluationContext}
     * 接口类型在表达式计算中的用途
     *
     * <p>
     * 如果要在表达式中使用 root 对象, 需要一个
     * {@link org.springframework.expression.EvaluationContext EvaluationContext}
     * 对象, 该对象用于在表达式计算期间解析表达式中用到的对象引用
     * </p>
     *
     * <p>
     * {@link StandardEvaluationContext#setVariable(String, Object)}
     * 方法用于通过一个名称设置一个变量,
     * 在表达式中可以通过 {@code #名称} 来访问该变量
     * </p>
     *
     * <p>
     * {@link StandardEvaluationContext#setRootObject(Object)} 方法用于设置一个 {@code root}
     * 对象, 在表达式中可以通过 {@code #root.<属性名>} 来访问 {@code root} 对象的属性; 也可以省略
     * {@code #root} 前缀, 直接使用 {@code <属性名>} 来访问对象属性
     * </p>
     *
     * <p>
     * {@link StandardEvaluationContext#setBeanResolver(org.springframework.expression.BeanResolver)
     * StandardEvaluationContext.setBeanResolver(BeanResolver)} 方法用于设置一个 Bean 解析器,
     * 用来从 Spring 的对象管理器中获取指定名称的 Bean 对象, 在表达式中以 {@code @<Bean 名称>} 来访问
     * </p>
     */
    @Test
    void standardEvaluationContext_shouldExecute() {
        //
        var standardCtx = new StandardEvaluationContext();
        standardCtx.setVariable("num", 100);
        standardCtx.setRootObject(User.builder().id(1L).name("Alvin").birthday(LocalDate.of(1981, 3, 17)).build());
        standardCtx.setBeanResolver(new BeanFactoryResolver(applicationContext));

        // 执行表达式获取设置变量的值并进行计算, 其中:
        // #num 用于访问上下文中的 num 变量
        // #root 用于访问 root 对象, 即 User 对象的属性, #root 可以省略, 直接通过 name 访问 User.name 属性
        // #intValue 用于访问对象容器中注入的名为 "intValue" 的 Bean 对象
        var expression = parser.parseExpression("#{ #root.name }: #{ (#num + @intValue.value) }");
        var result = expression.getValue(standardCtx, String.class);
        then(result).isEqualTo("Alvin: 200");
    }

    /**
     * 测试在表达式中使用数组对象
     *
     * <p>
     * 如果 {@code root} 对象本身为数组, 则可以通过 {@code #root[<索引值>]} 来访问该数组指定位置的元素, 省略
     * {@code #root} 后写做 {@code [<索引值>]}
     * </p>
     *
     * <p>
     * 如果 {@code root} 对象的某个属性值为数组, 则可以通过 {@code #root.<属性名>[<索引值>]} 来访问该数组指定位置的元素,
     * 省略 {@code #root} 后写做 {@code <属性名>[<索引值>]}
     * </p>
     */
    @Test
    void array_shouldExecute() {
        // 初始化一个 Evaluation 上下文对象
        var context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        // 实例化整型数组对象
        var nums = new int[] { 1, 2, 3, 4 };

        // 通过 root 对象下标访问数组元素
        var expression = parser.parseExpression("#{ [0] }:#{ [1] }:#{ [2] }");
        var value = expression.getValue(context, nums, String.class);
        then(value).isEqualTo("1:2:3");

        // 实例化 User 对象数组
        var users = new User[] {
            User.builder().id(1L).name("U-1").birthday(LocalDate.of(1981, 3, 17)).build(),
            User.builder().id(2L).name("U-2").birthday(LocalDate.of(1982, 3, 17)).build(),
            User.builder().id(3L).name("U-3").birthday(LocalDate.of(1983, 3, 17)).build() };

        // 通过 root 对象下标访问数组元素的属性值
        expression = parser.parseExpression("#{ [1].id }:#{ [1].name }:#{ [1].birthday.dayOfMonth }");
        value = expression.getValue(context, users, String.class);
        then(value).isEqualTo("2:U-2:17");

        // 实例化 Group 对象, 将 User 对象数组作为其属性
        var group = Group.builder().name("Group1").users(users).build();

        // 通过 root 对象属性和下标访问数组元素
        expression = parser.parseExpression(
            "#{ name }: #{ users[1].id }:#{ users[1].name }:#{ users[1].birthday.dayOfMonth }");
        value = expression.getValue(context, group, String.class);
        then(value).isEqualTo("Group1: 2:U-2:17");
    }

    /**
     * 测试在表达式中访问 {@link List} 集合对象
     *
     * <p>
     * {@link List} 集合在表达式中的访问方式和访问数组基本类似
     * </p>
     *
     * <p>
     * 如果 {@code root} 对象本身为 {@link List} 集合对象, 则可以通过 {@code #root[<索引值>]}
     * 来访问该集合指定位置的元素, 省略 {@code #root} 后写做 {@code [<索引值>]}
     * </p>
     *
     * <p>
     * 如果 {@code root} 对象的某个属性值为{@link List} 集合对象, 则可以通过 {@code #root.<属性名>[<索引值>]}
     * 来访问该集合指定位置的元素, 省略 {@code #root} 后写做 {@code <属性名>[<索引值>]}
     * </p>
     *
     * <p>
     * 有三个运算符可以在表达式中对集合进行过滤, 分别为:
     * <ul>
     * <li>
     * {@code ?[<expression>]}: 遍历数组的每一项, 并对中括号中的 {@code expression} 进行计算, 所有值为
     * {@code true} 的元素保留并组成新的集合返回
     * </li>
     * <li>
     * {@code ^[<expression>]}: 遍历数组的每一项, 返回第一个令中括号中 {@code expression} 为
     * {@code true} 的元素
     * </li>
     * <li>
     * {@code $[<expression>]}: 反向遍历数组的每一项, 返回第一个令中括号中 {@code expression} 为
     * {@code true} 的元素
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * {@code ![<expression>]} 可以将集合的每个元素通过中括号中的 {@code expression} 映射为新值, 组成新的集合返回
     * </p>
     *
     * <p>
     * 在过滤或映射的表达式中, 可以通过 {@code #this} 访问到当前正在遍历的集合项, {@code #this} 可以省略
     * </p>
     */
    @Test
    void list_shouldExecute() {
        // 初始化一个 Evaluation 上下文对象
        var context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        // 实例化整型集合对象
        var nums = List.of(1, 2, 3, 4);

        // 通过 root 对象下标访问集合元素
        var expression = parser.parseExpression("#{ [0] }:#{ [1] }:#{ [2] }");
        var value = expression.getValue(context, nums, String.class);
        then(value).isEqualTo("1:2:3");

        // 实例化 User 对象集合
        var users = List.of(
            User.builder().id(1L).name("U-1").birthday(LocalDate.of(1981, 3, 17)).build(),
            User.builder().id(2L).name("U-2").birthday(LocalDate.of(1982, 3, 17)).build(),
            User.builder().id(3L).name("U-3").birthday(LocalDate.of(1983, 3, 17)).build());

        // 通过 root 对象下标访问集合元素的属性值
        expression = parser.parseExpression("#{ [1].id }:#{ [1].name }:#{ [1].birthday.dayOfMonth }");
        value = expression.getValue(context, users, String.class);
        then(value).isEqualTo("2:U-2:17");

        // 实例化 Group 对象, 将 User 对象数组作为其属性
        var group = Group.builder().name("Group1").users(users.toArray(new User[0])).build();
        expression = parser.parseExpression(
            "#{ name }: #{ usersAsList[1].id }:#{ usersAsList[1].name }:#{ usersAsList[1].birthday.dayOfMonth }");

        // 通过 root 对象属性和下标访问数组元素
        value = expression.getValue(context, group, String.class);
        then(value).isEqualTo("Group1: 2:U-2:17");

        // 测试对集合元素的过滤操作, 基于 nums 集合, 获取一个全部元素值大于 2 的子集合
        expression = parser.parseExpression("#{ ?[#this >= 2] }");
        var list = (List<Object>) expression.getValue(context, nums, List.class);
        then(list).containsExactly(2, 3, 4);

        // 测试对集合元素的过滤操作, 基于 users 集合, 获取第一个 user.name 属性值为 "U-2" 的元素, 这里省略了 #this
        expression = parser.parseExpression("#{ ^[name == 'U-2'] }");
        var user = expression.getValue(context, users, User.class);
        then(user).isNotNull()
                .extracting("name")
                .isEqualTo("U-2");

        // 测试对集合元素的过滤操作, 基于 users 集合, 获取第一个 user.name 属性值为 "U-2" 的元素, 这里省略了 #this (即
        // #this.name)
        expression = parser.parseExpression("#{ $[name == 'U-3'] }");
        user = expression.getValue(context, users, User.class);
        then(user).isNotNull()
                .extracting("name").isEqualTo("U-3");

        // 将集合元素进行映射, 组成新的集合, 这里省略了 #this (即 #this.name)
        expression = parser.parseExpression("#{ ![id + '-' + name] }");
        list = (List<Object>) expression.getValue(context, users, List.class);
        then(list).asInstanceOf(InstanceOfAssertFactories.LIST).containsExactly("1-U-1", "2-U-2", "3-U-3");
    }

    /**
     * 测试在表达式中访问 {@link Map} 集合对象
     *
     * <p>
     * {@link Map} 集合在表达式中通过 {@code key} 来访问
     * </p>
     *
     * <p>
     * 如果 {@code root} 对象本身为 {@link Map} 集合对象, 则可以通过 {@code #root[<key>]}
     * 来访问该集合指定位置的元素, 省略 {@code #root} 后写做 {@code [<key>]}
     * </p>
     *
     * <p>
     * 如果 {@code root} 对象的某个属性值为{@link Map} 集合对象, 则可以通过 {@code #root.<属性名>[<key>]}
     * 来访问该集合指定位置的元素, 省略 {@code #root} 后写做 {@code <属性名>[<key>]}
     * </p>
     *
     * <p>
     * 通过 {@code ![<expression>]} 可以遍历 {@link Map} 中的每个键值对, 并映射为一个 {@link List} 集合
     * </p>
     *
     * <p>
     * 在映射的表达式中, 可以通过 {@code #this} 访问到当前正在遍历的集合项, {@code #this} 可以省略
     * </p>
     */
    @Test
    void map_shouldExecute() {
        // 初始化一个 Evaluation 上下文对象
        var context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        // 实例化 Map 类型集合对象
        var nums = Map.of("a", "A", "b", "B", "c", "C");

        // 通过 root 对象 key 值从 Map 对象中获取 value
        var expression = parser.parseExpression("#{ ['a'] }:#{ ['b'] }:#{ ['c'] }");
        var value = expression.getValue(context, nums, String.class);
        then(value).isEqualTo("A:B:C");

        // 实例化 Value 为 User 对象的 Map 集合
        var users = Map.of(
            "a", User.builder().id(1L).name("U-1").birthday(LocalDate.of(1981, 3, 17)).build(),
            "b", User.builder().id(2L).name("U-2").birthday(LocalDate.of(1982, 3, 17)).build(),
            "c", User.builder().id(3L).name("U-3").birthday(LocalDate.of(1983, 3, 17)).build());

        // 通过 root 对象下标访问集合元素的属性值
        expression = parser.parseExpression("#{ ['a'].id }:#{ ['a'].name }:#{ ['a'].birthday.dayOfMonth }");
        value = expression.getValue(context, users, String.class);
        then(value).isEqualTo("1:U-1:17");

        // 实例化 Group 对象, 将 User 集合对象作为其属性
        var group = Group.builder().name("Group1").users(users.values().toArray(new User[0])).build();

        // 通过 root 对象属性和下标访问数组元素
        expression = parser.parseExpression(
            "#{ name }: #{ usersAsMap['U-1'].id }:#{ usersAsMap['U-1'].name }:#{ usersAsMap['U-1'].birthday.dayOfMonth }");
        value = expression.getValue(context, group, String.class);
        then(value).isEqualTo("Group1: 1:U-1:17");

        // 将 Map 的每个键值对进行映射, 这里省略了 #this (即 #this.key 和 #this.value)
        expression = parser.parseExpression("#{ ![key + ': ' + value.name] }");
        var list = expression.getValue(context, users, List.class);
        then(list).containsExactlyInAnyOrder("a: U-1", "b: U-2", "c: U-3");
    }

    /**
     * 测试在表达式中声明 {@link List} 集合
     *
     * <p>
     * 在表达式中通过 <code>{element1, element2, ..., elementN}</code> 可以产生一个 {@link List}
     * 集合
     * </p>
     *
     * <p>
     * 在表达式中集合声明中可以进一步包含子集合, 组成多维集合
     * </p>
     */
    @Test
    void inlineList_shouldExecute() {
        // 初始化一个 Evaluation 上下文对象
        var context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        // 在表达式中声明一个元素为整型的 List 集合
        var expression = parser.parseExpression("#{ {1, 2, 3, 4, 5} }");
        var value = expression.getValue(context, List.class);
        then(value).containsExactly(1, 2, 3, 4, 5);

        // 在表达式中声明一个二维 List 集合
        expression = parser.parseExpression("#{ { {1, 2, 3}, {4, 5} } }");
        value = Objects.requireNonNull(expression.getValue(context, List.class));
        then(value.get(0)).asInstanceOf(InstanceOfAssertFactories.LIST).containsExactly(1, 2, 3);
        then(value.get(1)).asInstanceOf(InstanceOfAssertFactories.LIST).containsExactly(4, 5);

        var users = List.of(
            User.builder().id(1L).name("U-1").birthday(LocalDate.of(1981, 3, 17)).build(),
            User.builder().id(2L).name("U-2").birthday(LocalDate.of(1982, 3, 17)).build(),
            User.builder().id(3L).name("U-3").birthday(LocalDate.of(1983, 3, 17)).build(),
            User.builder().id(4L).name("U-4").birthday(LocalDate.of(1984, 3, 17)).build());

        // 通过 root 对象表示的集合元素, 在表达式中声明集合
        expression = parser.parseExpression("#{ { {[0], [1]}, {[2], [3]} } }");
        value = Objects.requireNonNull(expression.getValue(context, users, List.class));
        then(value.get(0)).asInstanceOf(InstanceOfAssertFactories.LIST).containsExactly(users.get(0), users.get(1));
        then(value.get(1)).asInstanceOf(InstanceOfAssertFactories.LIST).containsExactly(users.get(2), users.get(3));
    }

    /**
     * 测试在表达式中声明 {@link Map} 集合
     *
     * <p>
     * 在表达式中通过 <code>{key1: value1, key2: value2, ..., keyN: valueN}</code> 可以产生一个
     * {@link Map} 集合
     * </p>
     */
    @Test
    void inlineMap_shouldExecute() {
        // 初始化一个 Evaluation 上下文对象
        var context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        // 在表达式中声明一个 Map 集合
        var expression = parser.parseExpression("#{ {'a': 'A', 'b': 100} }");
        var value = expression.getValue(context, Map.class);
        then(value).containsExactly(
            entry("a", (Object) "A"),
            entry("b", (Object) 100));

        var users = List.of(
            User.builder().id(1L).name("U-1").birthday(LocalDate.of(1981, 3, 17)).build(),
            User.builder().id(2L).name("U-2").birthday(LocalDate.of(1982, 3, 17)).build());

        // 通过 root 对象表示的集合元素, 在表达式中声明集合
        expression = parser.parseExpression("#{ {'a': [0], 'b': [1]} }");
        value = expression.getValue(context, users, Map.class);
        then(value).containsExactly(
            entry("a", users.get(0)),
            entry("b", users.get(1)));
    }

    /**
     * 可以通过 {@code new} 运算符在表达式中产生对象
     *
     * <p>
     * 除了 {@code java.lang} 包下的类型, 其余类型都需要包括完整的包名
     * </p>
     *
     * <p>
     * 要在表达式中使用 {@code new} 运算符,
     * {@link org.springframework.expression.EvaluationContext EvaluationContext}
     * 参数要使用 {@link StandardEvaluationContext} 类型
     * </p>
     */
    @Test
    void new_shouldExecute() {
        // 初始化一个 Evaluation 上下文对象
        var context = new StandardEvaluationContext();

        // 在表达式中通过 new 运算符实例化一个字符串对象
        var expression = parser.parseExpression("#{ new String('Hello').toUpperCase() }");
        var value = expression.getValue(context, String.class);
        then(value).isEqualTo("HELLO");

        // 在上下文中设置变量
        context.setVariable("birthday", LocalDate.of(1981, 3, 17));

        // 在表达式中通过 new 运算符实例化一个 User 对象
        expression
            = parser.parseExpression("#{ new alvin.study.springboot.spel.infra.model.User(1L, 'Alvin', #birthday) }");
        var user = expression.getValue(context, User.class);
        then(Objects.requireNonNull(user).getId()).isEqualTo(1L);
        then(user.getName()).isEqualTo("Alvin");
        then(user.getBirthday()).isEqualTo(LocalDate.of(1981, 3, 17));

        // 在表达式中通过 new 运算符实例化数组
        expression = parser.parseExpression("#{ new int[] {1, 2, 3, 4} }");
        var array = expression.getValue(context, int[].class);
        then(array).containsExactly(1, 2, 3, 4);

        // 在表达式中通过 new 运算符实例化二维数组
        expression = parser.parseExpression("#{ new int[2][3] }");
        var arrays = expression.getValue(context, int[][].class);
        then(arrays).hasNumberOfRows(2)
                .contains(new int[] { 0, 0, 0 }, atIndex(0))
                .contains(new int[] { 0, 0, 0 }, atIndex(1));
    }

    /**
     * 在表达式中使用赋值语句
     *
     * <p>
     * 在表达式中可以使用 {@code =} 进行赋值操作, 可以赋值的数据包括两类:
     * <ul>
     * <li>
     * {@code #<variable> = <value>}, 这种方式会将值赋予
     * {@link org.springframework.expression.EvaluationContext EvaluationContext}
     * 对象的变量中, 可以通过
     * {@link org.springframework.expression.EvaluationContext#lookupVariable(String)
     * EvaluationContext.lookupVariable(String)} 方法通过变量名 (不带 {@code #} 号) 获取
     * </li>
     * <li>
     * {@code <property> = <value>}, 这种方式相当于调用 {@code root} 对象对应属性的 {@code set} 方法,
     * 设置新的属性值
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 要在表达式中进行赋值操作, {@link org.springframework.expression.EvaluationContext
     * EvaluationContext} 参数要使用 {@link StandardEvaluationContext} 类型
     * </p>
     *
     * <p>
     * 注意, 即便在表达式中对 {@code #root} 进行赋值, 也无法改变 {@code root} 对象的值, 而是在
     * {@link org.springframework.expression.EvaluationContext EvaluationContext}
     * 变量中增加了一个 {@code root} 变量, 且无法通过 {@code #root} 变量名访问该变量, 所以不应该尝试去改变
     * {@code root} 对象值
     * </p>
     */
    @Test
    void assignValue_shouldExecute() {
        // 初始化一个 Evaluation 上下文对象
        var context = new StandardEvaluationContext();
        context.setVariable("num", 100L);

        // 在表达式中为 EvaluationContext 中的 num 变量赋值, 并验证赋值表达式的返回结果
        var expression = parser.parseExpression("#{ #num = 200L }");
        var value = expression.getValue(context, Long.class);
        then(value).isEqualTo(200L);

        // 确认 num 变量的值已经改变
        expression = parser.parseExpression("#{ #num }");
        value = expression.getValue(context, Long.class);
        then(value).isEqualTo(200L);
        then(context.lookupVariable("num")).isEqualTo(200L);

        // 在表达式中为 str 变量赋值, 并确认 EvaluationContext 中增加了 str 变量
        expression = parser.parseExpression("#{ #str = 'Hello' }");
        var str = expression.getValue(context, String.class);
        then(str).isEqualTo("Hello");
        then(context.lookupVariable("str")).isEqualTo("Hello");

        // 在 EvaluationContext 中设置 root 对象
        context.setRootObject(
            User.builder()
                    .id(1L)
                    .name("Alvin")
                    .birthday(LocalDate.of(1981, 3, 17))
                    .build());

        // 在表达式中改变 root 对象的 id 属性, 即 root.id = 2L, 并验证赋值表达式的返回结果
        expression = parser.parseExpression("#{ id = 2L }");
        value = expression.getValue(context, Long.class);
        then(value).isEqualTo(2L);
        then(context.getRootObject().getValue())
                .isInstanceOf(User.class)
                .extracting("id")
                .isEqualTo(2L);

        // 确认 root 对象的 id 属性已经改变
        expression = parser.parseExpression("#{ #root }");
        var user = expression.getValue(context, User.class);
        then(user).isNotNull()
                .extracting("id")
                .isEqualTo(2L);

        // 从 EvaluationContext 中获取 root 对象, 确认其 id 属性以及改变
        user = (User) context.getRootObject().getValue();
        then(user).isNotNull()
                .extracting("id")
                .isEqualTo(2L);

        // 在表达式中为 root 赋值, 并确认赋值表达式的结果
        expression = parser.parseExpression(
            "#{ #root = new alvin.study.springboot.spel.infra.model.User(3L, 'Emma', birthday) }");
        user = expression.getValue(context, User.class);
        then(user).isNotNull()
                .extracting("id", "name", "birthday")
                .contains(3L, "Emma", LocalDate.of(1981, 3, 17));

        // 在表达式中访问 root 对象, 确认 root 变量并未因为上面的赋值发生变化
        expression = parser.parseExpression("#{ #root }");
        user = expression.getValue(context, User.class);
        then(user).isNotNull()
                .extracting("id", "name", "birthday")
                .contains(2L, "Alvin", LocalDate.of(1981, 3, 17));

        // 从 EvaluationContext 中获取 root 对象, 确认该对象未发生变化
        user = (User) context.getRootObject().getValue();
        then(user).isNotNull()
                .extracting("id", "name", "birthday")
                .contains(2L, "Alvin", LocalDate.of(1981, 3, 17));

        // 从 EvaluationContext 中获取 root 变量, 确认增加了 root 变量
        user = (User) context.lookupVariable("root");
        then(user).isNotNull()
                .extracting("id", "name", "birthday")
                .contains(3L, "Emma", LocalDate.of(1981, 3, 17));
    }
}
