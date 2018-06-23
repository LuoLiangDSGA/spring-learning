---
title: 使用Docker容器化SpringBoot+Dubbo应用的实践
date: 2018-06-10 23:04:36
categories: "SpringBoot"
tags: 
    - SpringBoot
    - Dubbo
    - Docker
---

> 原文地址:  [luoliangDSGA's blog](https://luoliangdsga.github.io/2018/06/10/使用Docker容器化SpringBoot-Dubbo应用的实践/)  
> 博客地址:  [https://luoliangdsga.github.io](https://luoliangdsga.github.io)     
欢迎转载，转载请注明作者及出处，谢谢！

## SpringBoot+Vue.js前后端分离实现大文件上传

**目前不可用，还在开发中**
> 之前写过一篇[SpringBoot+Vue](https://luoliangdsga.github.io/2018/06/10/使用Docker容器化SpringBoot-Dubbo应用的实践/)前后端分离实现文件上传的博客，但是那篇博客主要针对的是小文件的上传，如果是大文件，一次性上传，将会出现不可预期的错误。所以需要对大文件进行分块，再依次上传，这样处理对于服务器容错更好处理，更容易实现断点续传、跨浏览器上传等功能。本文也会实现断点，跨浏览器继续上传的功能。

### 开始
> 效果预览

![](https://raw.githubusercontent.com/simple-uploader/vue-uploader/master/example/simple-uploader.gif)
此处用到了[这位大佬](https://github.com/dolymood/dolymood.github.com/blob/master/_posts/js/2017-08-23-Vue上传组件vue-simple-uploader.md)的Vue上传组件，此图也是引用自他的GitHub，感谢这位大佬。

> 需要准备好基础环境
- Java 
- Node 
- MySQL   

> 准备好这些之后，就可以往下看了。

### 后端


### 前端