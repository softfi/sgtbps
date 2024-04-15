package com.sgtbps.retrofit;


import com.sgtbps.utils.Constants;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

public interface apiInterface {
    @FormUrlEncoded
    @POST(Constants.loginUrl)
    @Multipart
    Call<RequestBody> loginData(@Body RequestBody requestBody);

}
