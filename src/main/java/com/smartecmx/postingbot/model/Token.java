package com.smartecmx.postingbot.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tokens")
public class Token extends AbstractBaseModelEntity {
    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "expires_at", nullable = false)
    private LocalDate expiresAt;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
