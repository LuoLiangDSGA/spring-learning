package org.spring.ioc.entity;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/9
 **/
public class Author {
    private String name;

    private Integer age;

    private String url;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", url='" + url + '\'' +
                '}';
    }
}
