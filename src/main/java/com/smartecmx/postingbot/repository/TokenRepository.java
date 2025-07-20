package com.smartecmx.postingbot.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.smartecmx.postingbot.model.Token;

@Repository
public interface TokenRepository extends CrudRepository<Token, UUID> {
    
    Token findByTypeAndActive(String type, boolean active);

    Token findByValue(String value);
}
