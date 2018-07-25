## SpringBoot使用Spring Security和JWT控制访问权限
> 在项目开发中，我们要保证API只能被授权用户访问，所以安全问题是至关重要的。所以我们需要对API划分权限，当接收到用户的API请求时，需要做鉴权处理。目前实现鉴权的方案也有很多，常见的有Session保存用户信息鉴权，Oauth鉴权，Token鉴权。Session在分布式应用下比较无力，Oauth多用于开放平台，比如微信，所以本文使用JWT进行鉴权。Java主流安全认证的框架有Spring Security和Apache Shiro，本文会使用Spring Security做安全认证框架，搭建一个基础安全认证的框架，对不同用户角色的访问权限进行控制。

### 准备
需要准备好下面的基础环境和组件：
- ![](https://img.shields.io/badge/java_8-✓-blue.svg)
- ![](https://img.shields.io/badge/spring_boot-✓-blue.svg)
- ![](https://img.shields.io/badge/mysql-✓-blue.svg)
- ![](https://img.shields.io/badge/redis-✓-blue.svg)
### 前言

> JWT  

JWT是`Json Web Token`的简写，JWT官方是这样定义的：JWT是一个开放的，基于行业标准[RFC 7519](https://tools.ietf.org/html/rfc7519)定义的方法，用于在双方之间安全的传递数据。这些东西可能很难理解，简单地说，JWT就是一个轻巧的规范，使用它可以让我们的客户端和服务器进行安全可靠的信息交流。服务端不再保存session相关的信息，客户端在每次向服务器发起请求时都需要带上JWT，此时服务端是无状态的，如果服务端是集群模式，这样也方便服务端进行水平扩展。

#### JWT的构成
JWT由以下三部分构成，用`.`隔开，同时每段都用Base64进行编码，可以使用解析器进行解析。
```
header.payload.signature
```
比如下面就是一个JWT
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```
1. header（头部）  

  头部通常由两部分组成，一个是令牌的类型，即JWT，以及使用的散列算法，例如HS256.例如上面的JWT例子，第一段进行base64解码之后变成了如下的json.
```
{
  "alg": "HS256",
  "typ": "JWT"
}
```
2. 负载  
  
  负载中包含的是用户的相关数据，比如名称，过期时间等，也可以是开发者自定义的字段。同时，官方提供了一些建议使用的字段。
  ```
  - iss（发行人）
  - exp（过期时间）
  - sub（主题）
  - aud（受众）
  - iat (签发时间)
  - nbf (生效时间)
  - jti (编号)
  ```
  这些信息经过base64编码之后变成了上面例子中的第二段，解码之后如下：
  ```
  {
    "sub":"1234567890",
    "name":"John Doe",
    "iat":1516239022
  }
  ```
乍眼一看可能这串base64字符我们看不懂，但是进行解码之后这些信息完全是公开的，所以切记不能把敏感信息放在这个地方。

3. 签名  
   
要创建签名，需要使用header和payload，指定的密钥(secret)，以及header中指定的算法，按照下面的方式来生成签名
```
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret)
```

最后把三段用`.`拼接起来，一个JWT就完成了。JWT的基础知识就介绍这么多，更多的资料可以自行谷歌。

> Spring Security     

Spring Security是Spring提供的一个功能强大，可以高度自定义的身份验证和访问控制框架，它以Spring应用为基础，为Spring应用提供安全服务。所以相比于其他安全控制框架（Apache Shiro），Spring Security和Spring应用的集成将会更加的简单和方便，如果熟悉Spring应用的开发者，Spring Security也可以很快地上手。

主要特性：
- 可扩展且全面地支持身份验证和授权。
- 有效防止session fixation（会话固定），clickjacking（点击劫持），CSRF（跨站请求伪造）等攻击。
- 可以与Servlet API进行集成。
- 可以选择和Spring Web MVC集成。

更多请参考[Spring Security Reference](https://spring.io/projects/spring-security#overview)


### 开始

使用[Spring Initializr](https://start.spring.io/) 新建一个SpringBoot工程，在pom.xml中加入基础依赖。
```java
  <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>${jjwt.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
```


> 数据模型  

首先，我们要有用户和角色，用户表应该是下面这样的：
| ID | USERNAME | PASSWORD | 
| - | :-: | :-: | 
| 1 | Gryffindor| AjWICTKOPtSeZu1PGmoMsbPm | 

不同的用户有不同的角色，所以需要一张如下的角色表：
| ID | ROLENAME  
| - | :-: 
| 1 | USER

用户和角色需要建立关联，所以需要一张用户角色关系表：
| USER_ID | ROLE_ID  
| - | :-: 
| 1 | 1

编写一个User类：
```java

@Entity
@Data
public class User {
    @Id
    @GeneratedValue
    private Integer id;

    @Size(min = 1, max = 32, message = "Minimum username length: 4 characters，the maximum 32 characters ")
    @Column(unique = true, nullable = false)
    private String username;

    @Size(min = 6, message = "Minimum password length: 8 characters")
    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;
}
```
这里使用了lombok提供的@Data注解自动生成Getter，Settter。以及spring-data-jpa的注解来做实体和数据库的映射。

再编写一个Role类，定义两个角色：
```java
public enum Role implements GrantedAuthority {
    /**
     * 管理员
     */
    ROLE_ADMIN,
    /**
     * 用户
     */
    ROLE_USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
```





























- GrantedAuthority  所有的Authentication实现类都保存了一个GrantedAuthority列表，其表示用户所具有的权限。
- UserDetailsService
- AuthenticationManager 