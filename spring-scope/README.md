## Spring中的Scope
Scope用于描述Spring容器如何新建Bean实例

### 概述
Spring框架中提供了多种不同类型的Bean scope，这些Scope定义了bean在其使用的上下文中的生命周期和可见性。当前版本的Spring中定义了如下6种不同类型的Scope:
- singleton
> bean会被限制在每一个Spring IOC容器中只有一个实例，Spring默认配置即为singleton
- prototype
> 每次调用都会新建一个Bean实例

下面四种类型只能在web应用程序中使用

- request
> Web项目中，给每一个http request新建一个Bean实例
- session
> Web项目中，给每一个http session新建一个Bean实例
- application
> Web项目中，会在整个ServletContext的生命周期中新建一个Bean实例
- websocket
> Web项目中，

### example
> singleton scope
定义成singleton的bean会被限制在每一个Spring IOC容器中只有一个实例，Spring默认配置即为singleton

> prototype scope
定义成prototype的bean，在每次都会新建一个实例



