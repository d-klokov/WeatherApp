package ru.klokov.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions", indexes = {@Index(name = "expires_at_idx", columnList = "expires_at")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
