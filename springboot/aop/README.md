# Spring AOP

- [Spring AOP](#spring-aop)
  - [1. 切点表达式](#1-切点表达式)
    - [1.1. `execution`](#11-execution)
    - [1.2. `within`](#12-within)
    - [1.3. `args`](#13-args)
    - [1.4. `this` 和 `target`](#14-this-和-target)
    - [1.5. `@within`](#15-within)
    - [1.6. `@annotation`](#16-annotation)
    - [1.7. `@args`](#17-args)
    - [1.8. `@DeclareParents`](#18-declareparents)
    - [1.9. `perthis` 和 `pertarget`](#19-perthis-和-pertarget)

> [官方文档](https://docs.spring.io/spring-framework/docs/2.0.x/reference/aop.html)

## 1. 切点表达式

### 1.1. `execution`

由于 Spring 切面粒度最小是达到方法级别, 而 `execution` 表达式可以用于明确指定方法返回**类型, 类名, 方法名和参数名**
等与方法相关的部件,
并且在 Spring 中, 大部分需要使用 AOP 的业务场景也只需要达到方法级别即可, 因而 `execution` 表达式的使用是最为广泛的

如下是 `execution` 表达式的语法:

```plaintext
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) throws-pattern?)
```

这里问号表示当前项为可选项, 其中各项的语义如下:

- `modifiers-pattern`: 方法的可见性, 如 `public`, `protected`;
- `ret-type-pattern`: 方法的返回值类型, 如 `int`, `void` 等;
- `declaring-type-pattern`: 方法所在类的全路径名, 如 `com.spring.Aspect`;
- `name-pattern`: 方法名类型, 如 `business()`;
- `param-pattern`: 方法的参数类型, 如 `java.lang.String`;
- `throws-pattern`: 方法抛出的异常类型, 如 `java.lang.Exception`;

如下是一个使用 `execution` 表达式的例子:

```plaintext
execution(public * alvin.study.service.BusinessService.business(java.lang.String, ..))
```

上述切点表达式将会匹配使用 `public` 修饰, 返回值为**任意类型**, 并且是 `alvin.study.service.BusinessService`
类中名称为 `business`
的方法, 方法可以有多个参数, 但是第一个参数必须是 `java.lang.String` 类型的方法。上述示例中使用了 `..` 通配符, 关于通配符的类型,
主要有两种:

- `*` 通配符, 该通配符主要用于匹配单个单词, 或者是以某个词为前缀或后缀的单词

  如下示例表示返回值为任意类型, 在 `alvin.study.service.BusinessService` 类中, 并且参数个数为零的方法:

    ```plaintext
    execution(* alvin.study.service.BusinessService.*())
    ```

  下述示例表示返回值为任意类型, 在 `alvin.study.service` 包中, 以 `Business` 为前缀的类, 并且是类中参数个数为零方法:

    ```plaintext
    execution(* alvin.study.service.Business*.*())
    ```

- `..` 通配符, 该通配符表示 `0` 个或多个项, 主要用于 `declaring-type-pattern` 和 `param-pattern` 中,
  如果用于 `declaring-type-pattern` 中, 则表示匹配当前包及其子包, 如果用于 `param-pattern` 中, 则表示匹配 `0` 个或多个参数

  如下示例表示匹配返回值为任意类型, 是 `alvin.study.service` 包及其子包下的任意类的名称为 `business` 的方法,
  而且该方法不能有任何参数:

    ```plaintext
    execution(* alvin.study.service..*.business())
    ```

  这里需要说明的是, 包路径 `alvin.study.service..*.business()` 中的 `..` 应该理解为延续前面的 `service` 路径,
  表示到 `service`
  路径为止, 或者继续延续 `service` 路径, 从而包括其子包路径; 后面的 `*.business()`, 这里的 `*` 表示匹配一个单词,
  因为是在方法名前,
  因而表示匹配任意的类

  如下示例是使用 `..` 表示任意个数的参数的示例, 需要注意, 表示参数的时候可以在括号中事先指定某些类型的参数,
  而其余的参数则由 `..` 进行匹配:

    ```plaintext
    execution(* alvin.study.service.BusinessService.business(java.lang.String, ..))
    ```

### 1.2. `within`

`within` 表达式的粒度为类, 其参数为全路径的类名 (可使用通配符),
表示匹配当前表达式的所有类都将被当前方法环绕。如下是 `within` 表达式的语法:

```plaintext
within(declaring-type-pattern)
```

`within` 表达式只能指定到类级别, 如下示例表示匹配 `alvin.study.service.BusinessService` 中的所有方法:

```plaintext
within(alvin.study.service.BusinessService)
```

`within` 表达式路径和类名都可以使用通配符进行匹配, 比如如下表达式将匹配 `alvin.study.service` 包下的所有类,
不包括子包中的类:

```plaintext
within(alvin.study.service.*)
```

如下表达式表示匹配 `alvin.study.service` 包及子包下的所有类:

```plaintext
within(alvin.study.service..*)
```

### 1.3. `args`

`args` 表达式的作用是匹配指定参数类型和指定参数数量的方法, 无论其类路径或者是方法名是什么. 这里需要注意的是, `args`
指定的参数必须是全路径的

如下是 `args` 表达式的语法:

```plaintext
args(param-pattern)
```

如下示例表示匹配所有只有一个参数, 并且参数类型是 `java.lang.String` 类型的方法:

```plaintext
args(java.lang.String)
````

也可以使用通配符, 但这里通配符只能使用 `..`, 而不能使用 `*`. 如下是使用通配符的实例,
该切点表达式将匹配第一个参数为 `java.lang.String`,
最后一个参数为 `java.lang.Integer`, 并且中间可以有任意个数和类型参数的方法:

```plaintext
args(java.lang.String, .., java.lang.Integer)
```

### 1.4. `this` 和 `target`

`this` 和 `target` 需要放在一起进行讲解, 主要目的是对其进行区别. `this` 和 `target` 表达式中都只能指定类或者接口,
在面向切面编程规范中,
`this` 表示匹配调用当前切点表达式所指代对象方法的对象, `target` 表示匹配切点表达式指定类型的对象

比如有两个类 `A` 和 `B`, 并且 `A` 调用了 `B` 的某个方法, 如果切点表达式为 `this(B)`, 那么 `A` 的实例将会被匹配,
也即其会被使用当前切点
表达式的 `Advice` 环绕; 如果这里切点表达式为 `target(B)`, 那么 `B` 的实例也即被匹配,
其将会被使用当前切点表达式的 `Advice` 环绕

在介绍 Spring 中的 `this` 和 `target` 的使用之前, 首先需要讲解一个概念: **业务对象 (目标对象)**和**代理对象**

对于切面编程, 有一个目标对象, 也有一个代理对象, 目标对象是声明的业务逻辑对象, 而代理对象是使用切面逻辑对业务逻辑进行包裹之后生成的对象

如果使用的是 JDK 动态代理, 那么业务对象和代理对象将是两个对象, 在调用代理对象逻辑时, 其切面逻辑中会调用目标对象的逻辑;
如果使用的是 Cglib 代理,
由于是使用的子类进行切面逻辑织入的, 那么只有一个对象, 即织入了代理逻辑的业务类的子类对象, 此时不会生成业务类的对象

在 Spring 中, 其对 `this` 的语义进行了改写, 即如果当前对象生成的代理对象符合 `this` 指定的类型, 那么就为其织入切面逻辑

简单的说就是, `this` 将匹配代理对象为指定类型的类. `target` 的语义则没有发生变化, 即其将匹配业务对象为指定类型的类

如下是使用 `this` 和 `target` 表达式的简单示例:

```plaintext
this(alvin.study.service.BusinessService)
```

```plaintext
target(alvin.study.service.BusinessService)
```

通过上面的讲解可以看出, `this` 和 `target` 的使用区别其实不大, 大部分情况下其使用效果是一样的, 但其区别也还是有的

Spring 使用的代理方式主要有两种: JDK 代理和 Cglib 代理 (
关于这两种代理方式的讲解可以查看本人的文章代理模式实现方式及优缺点对比)

针对这两种代理类型, 关于目标对象与代理对象, 理解如下两点是非常重要的:

- 如果目标对象被代理的方法是其实现的某个接口的方法, 那么将会使用 JDK 代理生成代理对象, 此时代理对象和目标对象是两个对象,
  并且都实现了该接口

- 如果目标对象是一个类, 并且其没有实现任意接口, 那么将会使用 Cglib 代理生成代理对象, 并且只会生成一个对象, 即 Cglib
  生成的代理类的对象

结合上述两点说明, 这里理解 `this` 和 `target` 的异同就相对比较简单了

这里分三种情况进行说明:

- `this(SomeInterface)` 或 `target(SomeInterface)`: 这种情况下, 无论是对于 JDK 代理还是 Cglib 代理,
  其目标对象和代理对象都是实现 `SomeInterface` 接口的 (Cglib 生成的目标对象的子类也是实现了 `SomeInterface` 接口的),
  因而 `this` 和 `target` 语义都是符合的, 此时这两个表达式的效果一样;

- `this(SomeObject)` 或 `target(SomeObject)`, 这里 `SomeObject` 没实现任何接口
  这种情况下, Spring 会使用 Cglib 代理生成 `SomeObject` 的代理类对象, 由于代理类是 `SomeObject` 的子类,
  子类的对象也是符合 `SomeObject` 类型的, 因而 `this` 将会被匹配, 而对于 `target`, 由于目标对象本身就是 `SomeObject` 类型,
  因而这两个表达式的效果一样;

- `this(SomeObject)` 或 `target(SomeObject)`, 这里 `SomeObject` 实现了某个接口: 对于这种情况, 虽然表达式中指定的是一种具体的对象类型,
  但由于其实现了某个接口, 因而 Spring 默认会使用 JDK 代理为其生成代理对象, JDK 代理生成的代理对象与目标对象实现的是同一个接口,
  但代理对象与目标对象还是不同的对象, 由于代理对象不是 `SomeObject` 类型的, 因而此时是不符合 `this` 语义的,
  而由于目标对象就是 `SomeObject` 类型, 因而 `target` 语义是符合的, 此时 `this` 和 `target` 的效果就产生了区别;
  这里如果强制 Spring 使用 Cglib 代理, 因而生成的代理对象都是 `SomeObject` 子类的对象, 其是 `SomeObject` 类型的,
  因而 `this` 和 `target` 的语义都符合, 其效果就是一致的

关于 `this` 和 `target` 的异同, 使用如下示例进行简单演示:

```java
/**
 * 目标类
 */
public class Apple {
  public void eat() {
    System.out.println("Apple.eat method invoked");
  }
}
```

```java
/**
 * 切面类
 */
@Aspect
public class MyAspect {
  @Around("this(alvin.study.domain.Apple)")
  public Object around(ProceedingJoinPoint jp) throws Throwable {
    System.out.println("this is before around advice");

    var result = jp.proceed();
    System.out.println("this is after around advice");

    return result;
  }
}
```

执行切面的结果为:

```plaintext
this is before around advice
Apple.eat method invoked
this is after around advice
```

上述示例中, `Apple` 没有实现任何接口, 因而使用的是 Cglib 代理, `this` 表达式会匹配 `Apple` 对象

这里将切点表达式更改为 `target`, 还是执行上述代码, 会发现结果还是一样的:

```plaintext
target(alvin.study.domain.Apple)
```

如果对 `Apple` 的声明进行修改, 使其实现一个接口, 那么这里就会显示出 `this` 和 `target` 的执行区别了:

```java
public class Apple implements IApple {
  public void eat() {
    System.out.println("Apple.eat method invoked");
  }
}
```

还是执行上述代码, 对于 `this` 表达式, 其执行结果如下:

```plaintext
Apple.eat method invoked
```

对于 `target` 表达式, 其执行结果如下:

```plaintext
this is before around advice
Apple.eat method invoked
this is after around advice
```

可以看到, 这种情况下 `this` 和 `target` 表达式的执行结果是不一样的, 这正好符合前面讲解的第三种情况

### 1.5. `@within`

前面讲解了 `within` 的语义表示匹配指定类型的类实例, 这里的 `@within` 表示匹配带有指定注解的类, 其使用语法如下所示:

```plaintext
@within(annotation-type)
```

如下所示示例表示匹配使用 `alvin.study.annotation.BusinessAspect` 注解标注的类:

```plaintext
@within(alvin.study.annotation.BusinessAspect)
```

下面的例子演示 `@within` 的用法:

```java
/**
 * 注解类
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FruitAspect {
}
```

```java
/**
 * 目标类
 */
@FruitAspect
public class Apple {
  public void eat() {
    System.out.println("Apple.eat method invoked");
  }
}
```

```java
/**
 * 切面类
 */
@Aspect
public class MyAspect {
  @Around("@within(alvin.study.annotation.FruitAspect)")
  public Object around(ProceedingJoinPoint jp) throws Throwable {
    System.out.println("this is before around advice");

    var result = jp.proceed();
    System.out.println("this is after around advice");

    return result;
  }
}
```

上述切面表示匹配使用 `FruitAspect` 注解的类, 而 `Apple` 类则使用了该注解, 因而 `Apple` 类方法的调用会被切面环绕,
执行运行驱动类可得到如下结果, 说明 `Apple.eat()` 方法确实被环绕了:

```plaintext
this is before around advice
Apple.eat method invoked
this is after around advice
```

### 1.6. `@annotation`

`@annotation` 的使用方式与 `@within` 的相似, 表示匹配使用 `@annotation` 指定注解标注的方法将会被环绕, 其使用语法如下:

```plaintext
@annotation(annotation-type)
```

如下示例表示匹配使用 `alvin.study.annotation.BusinessAspect` 注解标注的方法:

```plaintext
@annotation(alvin.study.annotation.BusinessAspect)
```

这里继续复用 [1.5. `@within`](#15-within) 节使用的例子进行讲解 `@annotation` 的用法, 只是这里需要对 `Apple`
和 `MyAspect`
使用和指定注解的方式进行修改, `FruitAspect` 不用修改的原因是声明该注解时已经指定了其可以使用在类, 方法和参数上:

```java
/**
 * 目标类, 将 FruitAspect 移到了方法上
 */
public class Apple {
  @FruitAspect
  public void eat() {
    System.out.println("Apple.eat method invoked");
  }
}
```

```java
@Aspect
public class MyAspect {
  @Around("@annotation(alvin.study.annotation.FruitAspect)")
  public Object around(ProceedingJoinPoint pjp) throws Throwable {
    System.out.println("this is before around advice");

    var result = pjp.proceed();
    System.out.println("this is after around advice");

    return result;
  }
}
```

这里 `Apple.eat()` 方法使用 `FruitAspect` 注解进行了标注, 因而该方法的执行会被切面环绕, 其执行结果如下:

```plaintext
this is before around advice
Apple.eat method invoked
this is after around advice
```

### 1.7. `@args`

`@within` 和 `@annotation` 分别表示匹配使用指定注解标注的类和标注的方法将会被匹配, `@args` 则表示使用指定注解标注的类作为某个方法的参数时
该方法将会被匹配

如下是 `@args` 注解的语法:

```plaintext
@args(annotation-type)
```

如下示例表示匹配使用了 `alvin.study.annotation.FruitAspect` 注解标注的类作为参数的方法:

```plaintext
@args(alvin.study.annotation.FruitAspect)
```

这里使用如下示例对 `@args` 的用法进行讲解:

```java
/**
 * 使用注解标注的参数类
 */
@FruitAspect
public class Apple {
}
```

```java
/**
 * 使用 Apple 参数的目标类
 */
public class FruitBucket {
  public void putIntoBucket(Apple apple) {
    System.out.println("put apple into bucket");
  }
}
```

```java
@Aspect
public class MyAspect {
  @Around("@args(alvin.study.annotation.FruitAspect)")
  public Object around(ProceedingJoinPoint jp) throws Throwable {
    System.out.println("this is before around advice");

    var result = jp.proceed();
    System.out.println("this is after around advice");

    return result;
  }
}
```

这里 `FruitBucket.putIntoBucket(Apple)` 方法的参数 `Apple` 使用了 `@args` 注解指定的 `FruitAspect` 进行了标注,
因而该方法的调用将会被环绕

执行驱动类, 结果如下:

```plaintext
this is before around advice
put apple into bucket
this is after around advice
```

### 1.8. `@DeclareParents`

`@DeclareParents` 也称为 `Introduction` (引入), 表示为指定的目标类引入新的属性和方法

关于 `@DeclareParents` 的原理其实比较好理解, 因为无论是 JDK 代理还是 Cglib 代理, 想要引入新的方法,
只需要通过一定的方式将新声明的方法织入到代理类中即可, 因为代理类都是新生成的类,
因而织入过程也比较方便。如下是 `@DeclareParents` 的使用语法:

```java
@DeclareParents(value = "TargetType", defaultImpl = WeaverType.class)
private WeaverInterface attribute;
```

这里 `TargetType` 表示要织入的目标类型 (带全路径), `WeaverInterface` 中声明了要添加的方法, `WeaverType` 中声明了要织入的方法的具体实现

如下示例表示在 `Apple` 类中织入 `IDescriber` 接口声明的方法:

```java
@DeclareParents(value = "alvin.study.service.Apple", defaultImpl = DescriberImpl.class)
private IDescriber describer;
```

这里使用如下实例对 `@DeclareParents` 的使用方式进行讲解:

```java
/**
 * 织入方法的目标类
 */
public class Apple {
  public void eat() {
    System.out.println("Apple.eat method invoked");
  }
}
```

```java
/**
 * 要织入的接口
 */
public interface IDescriber {
  void desc();
}
```

```java
/**
 * 要织入接口的默认实现
 */
public class DescriberImpl implements IDescriber {
  @Override
  public void desc() {
    System.out.println("this is an introduction describer");
  }
}
```

```java
/**
 * 切面实例
 */
@Aspect
public class MyAspect {
  @DeclareParents(value = "alvin.study.service.Apple", defaultImpl = DescriberImpl.class)
  private IDescriber describer;
}
```

在 `MyAspect` 中声明了需要将 `IDescriber` 的方法织入到 `Apple` 实例中, 在驱动类中可以看到, 获取的是 `apple` 实例,
但是得到的 Bean 却可以强转为 `IDescriber` 类型, 因而说明织入操作成功了

### 1.9. `perthis` 和 `pertarget`

在 Spring AOP 中, 切面类的实例只有一个, 比如前面一直使用的 `MyAspect` 类, 假设使用的切面类需要具有某种状态,
以适用某些特殊情况的使用,
比如多线程环境, 此时单例的切面类就不符合要求了

在 Spring AOP 中, 切面类默认都是单例的, 但其还支持另外两种多例的切面实例的切面, 即 `perthis` 和 `pertarget`,
需要注意的是 `perthis` 和 `pertarget` 都是使用在切面类的 `@Aspect` 注解中的。这里 `perthis` 和 `pertarget` 表达式中都是指定一个
切面表达式, 其语义与前面讲解的 `this` 和 `target` 非常的相似

`perthis` 表示如果某个类的代理类符合其指定的切面表达式, 那么就会为每个符合条件的目标类都声明一个切面实例

`pertarget` 表示如果某个目标类符合其指定的切面表达式, 那么就会为每个符合条件的类声明一个切面实例

从上面的语义可以看出, `perthis` 和 `pertarget` 的含义是非常相似的

如下是 `perthis` 和 `pertarget` 的使用语法:

```plaintext
perthis(pointcut-expression)
```

```plaintext
pertarget(pointcut-expression)
```

由于 `perthis` 和 `pertarget` 的使用效果大部分情况下都是一致的, 这里主要讲解 `perthis` 和 `pertarget` 的区别

关于 `perthis` 和 `pertarget` 的使用, 需要注意的一个点是, 由于 `perthis` 和 `pertarget` 都是为每个符合条件的类声明一个切面实例,
因而切面类在配置文件中的声明上一定要加上 `prototype`, 否则 Spring 启动是会报错的

如下是使用的示例:

```java
/**
 * 目标类实现的接口
 */
public interface Fruit {
  void eat();
}
```

```java
/**
 * 业务类
 */
public class Apple implements Fruit {
  public void eat() {
    System.out.println("Apple.eat method invoked");
  }
}
```

```java
/**
 * 切面类
 */
@Aspect("perthis(this(com.spring.service.Apple))")
public class MyAspect {
  public MyAspect() {
    System.out.println("create MyAspect instance, address: " + toString());
  }

  @Around("this(com.spring.service.Apple)")
  public Object around(ProceedingJoinPoint jp) throws Throwable {
    System.out.println("this is before around advice");

    var result = jp.proceed();
    System.out.println("this is after around advice");

    return result;
  }
}
```

这里使用的切面表达式语法为 `perthis(this(com.spring.service.Apple))`, 这里 `this` 表示匹配代理类是 `Apple`
类型的类, `perthis` 则
表示会为这些类的每个实例都创建一个切面类

由于 `Apple` 实现了 `Fruit` 接口, 因而 Spring 使用 JDK 动态代理为其生成代理类, 也就是说代理类与 `Apple` 都实现了 `Fruit`
接口,
但是代理类不是 `Apple` 类型, 因而这里声明的切面不会匹配到 `Apple` 类

执行上述驱动类, 结果如下:

```plaintext
Apple.eat method invoked
```

结果表明 `Apple` 类确实没有被环绕

如果讲切面类中的 `perthis` 和 `this` 修改为 `pertarget` 和 `target`, 效果如何呢:

```java
@Aspect("pertarget(target(alvin.study.service.Apple))")
public class MyAspect {
  public MyAspect() {
    System.out.println("create MyAspect instance, address: " + toString());
  }

  @Around("target(com.spring.service.Apple)")
  public Object around(ProceedingJoinPoint pjp) throws Throwable {
    System.out.println("this is before around advice");

    var result = pjp.proceed();
    System.out.println("this is after around advice");

    return result;
  }
}
```

执行结果如下:

```plaintext
create MyAspect instance, address: chapter7.eg6.MyAspect@48fa0f47
this is before around advice
Apple.eat method invoked
this is after around advice
```

可以看到, `Apple` 类被切面环绕了

这里 `target` 表示目标类是 `Apple` 类型, 虽然 Spring 使用了 JDK 动态代理实现切面的环绕, 代理类虽不是 `Apple` 类型,
但是目标类却是 `Apple` 类型, 符合 `target` 的语义, 而 `pertarget` 会为每个符合条件的表达式的类实例创建一个代理类实例,
因而这里 `Apple` 会被环绕

由于代理类与目标类的差别非常小, 因而与 `this` 和 `target` 一样, `perthis` 和 `pertarget` 的区别也非常小, 大部分情况下其使用效果是一致的

执行结果如下:

```plaintext
create MyAspect instance, address: chapter7.eg6.MyAspect@48fa0f47
this is before around advice
Apple.eat method invoked.
this is after around advice
create MyAspect instance, address: chapter7.eg6.MyAspect@56528192
this is before around advice
Apple.eat method invoked.
this is after around advice
```

执行结果中两次打印的 `create MyAspect instance` 表示当前切面实例创建了两次, 这也符合进行的两次获取 `Apple` 实例
