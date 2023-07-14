package com.store.api.practiceapi.service;

import com.store.api.practiceapi.dto.ProductDTO;
import com.store.api.practiceapi.controller.exception.ResourceNotFoundException;
import com.store.api.practiceapi.model.Category;
import com.store.api.practiceapi.model.Product;
import com.store.api.practiceapi.repository.CategoryRepository;
import com.store.api.practiceapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class ProductService {
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, FileService fileService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileService = fileService;
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Product findOne(long id) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isEmpty()) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        return existingProduct.get();
    }

    public Product saveProduct(ProductDTO productRequest) throws IOException {
        Optional<Category> category = categoryRepository.findById(productRequest.getCategoryId());
        if (category.isEmpty()) {
            throw new ResourceNotFoundException("Category not found with id: " + productRequest.getCategoryId());
        }

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setCategory(category.get());
        Product savedProduct = productRepository.save(product);

        MultipartFile image = productRequest.getImage();
        String imageUrl = this.fileService.uploadImage(path, image);

        savedProduct.setImage(imageUrl);
        productRepository.save(savedProduct);

        return savedProduct;
    }

    public ResponseEntity<Resource> getImage(long id) throws IOException {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }

        String imageName = path + product.get().getImage();
        // Load the image file as a Resource
        Resource resource = this.fileService.loadImageAsResource(imageName);

        // Check if the resource exists
        if (resource.exists()) {
            // Set the content type of the response
            String contentType = "image/png";
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);

            // Return the image as a ResponseEntity
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public void updateProduct(long id, ProductDTO productRequest) throws IOException {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isEmpty()) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        Optional<Category> category = categoryRepository.findById(productRequest.getCategoryId());
        if (category.isEmpty()) {
            throw new ResourceNotFoundException("Category not found with id: " + productRequest.getCategoryId());
        }

        if (productRequest.getImage() == null) {
            existingProduct.get().setName(productRequest.getName());
            existingProduct.get().setDescription(productRequest.getDescription());
            existingProduct.get().setPrice(productRequest.getPrice());
            existingProduct.get().setQuantity(productRequest.getQuantity());
            existingProduct.get().setCategory(category.get());

            productRepository.save(existingProduct.get());
        } else {
//            Delete old image
            this.fileService.removeImage(path + existingProduct.get().getImage());
//            Update product
            existingProduct.get().setName(productRequest.getName());
            existingProduct.get().setDescription(productRequest.getDescription());
            existingProduct.get().setPrice(productRequest.getPrice());
            existingProduct.get().setQuantity(productRequest.getQuantity());
            existingProduct.get().setCategory(category.get());
            Product savedProduct = productRepository.save(existingProduct.get());

            MultipartFile image = productRequest.getImage();
            String imageUrl = this.fileService.uploadImage(path, image);
            savedProduct.setImage(imageUrl);

            productRepository.save(existingProduct.get());
        }
    }

    public void deleteProduct(long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        this.fileService.removeImage(path + product.get().getImage());
        productRepository.deleteById(id);
    }
}
