package com.auresus.academy.model.bean.responses

import java.io.Serializable

data class TicketListResponse(
/*{
    "cases": [
    {
        "type": "Lesson Cancel",
        "subject": "Lesson Cancel within 24 hrs",
        "studentName": "Charlie Lim - Test Account",
        "studentId": "0016F00003Q210IQAR",
        "status": "New",
        "parentId": "0016F00003Q20yvQAB",
        "description": null,
        "createdDate": "2021-05-17T09:23:22.000Z",
        "caseNumber": "00198179",
        "caseId": "5001m000005N3dfAAC",
        "caseComments": [

        ]
    }
    ]
}*/
    var cases: List<TicketList>
)

data class TicketList(
    var type: String,
    var subject: String,
    var studentName: String,
    var studentId: String,
    var status: String,
    var parentId: String,
    var description: String,
    var createdDate: String,
    var caseNumber: String,
    var caseId: String
) : Serializable