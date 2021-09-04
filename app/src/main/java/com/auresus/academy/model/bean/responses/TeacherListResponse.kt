package com.auresus.academy.model.bean.responses

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TeacherListResponse(
/*{
  "teachers": [
    {
      "teachername": "Michelle Santos",
      "teacherId": "0016F00003H95k4QAB"
    },
    {
      "teachername": "Charissa Gacutan",
      "teacherId": "0016F00003H95kZQAR"
    },
    {
      "teachername": "Sophia Apura",
      "teacherId": "0016F00003H95kuQAB"
    },
    {
      "teachername": "Quilatan Jean Therese Lorenzo",
      "teacherId": "0016F00003H95kyQAB"
    },
    {
      "teachername": "Yih Shyue Tham",
      "teacherId": "0016F00003H95lMQAR"
    },
    {
      "teachername": "Sharlaine Hular",
      "teacherId": "0016F00003IVkXzQAL"
    },
    {
      "teachername": "Evangeline Aspera Lacdang",
      "teacherId": "0016F00003TMY3TQAX"
    }
  ]
}
}*/
    var teachers: List<TeacherList>
)

data class TeacherList(
    val teachername: String,
    val teacherId: String
) : Serializable