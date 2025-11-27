package com.rag.chatstorage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.UUID;

import java.time.OffsetDateTime;

/**
 * Entity representing a chat message belonging to a chat session.
 *
 * Each message is associated with a {@link Session} and contains sender info,
 * message content, optional context (like AI response metadata),
 * and a creation timestamp.
 */
@Entity
@Table(name="chat_messages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Message {

    /**
     * Unique identifier for the message.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Associated chat session for this message.
     *
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    // @OnDelete(action = OnDeleteAction.CASCADE) // Ensures DB-level cleanup when session is deleted
    private Session session;

    /**
     * Sender of the message (e.g., user ID or role name like "assistant").
     */
    @NotBlank(message = "Sender cannot be blank")
    @Size(max = 255, message = "Sender name must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String sender;

    /**
     * Main message content (can contain long text).
     */
    @NotBlank(message = "Message content cannot be blank")
    @Column(name = "content", columnDefinition = "text", nullable = false)
    private String content;

    /**
     * Optional JSON context, e.g., AI metadata or conversation context.
     */
    @Column(name = "context", columnDefinition = "text")
    private String context;

    /**
     * Timestamp when the message was created.
     * Automatically populated by Hibernate.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

}
