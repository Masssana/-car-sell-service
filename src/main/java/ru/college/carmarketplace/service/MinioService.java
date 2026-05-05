package ru.college.carmarketplace.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    void uploadFile(MultipartFile file, String fileName);

    String getFileUrl(String fileName);

}
