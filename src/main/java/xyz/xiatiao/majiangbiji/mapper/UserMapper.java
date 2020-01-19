package xyz.xiatiao.majiangbiji.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.xiatiao.majiangbiji.model.User;

@Mapper
public interface UserMapper {

    @Insert("insert into user (name,account_id,token,GMT_CREATE,GMT_MODIFIED) values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified})")
    void insertUser(User user);

    @Select("select * from user where token = #{token}")
    User fromTokenSearchUser(@RequestParam String token);

    @Select("select * from user where account_id = #{accountId}")
    boolean userIsRepeat(@RequestParam String accountId);

    @Update("UPDATE user SET name=#{name},token=#{token},GMT_MODIFIED=#{gmtModified}")
    void updateUser(User user);
}
