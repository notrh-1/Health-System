package com.jkglxt.mapper;

import com.jkglxt.bean.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMessageMapper {
    List<ChatMessage> getList();
    int add(ChatMessage message);
    int delete(Integer id);
}
