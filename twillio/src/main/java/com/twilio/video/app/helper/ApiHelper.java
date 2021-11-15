package com.twilio.video.app.helper;


import com.twilio.video.app.data.api.model.Token;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiHelper {
    @GET("token/{name}/{roomName}")
    Call<Token> getToken(@Path(value = "name", encoded = true)String name, @Path(value = "roomName", encoded = true)String roomName);
    //GetIFSC Code
    @GET("AttachmentDeleteApi")
    Call<ResponseBody> deleteAttachment(@Query("id") String id);

}
