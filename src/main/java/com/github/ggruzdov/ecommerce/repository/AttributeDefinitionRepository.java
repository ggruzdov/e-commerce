package com.github.ggruzdov.ecommerce.repository;

import com.github.ggruzdov.ecommerce.model.AttributeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AttributeDefinitionRepository extends JpaRepository<AttributeDefinition, Integer> {

    List<AttributeDefinition> findAllByCategoryId(Integer categoryId);
}