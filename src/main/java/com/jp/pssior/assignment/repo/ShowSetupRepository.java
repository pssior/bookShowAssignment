package com.jp.pssior.assignment.repo;

import com.jp.pssior.assignment.model.entity.ShowSetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowSetupRepository extends JpaRepository<ShowSetup, String> {

}
