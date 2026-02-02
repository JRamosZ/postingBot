package com.smartecmx.postingbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "curious_facts")
public class CuriousFact  extends AbstractPostBaseEntity{
    @Column(name = "fact_text", nullable = false, columnDefinition = "TEXT")
    private String factText;
}
