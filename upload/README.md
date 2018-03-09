## Vue+SpringBoot实现前后端分离的文件上传
这篇文章需要一定Vue和SpringBoot的知识，分为两个项目，一个是前端Vue项目，一个是后端SpringBoot项目。

### 后端项目搭建
我使用的是SpringBoot1.5.10+JDK8+IDEA
使用IDEA新建一个SpringBoot项目，一直点next即可

![](http://p41b81jeu.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-02-12%2019.15.01.png)

项目创建成功后，maven的pom配置如下

```
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!--加入web模块-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.39</version>
        </dependency>
    </dependencies>
```

接下来编写上传的API接口
```
@RestController
@RequestMapping("/upload")
@CrossOrigin
public class UploadController {
    @Value("${prop.upload-folder}")
    private String UPLOAD_FOLDER;
    private Logger logger = LoggerFactory.getLogger(UploadController.class);

    @PostMapping("/singlefile")
    public Object singleFileUpload(MultipartFile file) {
        logger.debug("传入的文件参数：{}", JSON.toJSONString(file, true));
        if (Objects.isNull(file) || file.isEmpty()) {
            logger.error("文件为空");
            return "文件为空，请重新上传";
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_FOLDER + file.getOriginalFilename());
            //如果没有files文件夹，则创建
            if (!Files.isWritable(path)) {
                Files.createDirectories(Paths.get(UPLOAD_FOLDER));
            }
            //文件写入指定路径
            Files.write(path, bytes);
            logger.debug("文件写入成功...");
            return "文件上传成功";
        } catch (IOException e) {
            e.printStackTrace();
            return "后端异常...";
        }
    }
}
```
- CrossOrigin注解：解决跨域问题，因为前后端完全分离，跨域问题在所难免，加上这个注解会让Controller支持跨域，如果去掉这个注解，前端Ajax请求不会到后端。这只是跨域的一种解决方法，还有其他解决方法这篇文章先不涉及。
- MultipartFile：SpringMVC的multipartFile对象，用于接收前端请求传入的FormData。
- PostMapping是Spring4.3以后引入的新注解，是为了简化HTTP方法的映射，相当于我们常用的@RequestMapping(value = "/xx", method = RequestMethod.POST).

#### 后端至此已经做完了，很简单。

### 前端项目搭建
我使用的是Node8+Webpack3+Vue2

本地需要安装node环境，且安装Vue-cli，使用Vue-cli生成一个Vue项目。

![](http://p41b81jeu.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-02-12%2019.58.34.png)

项目创建成功之后，用WebStorm打开，就可以写一个简单的上传例子了，主要代码如下：

```
<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <form>
      <input type="file" @change="getFile($event)">
      <button class="button button-primary button-pill button-small" @click="submit($event)">提交</button>
    </form>
  </div>
</template>

<script>
  import axios from 'axios';

  export default {
    name: 'HelloWorld',
    data() {
      return {
        msg: 'Welcome to Your Vue.js App',
        file: ''
      }
    },
    methods: {
      getFile: function (event) {
        this.file = event.target.files[0];
        console.log(this.file);
      },
      submit: function (event) {
        //阻止元素发生默认的行为
        event.preventDefault();
        let formData = new FormData();
        formData.append("file", this.file);
        axios.post('http://localhost:8082/upload/singlefile', formData)
          .then(function (response) {
            alert(response.data);
            console.log(response);
            window.location.reload();
          })
          .catch(function (error) {
            alert("上传失败");
            console.log(error);
            window.location.reload();
          });
      }
    }
  }
</script>
```
使用Axios向后端发送Ajax请求，使用H5的FormData对象封装图片数据

### 测试
启动服务端，直接运行BootApplication类的main方法，端口8082
![](http://p41b81jeu.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-02-12%2020.07.49.png)

启动前端，端口默认8080，cd到前端目录下，分别执行：

* npm install
* npm run dev

启动成功后访问localhost:8080

![](http://p41b81jeu.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-02-12%2020.12.14.png)

选择一张图片上传，可以看到，上传成功之后，后端指定目录下也有了图片文件

![](http://p41b81jeu.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-02-12%2020.14.23.png)

![](http://p41b81jeu.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-02-12%2020.14.48.png)

#### 总结，到这里，一个前后端分离的上传例子就做完了，如有不对的，请指正，大家共同进步。最后，附上源码，欢迎star：。
