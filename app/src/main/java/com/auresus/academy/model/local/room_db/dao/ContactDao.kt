package com.auresus.academy.model.local.room_db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.auresus.academy.model.bean.Contact


/* Created by Sahil Bharti on 26/4/19.
 *
*/
@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contact: Contact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllContacts(contacts: List<Contact>)

    @Query("SELECT * FROM tb_contact WHERE id = :id LIMIT 1")
    fun retrieve(id: Int): Contact

    @Query("SELECT * FROM tb_contact")
    fun retrieveAllContact(): List<Contact>
}
