package com.smartecmx.postingbot.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.smartecmx.postingbot.model.CuriousFact;

@Repository
public interface CuriousFactRepository extends CrudRepository <CuriousFact, UUID> {

    List<CuriousFact> findAllByPublishedAtFacebookIsNull();

    List<CuriousFact> findAllByPublishedAtInstagramIsNull();

}
