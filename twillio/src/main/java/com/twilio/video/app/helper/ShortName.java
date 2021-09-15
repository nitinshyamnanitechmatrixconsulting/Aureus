package com.twilio.video.app.helper;

import java.util.Locale;

public class ShortName {
    public static String getShortString(String str){
        String shortName="";
        String[] arrStr=str.split(" ");
        shortName=arrStr[0].charAt(0)+"";
           /* for (x in 0 until arrStr.size){
                shortName+=(arrStr[x].get(0)).toString()
            }*/
        return shortName.toUpperCase(Locale.ROOT);
    }

}
