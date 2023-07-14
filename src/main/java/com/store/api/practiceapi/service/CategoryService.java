package com.store.api.practiceapi.service;

import com.store.api.practiceapi.controller.exception.ResourceNotFoundException;
import com.store.api.practiceapi.model.Category;
import com.store.api.practiceapi.repository.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Category findOne(long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        return category.get();
    }

    public Category saveCategory(Category category) {
        Category saveCategory = categoryRepository.save(category);
        return saveCategory;
    }

    public void updateCategory(long id, Category updatedCategory) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        existingCategory.get().setName(updatedCategory.getName());
        categoryRepository.save(existingCategory.get());
    }

    public void deleteCategory(long id) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
