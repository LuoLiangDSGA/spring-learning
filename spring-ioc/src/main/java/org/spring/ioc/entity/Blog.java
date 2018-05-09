package org.spring.ioc.entity;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/9
 **/
public class Blog {
    private String name;

    private String content;

    private Long date;

    private Author author;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", author=" + author +
                '}';
    }
}
