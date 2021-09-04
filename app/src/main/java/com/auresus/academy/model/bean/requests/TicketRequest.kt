package com.auresus.academy.model.bean.requests

data class TicketRequest(
    var parentId : String,
    var type : String,
    var limit : Int,
    var offset : Int
)
