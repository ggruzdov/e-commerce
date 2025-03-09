package com.github.ggruzdov.ecommerce.service;

import com.github.ggruzdov.ecommerce.model.AttributeDefinition;
import com.github.ggruzdov.ecommerce.repository.AttributeDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AttributeDefinitionService {

    private final AttributeDefinitionRepository attributeDefinitionRepository;

    // For production consider cache
    public Set<String> getAttributeNames(Integer categoryId) {
        return attributeDefinitionRepository.findAllByCategoryId(categoryId)
            .stream()
            .map(AttributeDefinition::getName)
            .collect(Collectors.toSet());
    }
}
