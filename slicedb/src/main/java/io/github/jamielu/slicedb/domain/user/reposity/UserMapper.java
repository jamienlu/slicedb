package io.github.jamielu.slicedb.domain.user.reposity;

import io.github.jamielu.slicedb.domain.user.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author jamieLu
 * @create 2025-01-24
 */
@Mapper
public interface UserMapper {
    @Insert("insert into user (user_id, name, age) values (#{userId}, #{name}, #{age})")
    int insert(User user);

    @Select("select * from user where user_id = #{userId}")
    User findById(int userId);

    @Update("update user set name = #{name}, age = #{age} where user_id = #{userId}")
    int update(User user);

    @Delete("delete from user where user_id = #{userId}")
    int delete(int userId);
}
