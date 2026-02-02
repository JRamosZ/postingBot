package com.smartecmx.postingbot.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.smartecmx.postingbot.model.TechnicalTip;

@Repository
public interface TechnicalTipRepository extends CrudRepository <TechnicalTip, UUID> {

    List<TechnicalTip> findAllByPublishedAtFacebookIsNull();

    List<TechnicalTip> findAllByPublishedAtInstagramIsNull();

}
