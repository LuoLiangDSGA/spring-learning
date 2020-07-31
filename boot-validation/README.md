## SpringBoot中的参数校验

> 背景

为了保证数据的正确性, 避免埋坑, 参数校验在日常业务开发中用得非常多, 在Spring中用得最多的就是使用`JSR303– Bean Validation`规范提供的校验, Hibernate Validator是Bean Validation的参考实现, Hibernate Validator提供了`JSR303`规范中所有内置constraint的实现，除此之外还有一些附加的constraint。SpringBoot也提供了starter方便我们快速的支持参数校验. 

### Bean Validation中的常用注解

> 表1. Bean Validation中内置的constraint

![](https://tva1.sinaimg.cn/large/007S8ZIlgy1gh90kkmqo9j30u00xr7ao.jpg)

> 表2. Hibernate Validator中附加的constraint

![](https://tva1.sinaimg.cn/large/007S8ZIlgy1gh90l051v1j30zu0dgdhg.jpg)

### SpringBoot中的使用

> 引入starter

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-validation</artifactId>
</dependency>

```

引入之后SpringBoot已经自动配置好了参数校验, 使用就变得非常简单了.

#### bean的校验
> 定义需要验证的bean

```
public class User {

    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "请输入合法年龄")
    private Integer age;

    ... Getter Setter toString省略
}

```
这里的message也可以写成国际化的key

> 编写controller模拟新增方法

```java
    @PostMapping("/users")
    public ResponseEntity<String> addUser(@Valid @RequestBody User user) {
        System.out.println(user.toString());
        return ResponseEntity.ok("validate success");
    }
```

这里在需要验证的bean参数前加上`@Valid`注解, 加了该注解之后, 方法被请求时将会对bean中添加了注解的字段进行验证

> 定义全局异常处理器

```
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
```

这里对bean验证失败抛出的`MethodArgumentNotValidException`进行了处理, 方便前端进行展示

> 测试请求一下接口

```
post localhost:8080/users
{
    "name": "fantasy",
    "age": 0
}
```
返回结果: 

```
{
    "age": "请输入合法年龄"
}
```

#### 简单参数的校验

> 上面的参数类型是bean, 如果我们需要对诸如String类型这样简单的参数进行校验呢?

我们编写一个简单的查询方法, 按姓名查询, 需要对请求的参数进行校验

```java
    @GetMapping("/v1/users")
    public ResponseEntity<String> getUsers(@NotBlank String name) {
        System.out.println(name);
        return ResponseEntity.ok("validate success");
    }
```
前面说过`JSR303`是对bean的校验, 不支持对普通参数进行校验, 但是`spring`提供了`@Validated`注解可以对方法参数进行校验, 但是`@Validated`需要添加在class上

```java
@RestController
@Validated
public class UserController {
    ...
}
```

这样就可以对普通参数进行校验了, 但普通参数校验失败抛出的异常不再是上面的`MethodArgumentNotValidException.class`, 而是`ConstraintViolationException`, 所以我们还需要在上面的异常拦截器中处理`ConstraintViolationException`异常

```java
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(ConstraintViolationException exs) {
        Map<String, String> errors = new HashMap<>();
        exs.getConstraintViolations().forEach(err -> errors.put(err.getPropertyPath().toString(), err.getMessage()));
        return errors;
    }
```
这里顺便说明一下`@Validated`和`@Valid`的区别, 简单的说`@Valid`是JSR303的标准实现, 而`@Validated`是Spring提供的实现, 它们主要区别如下: 


注解 | 范围 | 嵌套 | 校验组
---|--- | --- | ---
@Valid | 可以标记方法、构造函数、方法参数和成员属性（字段）上	| 支持 | 不支持
@Validated	 | 可以标记类、方法、方法参数，不能用在成员属性（字段）上 | 不支持 | 支持

#### 分组校验

> 当bean中的同一个字段需要不同校验规则时, 我们就需要分组校验

定义两个接口标识查询和更新

```java
public interface QueryAction {

}
public interface UpdateAction {

}
```

对User类稍作修改, 添加groups属性

```java
   @NotBlank(message = "姓名不能为空", groups = {UpdateAction.class, QueryAction.class})
    private String name;

    @NotNull(message = "年龄不能为空", groups = UpdateAction.class)
    @Min(value = 1, message = "请输入合法年龄", groups = UpdateAction.class)
    private Integer age;
```

这时候, 只用在controller的方法中使用`@Validated`提供的分组校验功能即可, 修改我们第一个接口的入参为:

```java
    @PostMapping("/users")
    public ResponseEntity<String> addUser(@Validated(value = UpdateAction.class) @RequestBody User user) {
        System.out.println(user.toString());
        return ResponseEntity.ok("validate success");
    }
```

指定这个方法使用更新操作的校验规则即可

#### 嵌套校验

当一个bean中存在另一个bean属性时, 可以使用嵌套校验, 只用在在bean中加上`@Valid`注解即可

```java
public class User {
    @Valid
    @NotNull
    private Action action;
}
```

#### 自定义校验

在业务中还会有一些特殊场景, 需要对某些字段增加自定义的校验逻辑, 比如想校验地址是否以`中国`开头, 这时候需要自定义注解, 并且实现`ConstraintValidator`接口自定义校验逻辑

```java
@Documented
@Constraint(validatedBy = StartWithValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface StartWithValidation {

    String message() default "不符合要求的初始值";

    String start() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

public class StartWithValidator implements ConstraintValidator<StartWithValidation, String> {

    private String start;

    @Override
    public void initialize(StartWithValidation constraintAnnotation) {
        start = constraintAnnotation.start();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (!StringUtils.isEmpty(s)) {
            return s.startsWith(start);
        }
        return false;
    }
}
```

这时候只需要在需要验证的字段上加上我们自定义的`@StartWithValidation`即可

### 总结

本篇文章到此over, [代码在此](https://github.com/LuoLiangDSGA/spring-learning/tree/master/boot-validation).