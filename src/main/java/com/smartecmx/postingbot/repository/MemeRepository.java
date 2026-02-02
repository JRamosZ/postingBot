package com.smartecmx.postingbot.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.smartecmx.postingbot.model.Meme;

@Repository
public interface MemeRepository extends CrudRepository<Meme, UUID> {

    List<Meme> findAllByPublishedAtFacebookIsNull();

    List<Meme> findAllByPublishedAtInstagramIsNull();

}
