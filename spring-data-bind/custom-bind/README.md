## SpringMvc数据绑定-自定义注解

> SpringMVC中给我们提供了很多方便的注解用于绑定数据，比如`@RequestParam`，`@PathVariable`，就可以把接收到的参数进行绑定。但在实际场景中会有自定义注解的需求，比如权限校验，在每个controller方法中都需要根据请求的header去获取token，根据token做自己的业务逻辑。

### 开始

- 既然SpringMVC中给我们实现了那么多的默认注解，那就看看SpringMVC是怎么做到的，下面是`PathVariableMethodArgumentResolver`的部分源码：

```java
/**
 * Resolves method arguments annotated with an @{@link PathVariable}.
 *
 * <p>An @{@link PathVariable} is a named value that gets resolved from a URI template variable.
 * It is always required and does not have a default value to fall back on. See the base class
 * {@link org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver}
 * for more information on how named values are processed.
 *
 * <p>If the method parameter type is {@link Map}, the name specified in the annotation is used
 * to resolve the URI variable String value. The value is then converted to a {@link Map} via
 * type conversion, assuming a suitable {@link Converter} or {@link PropertyEditor} has been
 * registered.
 *
 * <p>A {@link WebDataBinder} is invoked to apply type conversion to resolved path variable
 * values that don't yet match the method parameter type.
 *
 * @author Rossen Stoyanchev
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.1
 */
public class PathVariableMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver
		implements UriComponentsContributor {

	private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);


	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		if (!parameter.hasParameterAnnotation(PathVariable.class)) {
			return false;
		}
		if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
			PathVariable pathVariable = parameter.getParameterAnnotation(PathVariable.class);
			return (pathVariable != null && StringUtils.hasText(pathVariable.value()));
		}
		return true;
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		PathVariable ann = parameter.getParameterAnnotation(PathVariable.class);
		Assert.state(ann != null, "No PathVariable annotation");
		return new PathVariableNamedValueInfo(ann);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Nullable
	protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
		Map<String, String> uriTemplateVars = (Map<String, String>) request.getAttribute(
				HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		return (uriTemplateVars != null ? uriTemplateVars.get(name) : null);
	}

	@Override
	protected void handleMissingValue(String name, MethodParameter parameter) throws ServletRequestBindingException {
		throw new MissingPathVariableException(name, parameter);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void handleResolvedValue(@Nullable Object arg, String name, MethodParameter parameter,
			@Nullable ModelAndViewContainer mavContainer, NativeWebRequest request) {

		String key = View.PATH_VARIABLES;
		int scope = RequestAttributes.SCOPE_REQUEST;
		Map<String, Object> pathVars = (Map<String, Object>) request.getAttribute(key, scope);
		if (pathVars == null) {
			pathVars = new HashMap<>();
			request.setAttribute(key, pathVars, scope);
		}
		pathVars.put(name, arg);
	}

	@Override
	public void contributeMethodArgument(MethodParameter parameter, Object value,
			UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {

		if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
			return;
		}

		PathVariable ann = parameter.getParameterAnnotation(PathVariable.class);
		String name = (ann != null && StringUtils.hasLength(ann.value()) ? ann.value() : parameter.getParameterName());
		String formatted = formatUriValue(conversionService, new TypeDescriptor(parameter.nestedIfOptional()), value);
		uriVariables.put(name, formatted);
	} 
	...
}
```

> 可以看到这个类的注释上说明了这是实现`@PathVariable`注解的类，它的父类是`AbstractNamedValueMethodArgumentResolver`，`AbstractNamedValueMethodArgumentResolver`实现了`HandlerMethodArgumentResolver`，正是`HandlerMethodArgumentResolver`这个类，用来实现了自定义的注解。

- 那么`PathVariableMethodArgumentResolver`这个类在什么时候用的呢？可以参考`RequestMappingHandlerAdapter`:

```java
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter
		implements BeanFactoryAware, InitializingBean {
		@Override
	public void afterPropertiesSet() {
		// Do this first, it may add ResponseBody advice beans
		initControllerAdviceCache();

		if (this.argumentResolvers == null) {
			List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
			this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
		}
		if (this.initBinderArgumentResolvers == null) {
			List<HandlerMethodArgumentResolver> resolvers = getDefaultInitBinderArgumentResolvers();
			this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
		}
		if (this.returnValueHandlers == null) {
			List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
			this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
		}
	}
	
	private List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

		// Annotation-based argument resolution
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), false));
		resolvers.add(new RequestParamMapMethodArgumentResolver());
		resolvers.add(new PathVariableMethodArgumentResolver());
		resolvers.add(new PathVariableMapMethodArgumentResolver());
		resolvers.add(new MatrixVariableMethodArgumentResolver());
		resolvers.add(new MatrixVariableMapMethodArgumentResolver());
		resolvers.add(new ServletModelAttributeMethodProcessor(false));
		resolvers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(), this.requestResponseBodyAdvice));
		resolvers.add(new RequestPartMethodArgumentResolver(getMessageConverters(), this.requestResponseBodyAdvice));
		resolvers.add(new RequestHeaderMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new RequestHeaderMapMethodArgumentResolver());
		resolvers.add(new ServletCookieValueMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new ExpressionValueMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new SessionAttributeMethodArgumentResolver());
		resolvers.add(new RequestAttributeMethodArgumentResolver());

		// Type-based argument resolution
		resolvers.add(new ServletRequestMethodArgumentResolver());
		resolvers.add(new ServletResponseMethodArgumentResolver());
		resolvers.add(new HttpEntityMethodProcessor(getMessageConverters(), this.requestResponseBodyAdvice));
		resolvers.add(new RedirectAttributesMethodArgumentResolver());
		resolvers.add(new ModelMethodProcessor());
		resolvers.add(new MapMethodProcessor());
		resolvers.add(new ErrorsMethodArgumentResolver());
		resolvers.add(new SessionStatusMethodArgumentResolver());
		resolvers.add(new UriComponentsBuilderMethodArgumentResolver());

		// Custom arguments
		if (getCustomArgumentResolvers() != null) {
			resolvers.addAll(getCustomArgumentResolvers());
		}

		// Catch-all
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), true));
		resolvers.add(new ServletModelAttributeMethodProcessor(true));

		return resolvers;
	}
}
```

> 可以看到`RequestMappingHandlerAdapter`实现了Spring生命周期中的`InitializingBean`接口，并且重写了`afterPropertiesSet()`方法，这里面调用了`getDefaultArgumentResolvers()`，这个方法把默认的解析器都添加了进去，`@PathVariable`注解的解析器就是这里加进去的，`RequestMappingHandlerAdapter`是SpringMVC中一个很重要的类，SpringMVC中的大多数组件都是在这里进行配置的，比如Converter，ViewResolver。

- 看了默认的实现，现在我们来看看`HandlerMethodArgumentResolver`这个类，这个方法中只有方法：

```java
public interface HandlerMethodArgumentResolver {

	/**
	 * 用于判断是否支持对某种参数的解析
	 */
	boolean supportsParameter(MethodParameter parameter);

	/**
	 * 将请求中的参数值解析为某种对象
	 */
	@Nullable
	Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception;

}
```

- 编写自定义的HandlerMethodArgumentResolver

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Token {
}
```

```java
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private RedisService redisService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        // 判断是否有Token这个注解
        return methodParameter.hasParameterAnnotation(Token.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String token;
        if (methodParameter.getParameterType().equals(User.class) && Objects.nonNull(token =
                nativeWebRequest.getNativeRequest(HttpServletRequest.class).getHeader("token"))) {
            return redisService.get(token);
        }
        return null;
    }
}
```
> 这个自定义解析器会判断是否是`@Token`这个注解，然后从header中取出token，并且转换为User对象。

- 编写一个业务类模拟redis操作

```java
/**
 * @author luoliang
 * @date 2019/10/8
 * 模拟redis操作业务类
 */
@Service
public class RedisService {

    public Object get(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return User.builder().id(key).name("二哈").build();
    }

    public void set(String key, Object value) {
        // todo
    }
}
```

- 做完这些工作之后需要把自定义解析器加入到配置里

```java
@SpringBootConfiguration
public class WebMvcconfig extends WebMvcConfigurationSupport {
    private final UserArgumentResolver userArgumentResolver;

    public WebMvcconfig(UserArgumentResolver userArgumentResolver) {
        this.userArgumentResolver = userArgumentResolver;
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }
}
```

- 编写Controller，参数就可以使用自定义的注解了

```java
    @GetMapping("/user")
    public ResponseEntity<User> getUser(@Token User user) {
        return ResponseEntity.ok(user);
    }
```

### 总结

本篇文章主要记录了SpringMVC中自定义解析器的使用，同时举了一个真实场景的例子，旨在于知道怎么使用SpringMVC给我们带来的便利的同时，知道其原理。