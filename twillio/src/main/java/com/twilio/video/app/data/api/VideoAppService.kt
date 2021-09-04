/*
 * Copyright (C) 2019 Twilio, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twilio.video.app.data.api

import com.twilio.video.app.data.api.model.GetAccessTokenResponse
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VideoAppService {
    @GET("token/{name}/{roomName}")
    suspend fun getTwilioAccessTooken(@Path(value = "name", encoded = true) name:String, @Path(value = "roomName", encoded = true) roomName:String): GetAccessTokenResponse
    @GET("attachmentbookingservice")
    suspend fun getAttachmentList(@Query("id") id: String): ResponseBody

}
