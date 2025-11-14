package com.rag.chatstorage.mapper;

import com.rag.chatstorage.dto.CreateSessionRequest;
import com.rag.chatstorage.dto.RenameSessionRequest;
import com.rag.chatstorage.dto.SessionResponse;
import com.rag.chatstorage.dto.ToggleFavoriteRequest;
import com.rag.chatstorage.entity.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper interface for converting between Session entities and DTOs.
 * Implemented automatically by MapStruct at compile time.
 */
@Mapper(componentModel = "spring")
public interface SessionMapper {

    // Map CreateRequest → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "favorite", constant = "false")
    @Mapping(target = "createdAt", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.OffsetDateTime.now())")
    Session toEntity(CreateSessionRequest request);

    // Map RenameRequest → updates only title
    @Mapping(target = "title", source = "title")
    void updateTitleFromRequest(RenameSessionRequest request, @MappingTarget Session session);

    // Map ToggleFavoriteRequest → updates favorite field
    @Mapping(target = "favorite", source = "favorite")
    void updateFavoriteFromRequest(ToggleFavoriteRequest request, @MappingTarget Session session);

    // Map Entity → Response
    SessionResponse toResponse(Session session);

    // Map List<Entity> → List<Response>
    List<SessionResponse> toResponseList(List<Session> sessions);
}
