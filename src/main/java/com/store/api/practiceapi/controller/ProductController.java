package com.store.api.practiceapi.controller;

import com.store.api.practiceapi.dto.ProductDTO;
import com.store.api.practiceapi.model.Product;
import com.store.api.practiceapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/management/products")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public List<Product> retrieveAllProducts() {
        return productService.findAllProducts();
    }

    @GetMapping("/{id}")
    public EntityModel<Product> retrieveProduct(@PathVariable long id) {
        Product product = productService.findOne(id);
        EntityModel<Product> entityModel = EntityModel.of(product);
        WebMvcLinkBuilder link = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllProducts());
        entityModel.add(link.withRel("all-products"));

        return entityModel;
    }

    @PostMapping()
    public ResponseEntity<Product> createProduct(@Valid @ModelAttribute ProductDTO productRequest) {
        try {
            Product savedProduct = productService.saveProduct(productRequest);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedProduct.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable long id) throws IOException {
        return productService.getImage(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable long id, @Valid @ModelAttribute ProductDTO productRequest) {
        try {
            productService.updateProduct(id, productRequest);
            return ResponseEntity.ok("Updated");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Deleted");
    }
}
