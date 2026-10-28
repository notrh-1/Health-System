package com.jkglxt.mapper;

import com.jkglxt.bean.MainMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {
    List<MainMenu> getMenus();
}
