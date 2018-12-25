package org.boot.mybatis.mapper;

import org.apache.ibatis.annotations.*;
import org.boot.mybatis.model.User;

import java.util.List;

/**
 * @author luoliang
 * @date 2018/12/20
 */
@Mapper
public interface UserMapper {
    /**
     * 根据ID查询用户
     *
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    User findById(Integer id);

    /**
     * 添加一条用户数据
     *
     * @param user
     */
    @Insert("insert into user(name, password, state, address, email) values (#{name}, #{password}, #{state}, #{address}, #{email})")
    void insert(User user);

    /**
     * 更新用户数据
     *
     * @param user
     */
    @Update("update user set name=#{name},password=#{password},state=#{state},address=#{address},email=#{email} where id=#{id}")
    void update(User user);

    @Delete("delete from user where id = #{id}")
    void delete(Integer id);

    /**
     * 查询指定状态的用户列表
     *
     * @param state
     * @return
     */
    @Select("select * from user where id = #{state}")
    List<User> selectList(Integer state);
}
