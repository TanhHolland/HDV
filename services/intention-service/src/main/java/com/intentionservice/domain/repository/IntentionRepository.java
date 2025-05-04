package com.intentionservice.domain.repository;

import com.intentionservice.domain.root.Intention;
import org.springframework.data.repository.CrudRepository;

public interface IntentionRepository extends CrudRepository<Intention, Integer> {
}