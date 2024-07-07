package it.unical.inf.ea.backend.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name="invalid_tokens")
public class InvalidToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invalid_token_seq")
    @SequenceGenerator(name = "invalid_token_seq", sequenceName = "invalid_token_sequence", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "expiration_date", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime expirationDate;
}
