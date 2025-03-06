package com.example.vipayee;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }
}
