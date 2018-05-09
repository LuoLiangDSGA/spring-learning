package org.spring.ioc.entity;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/9
 **/
public class User {
    private String id;

    private String name;

    private Integer age;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
