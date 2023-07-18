package com.javatechie.spring.batch.repository;

import com.javatechie.spring.batch.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch,Integer> {
}
