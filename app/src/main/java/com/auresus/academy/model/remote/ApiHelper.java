package com.auresus.academy.model.remote;


import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.Call;
import retrofit2.http.Query;

public interface ApiHelper {
    //GetIFSC Code
    @GET("AttachmentDeleteApi")
    Call<ResponseBody> deleteAttachment(@Query("id") String id);
}
