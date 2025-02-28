package com.realestate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.models.Ranking;

public interface RankingRepository extends JpaRepository<Ranking, String>{

}
