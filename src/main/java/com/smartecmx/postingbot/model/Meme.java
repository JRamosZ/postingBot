package com.smartecmx.postingbot.model;

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
@Table(name = "memes")
public class Meme extends AbstractPostBaseEntity {
    @Column(name = "template_id", nullable = false)
    private String templateId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "meme_texts", nullable = false)
    private String[] memeTexts;
}
