package com.smartecmx.postingbot.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public class AbstractPostBaseEntity extends AbstractBaseModelEntity {

    @Column(name = "published_at_facebook")
    private LocalDateTime publishedAtFacebook;

    @Column(name = "published_at_instagram")
    private LocalDateTime publishedAtInstagram;

}
