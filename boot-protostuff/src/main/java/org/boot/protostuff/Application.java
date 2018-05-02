package org.boot.protostuff;

import com.alibaba.fastjson.JSON;
import org.boot.protostuff.entity.Group;
import org.boot.protostuff.entity.User;
import org.boot.protostuff.util.ProtostuffUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/1
 **/
@SpringBootApplication
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        //创建一个user对象
        User user = new User();
        user.setId("1");
        user.setAge(20);
        user.setName("张三");
        user.setDesc("programmer");
        //创建一个Group对象
        Group group = new Group();
        group.setId("1");
        group.setName("分组1");
        group.setUser(user);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            //使用ProtostuffUtils序列化
            byte[] data = ProtostuffUtils.serialize(group);
//            System.out.println("序列化后：" + Arrays.toString(data));
            Group result = ProtostuffUtils.deserialize(data, Group.class);
//            System.out.println("反序列化后：" + result.toString());
        }
        System.out.println("Protostuff耗时：" + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        for (int i = 0; i < 1000000; i++) {
            //使用ProtostuffUtils序列化
            String data = JSON.toJSONString(group);
//            System.out.println("序列化后：" + Arrays.toString(data));
            Group result = JSON.parseObject(data, Group.class);
//            System.out.println("反序列化后：" + result.toString());
        }
        System.out.println("Fastjson耗时：" + (System.currentTimeMillis() - start));
    }
}
