package com.jkglxt.mapper;

import com.jkglxt.bean.Sport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SportMapper {
    List<Sport> getSportList();
    
    Sport getSportById(Integer id);
    
    int addSport(Sport sport);
    
    int updateSport(Sport sport);
    
    int deleteSport(Integer id);
}
