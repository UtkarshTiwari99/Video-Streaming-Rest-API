package com.example.videostreamingapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int videoId;
    private String title;
    private String description;
    private String thumbnailUrl;
    private int views;
    private String url;
    @ManyToOne
    @JoinColumn(name = "userid")
    private User user;
    public Video(String title,String description,String thumbnailUrl,int views,String url,User user) {
        this.title=title;
        this.description=description;
        this.thumbnailUrl=thumbnailUrl;
        this.views=views;
        this.url=url;
        this.user=user;
    }
}