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

### 后端
> 新建一个SpringBoot项目，我这里使用的是SpringBoot2，引入mvc，jpa，mysql相关的依赖。
```java
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
        </dependency>
    </dependencies>
```

> 在yml中配置mvc以及数据库连接等属性
```java
server:
  port: 8081
  servlet:
    path: /boot

spring:
  servlet:
    multipart:
      max-file-size: 20MB
      max-request-size: 20MB
  datasource:
    url: jdbc:mysql://localhost:3306/test?characterEncoding=utf-8&useSSL=false
    username: root
    password: root
    driver-class-name: com.mysql.jdbc.Driver
  jpa:
    properties:
      hibernate:
        hbm2ddl:
          auto: update
    show-sql: true

logging:
  level:
    org.boot.uploader.*: debug

prop:
  upload-folder: files
```

> 定义文件上传相关的类，一个是FileInfo，代表文件的基础信息；一个是Chunk，代表文件块。

> FileInfo.java
```java
@Data
@Entity
public class FileInfo implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String identifier;

    @Column(nullable = false)
    private Long totalSize;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String location;
}
```
> Chunk.java
```java
@Data
@Entity
public class Chunk implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    /**
     * 当前文件块，从1开始
     */
    @Column(nullable = false)
    private Integer chunkNumber;
    /**
     * 分块大小
     */
    @Column(nullable = false)
    private Long chunkSize;
    /**
     * 当前分块大小
     */
    @Column(nullable = false)
    private Long currentChunkSize;
    /**
     * 总大小
     */
    @Column(nullable = false)
    private Long totalSize;
    /**
     * 文件标识
     */
    @Column(nullable = false)
    private String identifier;
    /**
     * 文件名
     */
    @Column(nullable = false)
    private String filename;
    /**
     * 相对路径
     */
    @Column(nullable = false)
    private String relativePath;
    /**
     * 总块数
     */
    @Column(nullable = false)
    private Integer totalChunks;
    /**
     * 文件类型
     */
    @Column
    private String type;
    @Transient
    private MultipartFile file;
}
```

> 编写文件块相关的业务操作
```java
@Service
public class ChunkServiceImpl implements ChunkService {
    @Resource
    private ChunkRepository chunkRepository;

    @Override
    public void saveChunk(Chunk chunk) {
        chunkRepository.save(chunk);
    }

    @Override
    public boolean checkChunk(String identifier, Integer chunkNumber) {
        Specification<Chunk> specification = (Specification<Chunk>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("identifier"), identifier));
            predicates.add(criteriaBuilder.equal(root.get("chunkNumber"), chunkNumber));

            return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        };

        return chunkRepository.findOne(specification).orElse(null) == null;
    }

}
```
1. checkChunk()方法会根据文件唯一标识，和当前块数判断是否已经上传过这个块。
2. 这里只贴了ChunkService的代码，其他的代码只是jpa简单的存取。

> 接下来就是编写最重要的controller了
```java
@RestController
@RequestMapping("/uploader")
@Slf4j
public class UploadController {
    @Value("${prop.upload-folder}")
    private String uploadFolder;
    @Resource
    private FileInfoService fileInfoService;
    @Resource
    private ChunkService chunkService;

    @PostMapping("/chunk")
    public String uploadChunk(Chunk chunk) {
        MultipartFile file = chunk.getFile();
        log.debug("file originName: {}, chunkNumber: {}", file.getOriginalFilename(), chunk.getChunkNumber());

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(generatePath(uploadFolder, chunk));
            //文件写入指定路径
            Files.write(path, bytes);
            log.debug("文件 {} 写入成功, uuid:{}", chunk.getFilename(), chunk.getIdentifier());
            chunkService.saveChunk(chunk);

            return "文件上传成功";
        } catch (IOException e) {
            e.printStackTrace();
            return "后端异常...";
        }
    }

    @GetMapping("/chunk")
    public Object checkChunk(Chunk chunk, HttpServletResponse response) {
        if (chunkService.checkChunk(chunk.getIdentifier(), chunk.getChunkNumber())) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }

        return chunk;
    }

    @PostMapping("/mergeFile")
    public String mergeFile(FileInfo fileInfo) {
        String path = uploadFolder + "/" + fileInfo.getIdentifier() + "/" + fileInfo.getFilename();
        String folder = uploadFolder + "/" + fileInfo.getIdentifier();
        merge(path, folder);
        fileInfo.setLocation(path);
        fileInfoService.addFileInfo(fileInfo);

        return "合并成功";
    }
}
```
1. 文章开头就提到了前后端分离，既然是前后端分离，肯定会涉及到跨域问题，在上一篇文章中是通过springMVC的@CrossOrigin注解来解决跨域问题，这里并没有使用这个注解，在下面的前端项目中会使用一个node的中间件来做代理，解决跨域的问题。
2. 可以看到有两个/chunk路由，第一个是post方法，用于上传并存储文件块，需要对文件块名进行编号，再存储在指定路径下；第二个是get方法，前端上传之前会先进行检测，如果此文件块已经上传过，就可以实现断点和快传。
3. /mergeFile用于合并文件，在所有块上传完毕后，前端会调用此接口进行制定文件的合并。其中的merge方法是会遍历指定路径下的文件块，并且按照文件名中的数字进行排序后，再合并成一个文件，否则合并后的文件会无法使用，代码如下：
```java
public static void merge(String targetFile, String folder) {
        try {
            Files.createFile(Paths.get(targetFile));
            Files.list(Paths.get(folder))
                    .filter(path -> path.getFileName().toString().contains("-"))
                    .sorted((o1, o2) -> {
                        String p1 = o1.getFileName().toString();
                        String p2 = o2.getFileName().toString();
                        int i1 = p1.lastIndexOf("-");
                        int i2 = p2.lastIndexOf("-");
                        return Integer.valueOf(p2.substring(i2)).compareTo(Integer.valueOf(p1.substring(i1)));
                    })
                    .forEach(path -> {
                        try {
                            //以追加的形式写入文件
                            Files.write(Paths.get(targetFile), Files.readAllBytes(path), StandardOpenOption.APPEND);
                            //合并后删除该块
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```
> 到这里，后端主要的逻辑已经写完了，下面开始编写前端的部分。
### 前端
前端我直接clone了[vue-uploader](https://github.com/simple-uploader/vue-uploader)，在这个代码的基础上进行了修改。
> App.vue
```javascript
<template>
  <uploader :options="options" :file-status-text="statusText" class="uploader-example" ref="uploader"
            @file-complete="fileComplete" @complete="complete"></uploader>
</template>

<script>
  import axios from 'axios'
  import qs from 'qs'

  export default {
    data() {
      return {
        options: {
          target: '/boot/uploader/chunk',
          testChunks: true,
          simultaneousUploads: 1,
          chunkSize: 10 * 1024 * 1024
        },
        attrs: {
          accept: 'image/*'
        },
        statusText: {
          success: '成功了',
          error: '出错了',
          uploading: '上传中',
          paused: '暂停中',
          waiting: '等待中'
        }
      }
    },
    methods: {
      // 上传完成
      complete() {
        console.log('complete', arguments)
      },
      // 一个根文件（文件夹）成功上传完成。
      fileComplete() {
        console.log('file complete', arguments)
        const file = arguments[0].file;
        axios.post('/boot/uploader/mergeFile', qs.stringify({
          filename: file.name,
          identifier: arguments[0].uniqueIdentifier,
          totalSize: file.size,
          type: file.type
        })).then(function (response) {
          console.log(response);
        }).catch(function (error) {
          console.log(error);
        });
      }
    },
    mounted() {
      this.$nextTick(() => {
        window.uploader = this.$refs.uploader.uploader
      })
    }
  }
</script>
...
```

配置说明：
1. target 目标上传 URL，可以是字符串也可以是函数，如果是函数的话，则会传入 Uploader.File 实例、当前块 Uploader.Chunk 以及是否是测试模式，默认值为 '/'。
2. chunkSize 分块时按照该值来分。最后一个上传块的大小是可能是大于等于1倍的这个值但是小于两倍的这个值大小，默认 1*1024*1024。
3. testChunks 是否测试每个块是否在服务端已经上传了，主要用来实现秒传、跨浏览器上传等，默认true。
4. simultaneousUploads 并发上传数，默认3。  

**更多说明请直接参考[vue-uploader](https://github.com/simple-uploader/vue-uploader)**

> 解决跨域问题

这里使用了http-proxy-middleware这个node中间件，可以对前端的请求进行转发，转发到指定的路由。
#### 在index.js中进行配置，如下：
```javascript
dev: {
    env: require('./dev.env'),
    port: 8080,
    autoOpenBrowser: true,
    assetsSubDirectory: '',
    assetsPublicPath: '/',
    proxyTable: {
      '/boot': {
        target: 'http://localhost:8081',
        changeOrigin: true  //如果跨域，则需要配置此项
      }
    },
    // CSS Sourcemaps off by default because relative paths are "buggy"
    // with this option, according to the CSS-Loader README
    // (https://github.com/webpack/css-loader#sourcemaps)
    // In our experience, they generally work as expected,
    // just be aware of this issue when enabling this option.
    cssSourceMap: false
  }
```
proxyTable表示代理配置表，将特定的请求代理到指定的API接口，这里是将'localhost:8080/boot/xxx'代理到'http://localhost:8081/boot/xxx'。

> 现在可以开始验证了，分别启动前后端的项目
- 前端
```
npm install
npm run dev
```
- 后端
可以通过command line，也可以直接运行BootUploaderApplication的main()方法

运行效果就像最开始的那张图，上传暂停之后更换浏览器，选择同一个文件可以实现继续上传的效果，大家可以自行进行尝试:blush:，代码会在我的[GitHub](https://github.com/LuoLiangDSGA/spring-learning/tree/master/boot-uploader)上进行更新。

### 最后
整篇文章到这里差不多就结束了，这个项目主要作用也是作为demo用来学习，有很多可以扩展的地方，肯定也会有不完善的地方，有更好的方法也希望能指出，共同交流学习:smiley:。