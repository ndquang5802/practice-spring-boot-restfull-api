package com.store.api.practiceapi.controller;

import com.store.api.practiceapi.model.Category;
import com.store.api.practiceapi.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/management/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping()
    public List<Category> retrieveAllCategories() {
        return categoryService.findAllCategories();
    }

    @GetMapping("/{id}")
    public EntityModel<Category> retrieveCategory(@PathVariable long id) {
        Category category = categoryService.findOne(id);
        EntityModel<Category> entityModel = EntityModel.of(category);
        WebMvcLinkBuilder link = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllCategories());
        entityModel.add(link.withRel("all-categories"));
        return entityModel;
    }

    @PostMapping()
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category savedCategory = categoryService.saveCategory(category);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCategory.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public void updateCategory(@PathVariable long id, @Valid @RequestBody Category updatedCategory) {
        categoryService.updateCategory(id, updatedCategory);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
    }
}
