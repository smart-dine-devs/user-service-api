package com.devstack.SmartDine.entity;

import com.devstack.SmartDine.entity.enums.RoleName;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;
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
        private Long id;
        private RoleName name;
        private String description;
        private LocalTime createdAt;
        private LocalTime updatedAt;
}
