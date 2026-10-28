package com.jkglxt.mapper;

import com.jkglxt.bean.MealPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MealPlanMapper {
    List<MealPlan> getByUserId(@Param("userId") Integer userId);
    
    List<MealPlan> getByUserIdAndDate(@Param("userId") Integer userId, @Param("mealDate") String mealDate);
    
    MealPlan getById(@Param("id") Integer id);
    
    int add(MealPlan mealPlan);
    
    int delete(@Param("id") Integer id);
    
    int updateCalorieRecordId(@Param("id") Integer id, @Param("calorieRecordId") Integer calorieRecordId);
    
    Map<String, Object> getNutritionStats(@Param("userId") Integer userId, @Param("mealDate") String mealDate);
}
