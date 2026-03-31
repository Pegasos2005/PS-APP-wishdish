package com.wishDishDevelops.backend.repositories;

import com.wishDishDevelops.backend.models.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Integer> {}