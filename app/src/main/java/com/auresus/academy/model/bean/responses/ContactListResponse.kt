package com.auresus.academy.model.bean.responses

import com.auresus.academy.model.bean.Contact
import java.io.Serializable


/* Created by Sahil Bharti on 22/1/19.
 *
*/
class ContactListResponse : Serializable {

    val page: Int = 0
    val per_page: Int = 0
    val total: Int = 0
    val total_pages: Int = 0
    var data: ArrayList<Contact>? = null

}