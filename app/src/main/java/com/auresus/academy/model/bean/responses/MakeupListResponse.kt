package com.auresus.academy.model.bean.responses

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MakeupListResponse(
/*{
    "bookings": [
    {
        "weekday": "Tuesday",
        "unitFee": null,
        "type": "Regular",
        "time": "15:00:00.000Z",
        "teacherName": "Wei Pern Teh",
        "teacherId": "0016F00003H95leQAB",
        "studentName": "Charlie Lim - Test Account",
        "studentId": "0016F00003Q210IQAR",
        "status": "Canceled",
        "rescheduled": false,
        "packageType": "Individual",
        "packageName": "Piano Lessons 4x45 min",
        "lessonTypeNew": null,
        "lessonPasscode": null,
        "lessonOnlineURL": null,
        "isFifthBooking": false,
        "instrument": "Piano",
        "gst": null,
        "expiryDate": "2021-06-30",
        "enrollmentId": "a066F00001TyFtJQAV",
        "duration": "45 min",
        "date": "2020-04-28",
        "centerId": "0016F00003H95LyQAJ",
        "center": "Aureus Forum",
        "cancellationReason": "Personal Reason",
        "bookingId": "a016F00002J2b9UQAR",
        "availableMakeupMin": "30 min"
    }
    ]
}*/
    var cases: List<MakeupList>,
    var bookings: List<MakeupList>

)

data class MakeupList(
    val weekday: String,
    val unitFee: Any? = null,
    val type: String,
    val time: String,
    val teacherName: String,

    @SerializedName("teacherId")
    val teacherID: String,

    val studentName: String,

    @SerializedName("studentId")
    val studentID: String,

    val status: String,
    val rescheduled: Boolean,
    val packageType: String,
    val packageName: String,
    val lessonTypeNew: Any? = null,
    val lessonPasscode: Any? = null,
    val lessonOnlineURL: Any? = null,
    val isFifthBooking: Boolean,
    val instrument: String,
    val gst: Any? = null,
    val expiryDate: String,
    @SerializedName("enrollmentId")
    val enrollmentID: String,

    val duration: String,
    val date: String,

    @SerializedName("centerId")
    val centerID: String,

    val center: String,
    val cancellationReason: String,

    @SerializedName("bookingId")
    val bookingID: String,

    val availableMakeupMin: String

) : Serializable