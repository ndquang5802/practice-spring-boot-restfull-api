package com.store.api.practiceapi.repository;

import com.store.api.practiceapi.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
