package com.rag.chatstorage.mapper;

import com.rag.chatstorage.dto.MessageRequest;
import com.rag.chatstorage.dto.MessageResponse;
import com.rag.chatstorage.entity.Message;
import com.rag.chatstorage.entity.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "session", source = "session")
    @Mapping(target = "createdAt", expression = "java(java.time.OffsetDateTime.now())")
    Message toEntity(MessageRequest request, Session session);

    /*@Mapping(target = "id", ignore = true)
    @Mapping(target = "session", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Message toEntity(MessageRequest request);*/

    @Mapping(target = "sessionId", source = "session.id")
    MessageResponse toResponse(Message entity);

}
