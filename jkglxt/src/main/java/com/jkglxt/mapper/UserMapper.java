package com.jkglxt.mapper;

import com.jkglxt.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User login(@Param("username") String username, @Param("password") String password);
    
    List<User> getUserList();
    
    int addUser(User user);
    
    int updateUser(User user);
    
    int deleteUser(Integer id);
    
    User getUserById(Integer id);
    
    int updateRole(@Param("id") Integer id, @Param("role") String role);
    
    int updateState(@Param("id") Integer id, @Param("state") Integer state);
    
    User findByUsername(@Param("username") String username);
}
