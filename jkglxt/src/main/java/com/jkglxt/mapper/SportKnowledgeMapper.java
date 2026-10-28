package com.jkglxt.mapper;

import com.jkglxt.bean.SportKnowledge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SportKnowledgeMapper {
    List<SportKnowledge> getList();
    
    SportKnowledge getById(@Param("id") Integer id);
    
    List<SportKnowledge> getByCategory(@Param("category") String category);
    
    int add(SportKnowledge knowledge);
    
    int update(SportKnowledge knowledge);
    
    int delete(@Param("id") Integer id);
}
