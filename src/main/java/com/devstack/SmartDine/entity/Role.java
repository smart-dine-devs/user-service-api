package com.devstack.SmartDine.entity;

import com.devstack.SmartDine.entity.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalTime;

@Entity
@Table(name="roles")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Role {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Enumerated(EnumType.STRING)
        @Column(name ="name", nullable = false,unique =true)
        private RoleName name;

        @Column(name ="description", length = 255)
        private String description;

        @Column(name= "created_at" , nullable = false, updatable = false)
        private LocalTime createdAt;

        @LastModifiedDate
        @Column(name= "updated_at" , nullable = false)
        private LocalTime updatedAt;
}
