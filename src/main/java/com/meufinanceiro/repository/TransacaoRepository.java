package com.meufinanceiro.repository;

import com.meufinanceiro.model.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<TransacaoEntity, String> {
    List<TransacaoEntity> findByContaId(String contaId);
}