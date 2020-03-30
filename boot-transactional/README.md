## 在SpringMVC中优雅的拼接URL

### 背景

> 在日常开发中常常会遇到拼接URL的情况，大多数时候可以手动拼接字符串来达到目的，但是这样的方式不够优雅，同时容易出错。其实SpringMVC中已经给我们提供好了工具，这个工具就是UriComponentsBuilder类。

### 开始

UriComponentsBuilder给我们提供了多种方式来构建不可变的UriComponents实例，要使用这个工具，需要在maven中引入web的依赖：

```
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>5.1.0.RELEASE</version>
</dependency>
```
这里仅仅引入了web，如果是SpringBoot中，可以直接引入starter。

> 直接通过字符串拼接

```java
String url = "https://api.github.com" + "?" + "token=xxx" + "&name=" + "tomcat";
```

这种方式简单，写起来也不费劲，但问题就是容易出错。

> 通过Guava

Google的Guava中也提供了工具，方便我们拼接URL

```java
// 优雅的拼接出id=1&name=java这样的URL参数
Joiner.on("&").withKeyValueSeparator("=").join(ImmutableMap.of("id", 1, "name", "java"));
// 轻松把URL参数的值转为Map
Splitter.on("&").withKeyValueSeparator("=").split("id=1&name=java");
```

> UriComponentsBuilder

- 构造一个简单的URI

```
@Test
public void constructUri() {
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
            .scheme("http").host("www.github.com").path("/constructing-uri")
            .queryParam("name", "tom")
			.build();

    assertEquals("/constructing-uri", uriComponents.getPath());
    assertEquals("name=tom", uriComponents.getQuery());
    assertEquals("/constructing-uri", uriComponents.toUriString());
}
```

`uriComponents`可以使用`toUriString()`方法去输出拼接好的URI地址，这里的结果是：

```
http://www.github.com/constructing-uri?name=tom
```

可以看到`UriComponentsBuilder`是流式API的形式，代码也非常容易理解:

    1. scheme：协议，http或者https
    2. host：主机地址
    3. path：要访问的路径
    4. queryParam：url的参数，可以传入多个value
    
**这个例子在我们后台想重定向到某个地址时非常有用。**

- 构造一个编码的URI

有些参数中携带了特殊符号，这时候需要进行编码，`UriComponentsBuilder`编码也很简单：

```java
@Test
public void constructUriEncoded() {
	UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme("http").host("www.github.com").path("/constructing uri").build().encode();

	assertEquals("/constructing%20uri", uriComponents.getPath());
}
```

- 通过模板构造URI

我们可以通过占位符的方式来构造URI，这种方式是Spring中常常使用的方式，如果用过RestTemplate，那么一定不会陌生。

```java
@Test
public void constructUriFromTemplate() {
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
            .scheme("http").host("www.github.com").path("/{path-name}")
            .query("name={keyword}")
            .buildAndExpand("constructing-uri", "tomcat");

    assertEquals("/constructing-uri", uriComponents.getPath());
}
```

- 从已有的URI中获取信息

既然存在自己构造URI，那么也有从已知的URI中获取信息的需求，`UriComponentsBuilder`也可以做到

```java
@Test
public void fromUriString() {
    UriComponents result = UriComponentsBuilder
            .fromUriString("https://www.github.com/constructing-uri?name=tomcat").build();
    MultiValueMap<String, String> expectedQueryParams = new LinkedMultiValueMap<>(1);
    expectedQueryParams.add("name", "tomcat");
    assertEquals(result.getQueryParams(), expectedQueryParams);
}
```

使用`fromUriString()`方法，便可以把一个字符串URI转换为`UriComponents`对象，并且可以通过`getQueryParams()`方法取出参数。


### 总结

`UriComponentsBuilder`的用法远远不止这些，这些例子只是我日常开发中常常用到的，更多的可以参考[docs](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/util/UriComponentsBuilder.html)，代码已经同步到[男性交友网站](https://note.youdao.com/)。