package com.store.api.practiceapi.repository;

import com.store.api.practiceapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
