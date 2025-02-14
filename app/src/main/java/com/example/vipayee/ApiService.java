package com.example.vipayee;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import java.util.List;

public interface ApiService {
    @POST("Registration/register")
    Call<Void> registerUser(@Body User user);

    @GET("Registration/users")
    Call<List<User>> getAllUsers();
}


