package com.devstack.SmartDine.entity;

import com.devstack.SmartDine.entity.enums.OtpType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name="otp_verification")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OTPVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)

    private User user;

    @Column(name="otp_code" ,nullable= false,length = 10)
    private String otpCode;

    @Enumerated(EnumType.STRING)
    @Column(name="otp_type" ,nullable= false )
    private OtpType otpType;


    @Column(name="is_used" ,nullable= false)
    @Builder.Default
    private boolean used=false;

    @Column(name="attempts" ,nullable= false)
    @Builder.Default
    private int attempts=0;

    @Column(name="expires_At" ,nullable= false )
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name= "created_at" , nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid(String code) {
        return !used && !isExpired() && otpCode.equals(code);
    }
}
