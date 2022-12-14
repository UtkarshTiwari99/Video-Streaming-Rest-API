package com.example.videostreamingapi.dto;

import lombok.Data;

@Data
public class VideoRes{
    private String  title;
    private String description;
    private String url;
    private String thumbnailUrl;
    private Integer views;
    private String channelName;
}