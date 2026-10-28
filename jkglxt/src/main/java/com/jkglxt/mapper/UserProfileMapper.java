package com.jkglxt.mapper;

import com.jkglxt.bean.UserProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProfileMapper {
    UserProfile getByUserId(Integer userId);
    int add(UserProfile profile);
    int update(UserProfile profile);
}
