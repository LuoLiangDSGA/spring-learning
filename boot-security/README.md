## SpringBoot通过Spring security和JWT来控制权限的实践
> 在项目开发中，我们要保证API只能被授权用户访问，所以安全问题是至关重要的。所以我们需要对API划分权限，当接收到用户的API请求时，需要做鉴权处理。目前实现鉴权的方案也有很多，常见的有Session保存用户信息鉴权，Oauth鉴权，Token鉴权。Session在分布式应用下比较无力，Oauth多用于开放平台，比如微信，所以本文使用JWT进行鉴权。Java主流安全认证的框架有Spring Security和Apache Shiro，本文会使用Spring Security做安全认证框架，搭建一个基础安全认证的框架，对不同用户角色的访问权限进行控制。

### 准备
需要准备好下面的基础环境和组件：
- ![](https://img.shields.io/badge/java_8-✓-blue.svg)
- ![](https://img.shields.io/badge/spring_boot-✓-blue.svg)
- ![](https://img.shields.io/badge/mysql-✓-blue.svg)
- ![](https://img.shields.io/badge/redis-✓-blue.svg)
### 开始

> JWT  

构成  
header.payload.signature

header
- 声明类型 jwt
- 声明加密的算法 hs256
```
{
  "alg": "HS256",
  "typ": "JWT"
}
```

> Spring Security
- GrantedAuthority  所有的Authentication实现类都保存了一个GrantedAuthority列表，其表示用户所具有的权限。
- UserDetailsService
- AuthenticationManager 