package org.boot.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.boot.config.valid.QueryAction;
import org.boot.config.valid.UpdateAction;

/**
 * @author fantasy
 * @date 2020/7/30
 */
public class User {

    private Long id;

    @NotBlank(message = "姓名不能为空", groups = {UpdateAction.class, QueryAction.class})
    private String name;

    @NotNull(message = "年龄不能为空", groups = UpdateAction.class)
    @Min(value = 1, message = "请输入合法年龄", groups = UpdateAction.class)
    private Integer age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
