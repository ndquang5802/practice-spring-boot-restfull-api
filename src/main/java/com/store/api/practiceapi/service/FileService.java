package com.store.api.practiceapi.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface FileService {
    String uploadImage(String path, MultipartFile file) throws IOException;
    void removeImage(String imagePath);
    Resource loadImageAsResource(String imageName) throws IOException;
}
