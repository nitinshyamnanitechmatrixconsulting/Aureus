package com.twilio.video.app.helper

import java.util.*

class StringHelper {
    companion object{
        fun getShortString(str:String):String{
            var shortName:String="";
            var arrStr=str.split(" ");
            shortName=(arrStr[0].get(0)).toString()
           /* for (x in 0 until arrStr.size){
                shortName+=(arrStr[x].get(0)).toString()
            }*/
            return shortName.toUpperCase(Locale.ROOT);
        }
    }
}