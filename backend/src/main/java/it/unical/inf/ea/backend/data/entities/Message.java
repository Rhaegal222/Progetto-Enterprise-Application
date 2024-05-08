package it.unical.inf.ea.backend.data.entities;

import it.unical.inf.ea.backend.dto.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    private String text;

    @Column(length = 36, nullable = false, updatable = false)
    private String conversationId;

    @Column(name = "message_date", nullable = false, updatable = false)
    private LocalDateTime messageDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_status", nullable = false)
    private MessageStatus messageStatus;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "send_user", nullable = false)
    private  User sendUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_user", nullable = false)
    private User receivedUser;

}
