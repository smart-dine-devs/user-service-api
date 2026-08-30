package com.devstack.SmartDine.entity;

import com.devstack.SmartDine.entity.enums.OtpType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name="refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(name="token" ,unique=true ,nullable= false,columnDefinition = "TEXT")
    private String token;

    @Column(name="revoked" ,nullable= false)
    @Builder.Default
    private boolean revoked =false;

    @Column(name="expires_At" ,nullable= false )
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name= "created_at" , nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid(String code) {
        return !revoked && !isExpired();
    }
}
