package com.jkglxt.mapper;

import com.jkglxt.bean.CalorieRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CalorieRecordMapper {
    List<CalorieRecord> getByUserId(@Param("userId") Integer userId);
    
    List<CalorieRecord> getByUserIdAndDate(@Param("userId") Integer userId, @Param("recordDate") String recordDate);
    
    CalorieRecord getById(@Param("id") Integer id);
    
    int add(CalorieRecord record);
    
    int delete(@Param("id") Integer id);
    
    List<Map<String, Object>> getStatsByUserIdAndDate(@Param("userId") Integer userId, @Param("recordDate") String recordDate);
}
