package com.auresus.academy.model.local.room_db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.auresus.academy.model.bean.Contact
import com.auresus.academy.model.local.room_db.dao.ContactDao


/* Created by Sahil Bharti on 26/4/19.
 *
*/
@Database(entities = [Contact::class], version = 1)
abstract class AppDB : RoomDatabase() {

    abstract fun contactDao(): ContactDao


}