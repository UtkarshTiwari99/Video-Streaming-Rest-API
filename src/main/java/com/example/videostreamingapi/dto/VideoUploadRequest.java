package com.example.videostreamingapi.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoUploadRequest {
    private MultipartFile thumbnail;
    private MultipartFile video;
    private String title;
    private String description;
}