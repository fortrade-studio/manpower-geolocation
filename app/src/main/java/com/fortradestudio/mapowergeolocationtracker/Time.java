package com.fortradestudio.mapowergeolocationtracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Time {

    public String calculateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy, HH:mm:ss zz");
        Date date = new Date();
        sdf.setTimeZone(TimeZone.getDefault());
        String txtDate = sdf.format(date);
        return txtDate;
    }
}
