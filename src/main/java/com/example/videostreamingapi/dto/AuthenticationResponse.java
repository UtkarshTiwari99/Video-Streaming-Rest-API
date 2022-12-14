package com.example.videostreamingapi.dto;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {
    public String getUserName() {
        return userName;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getAuthToken() {
        return authToken;
    }

    private final int statusCode;

    private final String userName;

    private final String authToken;

    public AuthenticationResponse(int statusCode, String userName, String authToken) {
        this.statusCode = statusCode;
        this.userName = userName;
        this.authToken = authToken;
    }
}
