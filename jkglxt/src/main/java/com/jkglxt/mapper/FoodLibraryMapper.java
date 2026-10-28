package com.jkglxt.mapper;

import com.jkglxt.bean.FoodLibrary;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FoodLibraryMapper {
    @Select("SELECT * FROM food_library ORDER BY food_type, id")
    List<FoodLibrary> findAll();

    @Select("SELECT * FROM food_library WHERE food_type = #{foodType} ORDER BY id")
    List<FoodLibrary> findByType(String foodType);

    @Select("SELECT DISTINCT food_type FROM food_library ORDER BY food_type")
    List<String> findAllTypes();

    @Select("SELECT * FROM food_library WHERE id = #{id}")
    FoodLibrary findById(Integer id);

    @Insert("INSERT INTO food_library (food_name, food_type, calories, protein, carbs, fat, create_time, update_time) " +
            "VALUES (#{foodName}, #{foodType}, #{calories}, #{protein}, #{carbs}, #{fat}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FoodLibrary foodLibrary);

    @Update("UPDATE food_library SET food_name = #{foodName}, food_type = #{foodType}, calories = #{calories}, " +
            "protein = #{protein}, carbs = #{carbs}, fat = #{fat}, update_time = NOW() WHERE id = #{id}")
    int update(FoodLibrary foodLibrary);

    @Delete("DELETE FROM food_library WHERE id = #{id}")
    int deleteById(Integer id);
}
