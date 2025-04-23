package com.example.vipayee;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {


    @POST("user/Registration/register")
    Call<ApiResponse> registerUser(@Body User user);

    @GET("Registration/users")
    Call<List<User>> getAllUsers();

    @GET("Registration/get-pin/{userId}")
    Call<ApiService> getPin(@Path("userId") String userId);

    @GET("Registration/get-pin/{userId}")
    Call<User> getPin1(@Path("userId") String userId);

    @GET("Registration/get-uuid/{phoneNumber}")
    Call<String> getUserId(@Path("phoneNumber") String phoneNumber);

    @GET("user/Registration/get-pin-by-phone/{phone}")
    Call<User> getUserByPhone(@Path("phone") String phoneNumber);


    @POST("user/Transaction/process")
    Call<Void> processTransaction(@Body TransactionRequest request);

    @GET("user/Registration/get-user")
    Call<UserResponse> getUserByPhoneNumber(@Query("phoneNumber") String phoneNumber);


}


