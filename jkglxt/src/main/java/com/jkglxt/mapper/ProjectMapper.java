package com.jkglxt.mapper;

import com.jkglxt.bean.Project;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ProjectMapper {
    int addProject(Project project);
    int deleteProject(Integer id);
    List<Project> getByUserId(Integer userId);
}