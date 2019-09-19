## Spring中的Event实战

> 这篇文章用于介绍event在Spring中的使用，同时也是一篇偏实践性的文章。event在Spring中容易被忽略，但是这是一个非常有用的功能。与Spring中的许多其他功能一样，event也是ApplicationContext提供的功能之一。

### 概述
使用Event很简单，只需要注意几点：
- 自定义的event类需要继承`ApplicationEvent`类
- 事件发布者需要注入`ApplicationEventPublisher`对象
- 事件监听器需要实现`ApplicationListener`接口

### 定义事件

创建一个简单的事件类，用于在应用间传递消息

> NotifyEvent.java

```java
public class NotifyEvent extends ApplicationEvent {
    private String message;

    public NotifyEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```
此类继承了`ApplicationEvent`

### 监听器
监听器需要实现`ApplicationListener`接口，这是一个泛型接口，泛型代表事件类型，所以实现此接口的类可以监听指定的事件。

> `NotifyEventListener.java`

```java
@Component
public class NotifyEventListener implements ApplicationListener<NotifyEvent> {

    @Override
    public void onApplicationEvent(NotifyEvent event) {
        System.out.println("Received notify event - " + event.getMessage());
        System.out.println("process finished.");
    }
}
```
这里创建了`NotifyEventListener`并且对刚才定义的事件进行了监听。

### 事件发布者
在事件发布者中构造事件对象，然后把事件发送给所有监听了此事件的监听器。在这里发布事件只需要注入`ApplicationEventPublisher`即可。

```java
public class SpringEventApplication implements CommandLineRunner {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void run(String... args) throws Exception {
        String message = "start publish application event. ";
        System.out.println(message);
        applicationEventPublisher.publishEvent(new AnnotationDrivenNotifyEvent(this, message));
        System.out.println("publish finished.");
    }
}
...
```
**这里需要说明的是，Spring默认发送事件是同步的，在某些场景下执行某些耗时但不关心结果的处理，用同步处理是不如异步的，Spring是支持异步事件的**

### 异步事件
1. 在这里定义一个异步事件，和前面的`NotifyEvent`类构造其实是一样的。

> `AsynchronousNotifyEvent.java`

```java
public class AsynchronousNotifyEvent extends ApplicationEvent {
    private String message;

    public AsynchronousNotifyEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```
1. 增加异步配置

```java
@SpringBootConfiguration
public class AsynchronousSpringEventConfig {

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());

        return eventMulticaster;
    }
}
```
`ApplicationEventMulticaster`是Spring的事件广播器，`SimpleApplicationEventMulticaster`是它的一个实现。增加这个配置之后，发送事件就变成异步的了。

1. 可以定义一个异步监听器，用于测试是否异步

> `AsynchronousNotifyEventListener.java`

```java
@Component
public class AsynchronousNotifyEventListener implements ApplicationListener<AsynchronousNotifyEvent> {

    @Override
    public void onApplicationEvent(AsynchronousNotifyEvent event) {
        System.out.println("Received notify event - " + event.getMessage());
        try {
            for (int i = 0; i < 5; i++) {
                System.out.println("process " + i + " 条数据");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("process finished.");
    }
}
```
发布者此时构造发送`AsynchronousNotifyEvent`对象即可看到，在事件发送完成之后，listener中还在对数据进行处理。

### 注解驱动的异步事件

Spring4.2之后，实现自己的监听器不用实现`ApplicationListener`接口，可以使用`@EventListener`注解，代码变得更加简洁。

> 代码改造后如下

```java
@Component
public class AnnotationDrivenNotifyEventListener {

    @Async
    @EventListener
    public void receive(AnnotationDrivenNotifyEvent event) {
        System.out.println("Received notify event - " + event.getMessage());
        try {
            for (int i = 0; i < 5; i++) {
                System.out.println("process " + i + " 条数据");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("process finished.");
    }
}
```
这里就可以看到，使用了`@Async`和`@EventListener`接口来实现监听，并且异步的功能，这比上面通过配置全局广播器的方式更加灵活，同时，使用`@Async`注解别忘了加上`@EnableAsync`。

### Spring事件原理
首先可以看到`ApplicationEventPublisher`的`publishEvent`方法，这个方法在`AbstractApplicationContext`中

```java
	protected void publishEvent(Object event, @Nullable ResolvableType eventType) {
		Assert.notNull(event, "Event must not be null");

		// Decorate event as an ApplicationEvent if necessary
		ApplicationEvent applicationEvent;
		if (event instanceof ApplicationEvent) {
			applicationEvent = (ApplicationEvent) event;
		}
		else {
			applicationEvent = new PayloadApplicationEvent<>(this, event);
			if (eventType == null) {
				eventType = ((PayloadApplicationEvent<?>) applicationEvent).getResolvableType();
			}
		}

		// Multicast right now if possible - or lazily once the multicaster is initialized
		if (this.earlyApplicationEvents != null) {
			this.earlyApplicationEvents.add(applicationEvent);
		}
		else {
			// 获取ApplicationEventMulticaster，调用`multicastEvent`方法广播事件
			getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);
		}

		// Publish event via parent context as well...
		if (this.parent != null) {
			if (this.parent instanceof AbstractApplicationContext) {
				((AbstractApplicationContext) this.parent).publishEvent(event, eventType);
			}
			else {
				this.parent.publishEvent(event);
			}
		}
	}

	/**
	 * 获取ApplicationEventMulticaster
	 */
	ApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
		if (this.applicationEventMulticaster == null) {
			throw new IllegalStateException("ApplicationEventMulticaster not initialized - " +
					"call 'refresh' before multicasting events via the context: " + this);
		}
		return this.applicationEventMulticaster;
	}
```
这里的源码就可以解释上面我们配置的`ApplicationEventMulticaster`,同时，在`AbstractApplicationContext`中可以看到Spring的初始化核心方法`refresh()`的代码如下：

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
	synchronized (this.startupShutdownMonitor) {
		// Prepare this context for refreshing.
		prepareRefresh();

		// Tell the subclass to refresh the internal bean factory.
		ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

		// Prepare the bean factory for use in this context.
		prepareBeanFactory(beanFactory);

		try {
			// Allows post-processing of the bean factory in context subclasses.
			postProcessBeanFactory(beanFactory);

			// Invoke factory processors registered as beans in the context.
			invokeBeanFactoryPostProcessors(beanFactory);

			// Register bean processors that intercept bean creation.
			registerBeanPostProcessors(beanFactory);

			// Initialize message source for this context.
			initMessageSource();

			// Initialize event multicaster for this context.
			initApplicationEventMulticaster();

			// Initialize other special beans in specific context subclasses.
			onRefresh();

			// Check for listener beans and register them.
			registerListeners();

			// Instantiate all remaining (non-lazy-init) singletons.
			finishBeanFactoryInitialization(beanFactory);

			// Last step: publish corresponding event.
			finishRefresh();
		}

		catch (BeansException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception encountered during context initialization - " +
						"cancelling refresh attempt: " + ex);
			}

			// Destroy already created singletons to avoid dangling resources.
			destroyBeans();

			// Reset 'active' flag.
			cancelRefresh(ex);

			// Propagate exception to caller.
			throw ex;
		}

		finally {
			// Reset common introspection caches in Spring's core, since we
			// might not ever need metadata for singleton beans anymore...
			resetCommonCaches();
		}
	}
}
```
可以看到第27行的`initApplicationEventMulticaster()`方法，在这里对上下文的事件广播器进行初始化，33行的`registerListeners()`方法注册监听器，这两个方法的代码如下：

> `initApplicationEventMulticaster`

```java
protected void initApplicationEventMulticaster() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		// 如果用户手动新建了一个名为applicationEventMulticaster类型为ApplicationEventMulticaster的bean，则将这个bean作为事件广播器
		if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
			this.applicationEventMulticaster =
					beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
			}
		}
        // 否则新建一个SimpleApplicationEventMulticaster作为默认的事件广播器
		else {
			this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
			beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
			if (logger.isTraceEnabled()) {
				logger.trace("No '" + APPLICATION_EVENT_MULTICASTER_BEAN_NAME + "' bean, using " +
						"[" + this.applicationEventMulticaster.getClass().getSimpleName() + "]");
			}
		}
	}
```

> `registerListeners`，作用是listener添加到`ApplicationEventMulticaster`中

```java
protected void registerListeners() {
	// Register statically specified listeners first.
	for (ApplicationListener<?> listener : getApplicationListeners()) {
		getApplicationEventMulticaster().addApplicationListener(listener);
	}

	// Do not initialize FactoryBeans here: We need to leave all regular beans
	// uninitialized to let post-processors apply to them!
	String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
	for (String listenerBeanName : listenerBeanNames) {
		getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
	}

	// Publish early application events now that we finally have a multicaster...
	Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
	this.earlyApplicationEvents = null;
	if (earlyEventsToProcess != null) {
		for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
			getApplicationEventMulticaster().multicastEvent(earlyEvent);
		}
	}
}
```
通过前面的代码，知道了`ApplicationEventMulticaster`是如何被构建的，那么现在可以看看它是怎么广播事件的。

```java
@Override
public void multicastEvent(final ApplicationEvent event, @Nullable ResolvableType eventType) {
	ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
	// 获取SimpleApplicationEventMulticaster中的线程执行器，
	// 如果存在线程执行器则在新线程中异步执行，否则直接同步执行监听器中的方法
	Executor executor = getTaskExecutor();
	for (ApplicationListener<?> listener : getApplicationListeners(event, type)) {
		if (executor != null) {
			executor.execute(() -> invokeListener(listener, event));
		}
		else {
			invokeListener(listener, event);
		}
	}
}

protected void invokeListener(ApplicationListener<?> listener, ApplicationEvent event) {
	ErrorHandler errorHandler = getErrorHandler();
	if (errorHandler != null) {
		try {
			doInvokeListener(listener, event);
		}
		catch (Throwable err) {
			errorHandler.handleError(err);
		}
	}
	else {
		doInvokeListener(listener, event);
	}
}
```
这里可以解释前面我们通过配置设置线程池之后，事件发送变成了异步，但是通过这种方式有一个问题，就是所有的事件发送都变成异步了，所以还是建议使用@Async的方式进行异步。

### 总结
本篇文章介绍了Event在Spring中的使用方法，并且对Spring事件机制进行了简单的梳理，代码在[github](https://github.com/LuoLiangDSGA/spring-learning/tree/master/spring-event)上
