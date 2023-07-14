package com.store.api.practiceapi.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServeImplement implements FileService {
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
//        File name
        String name = file.getOriginalFilename();

//        Random name generate file
        String randomId = UUID.randomUUID().toString();
        String fileWithId = randomId.concat(name.substring(name.lastIndexOf(".")));

//        Full path
        String filePath = path + File.separator + fileWithId;

//        Create folder if not created
        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }

//        File copy
        Files.copy(file.getInputStream(), Paths.get(filePath));
        return fileWithId;
    }

    @Override
    public void removeImage(String imagePath) {
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            imageFile.delete();
        }
    }

    @Override
    public Resource loadImageAsResource(String imageName) throws IOException {
        try {
            Path imagePath = Paths.get(imageName);
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("Image not found: " + imageName);
            }
        } catch (MalformedURLException e) {
            throw new IOException("Failed to load image: " + imageName, e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
