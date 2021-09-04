package com.auresus.academy.model.bean.responses

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InstrumentListResponse(
/*[
{
    "label": "Aural",
    "apiName": "Aural"
  },
    ]
}*/
    val label: String,
    val apiName: String
)
