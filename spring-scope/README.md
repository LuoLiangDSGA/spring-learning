## Spring中的Scope

### 概述
Spring框架中提供了多种不同类型的Bean scope，这些Scope定义了bean在其使用的上下文中的生命周期和可见性。当前版本的Spring中定义了如下6种不同类型的Scope:
- singleton
- prototype
- request
- session
- application
- websocket

其中最后四种类型只能在web应用程序中使用

### example
> singleton scope
定义成singleton的bean会被限制在每一个Spring IOC容器中只有一个实例

> prototype scope
定义成prototype的bean，在每次都会新建一个实例，同时不限制数量