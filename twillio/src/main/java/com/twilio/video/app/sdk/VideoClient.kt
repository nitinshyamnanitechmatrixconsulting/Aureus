package com.twilio.video.app.sdk

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.twilio.video.LocalDataTrack
import com.twilio.video.Room
import com.twilio.video.Video
import com.twilio.video.app.data.api.model.ApiResponse
import com.twilio.video.app.ui.Attachment
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoClient(
    private val context: Context,
    private val connectOptionsFactory: ConnectOptionsFactory
) {

    suspend fun connect(
        identity: String,
        roomName: String,
        roomListener: Room.Listener,localDataTrack: LocalDataTrack?
    ): Room {

            return Video.connect(
                    context,
                    connectOptionsFactory.newInstance(identity, roomName,localDataTrack),
                    roomListener )
    }

}
