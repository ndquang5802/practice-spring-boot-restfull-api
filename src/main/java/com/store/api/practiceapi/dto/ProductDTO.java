package com.store.api.practiceapi.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductDTO {
    private String name;
    private String description;
    private Float price;
    private Integer quantity;
    private MultipartFile image;
    private Long categoryId;
}
