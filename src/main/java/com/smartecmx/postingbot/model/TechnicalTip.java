package com.smartecmx.postingbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "technical_tips")
public class TechnicalTip  extends AbstractPostBaseEntity{
    @Column(name = "tip_text", nullable = false, columnDefinition = "TEXT")
    private String tipText;

    @Column(name = "cta_text", nullable = true)
    private String ctaText;
}
