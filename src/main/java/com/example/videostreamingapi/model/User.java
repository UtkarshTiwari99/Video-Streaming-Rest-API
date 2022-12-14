package com.example.videostreamingapi.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String username;
    private String email;
    private String password;
    private String photoUrl;
    private boolean enabled;
    private String channelName;
    private String roles;

    public User(String userName,String email,String password,String channelName,boolean enabled){
        this.username=userName;
        this.email=email;
        this.password=password;
        this.enabled=enabled;
        this.photoUrl="";
        this.channelName=channelName;
        this.roles="ROLE_USER";
    }
}

//
//@Data
//public class User {
//
//    public User(){
//
//    }
//
//    public String username;
//    public String enabled;
//    public String password;
//}