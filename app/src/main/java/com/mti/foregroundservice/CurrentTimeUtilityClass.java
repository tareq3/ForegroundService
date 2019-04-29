/*
 * Created by Tareq Islam on 4/21/19 6:24 PM
 *
 *  Last modified 3/28/19 11:11 AM
 */

package com.mti.foregroundservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dipu_ on 4/22/2017.
 */

public class CurrentTimeUtilityClass {
    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static long getDiffbetweenTimeStamps(int pre_time){


      return Calendar.getInstance().getTime().getMinutes() -pre_time;

    }


    public static long getDiffBetween(String preTime1){

       long diff=-1;
        try {
       Date  preTime = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy").parse(preTime1);
            Date nowTime = Calendar.getInstance().getTime();
           diff=   (nowTime.getTime() - preTime.getTime())  / (60 * 1000) % 60 ;
        } catch (ParseException e) {
            e.printStackTrace();
        }


return diff;

    }

  /*  public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = Calendar.getInstance().getTime().toString();
                try {

                    Thread.sleep(1000 * 120);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Date date = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy").parse(s);
                    System.out.println(getDiffBetween(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }*/

    }

