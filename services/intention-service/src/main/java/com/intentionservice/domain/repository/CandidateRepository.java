package com.intentionservice.domain.repository;

import com.intentionservice.domain.vo.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Integer>
{
}