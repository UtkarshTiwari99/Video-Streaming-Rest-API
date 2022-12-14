package com.example.videostreamingapi.repository;

import com.example.videostreamingapi.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface VideoRepository extends JpaRepository<Video,Integer> {
    Optional<Video> findByTitle(String title);
}