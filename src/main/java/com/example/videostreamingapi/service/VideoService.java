package com.example.videostreamingapi.service;

import com.example.videostreamingapi.model.Video;
import com.example.videostreamingapi.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService{
    @Autowired
    private VideoRepository videoRepository;

    public List<Video> getAll(){
        return videoRepository.findAll();
    }

    public void save(Video video){
        videoRepository.save(video);
    }

}