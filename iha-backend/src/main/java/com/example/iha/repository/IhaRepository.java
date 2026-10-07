package com.example.iha.repository;

import com.example.iha.entity.Iha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IhaRepository extends JpaRepository<Iha, Long>, JpaSpecificationExecutor<Iha> {

    boolean existsBySeriNo(String seriNo);

    boolean existsBySeriNoAndIdNot(String seriNo, Long id);
}
