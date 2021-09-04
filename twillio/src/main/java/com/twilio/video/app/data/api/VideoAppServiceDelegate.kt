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

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.twilio.video.app.data.Preferences.ENVIRONMENT
import com.twilio.video.app.data.Preferences.ENVIRONMENT_DEFAULT
import com.twilio.video.app.data.Preferences.RECORD_PARTICIPANTS_ON_CONNECT
import com.twilio.video.app.data.Preferences.RECORD_PARTICIPANTS_ON_CONNECT_DEFAULT
import com.twilio.video.app.data.Preferences.TOPOLOGY
import com.twilio.video.app.data.Preferences.TOPOLOGY_DEFAULT
import com.twilio.video.app.data.api.model.ApiResponse
import com.twilio.video.app.data.api.model.GetAccessTokenResponse
import com.twilio.video.app.ui.Attachment
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException

class VideoAppServiceDelegate(
    private val sharedPreferences: SharedPreferences,
    private val videoAppServiceDev: VideoAppService,
    private val videoAppServiceStage: VideoAppService,
    private val videoAppServiceProd: VideoAppService
) : TokenService {

    override suspend fun getToken(identity: String, roomName: String): GetAccessTokenResponse? {
        val ACCESS_TOKEN_SERVER = "https://aureusacademy-meeting.herokuapp.com/"
        var getAccessTokenResponse: GetAccessTokenResponse? = null
        val retrofit = Retrofit.Builder()
            .baseUrl(ACCESS_TOKEN_SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val videoAppService = retrofit.create(VideoAppService::class.java)
        try {
            getAccessTokenResponse = videoAppService.getTwilioAccessTooken(identity, roomName)
        }
        catch (e:IOException){
            e.printStackTrace()
        }
        return getAccessTokenResponse
    }


    private fun resolveVideoAppService(env: String): VideoAppService {
        return when (env) {
            TWILIO_API_DEV_ENV -> videoAppServiceDev
            TWILIO_API_STAGE_ENV -> videoAppServiceStage
            else -> videoAppServiceProd
        }
    }
}
