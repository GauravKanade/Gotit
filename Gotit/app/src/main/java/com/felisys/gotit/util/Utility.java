package com.felisys.gotit.util;

import com.felisys.gotit.database.DatabaseHandler;

import java.util.Calendar;
import java.util.List;

/**
 * Created by gauravkanade on 1/21/17.
 */

public class Utility {
    public static final String LOG_TAG = "Gotit";
    public static final String DATABASE_LOG_TAG = "GotitDatabase";

    public static final String URL = "http://192.168.1.5:8080";
    public static final String API_CHECK_LOGIN = URL + "/api/users/user/login/<EMAIL>";
    public static final String API_SAVE_USER = URL + "/api/users/user/save";
    public static final String API_GET_USER_BY_ID = URL + "/api/users/user/<USER_ID>";
    public static final String API_GET_ALL_TARGETS = URL + "/api/target/target/all";
    public static final String API_GENERATE_TEST_PAPER = URL + "/api/practicePaper/generate/<USER_ID>/<TARGET>/<SUBJECT>";
    public static final String API_SAVE_ANSWER = URL + "/api/practicePaper/saveAnswer";
    public static final String API_EVALUATE_TESTPAPER = URL + "/api/practicePaper/evaluate";
    public static final String API_UPDATE_TESTPAPER = URL + "/api/practicePaper/update";

    public static final String EMAIL_PH = "<EMAIL>";
    public static final String USER_ID_PH = "<USER_ID>";
    public static final String TARGET_PH = "<TARGET>";
    public static final String SUBJECT_PH = "<SUBJECT>";

    public static DatabaseHandler databaseHandler;

    public static int[] convertToArray(List<Integer> integerList) {
        int[] integers = new int[integerList.size()];
        for (int i = 0; i < integers.length; i++) {
            integers[i] = integerList.get(i);
        }
        return integers;
    }

    public static String getDateFormat(long milliseconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);
        int m = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int amPm = cal.get(Calendar.AM_PM);
        String month = "";
        switch (m) {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;
        }

        String dateFormatted = day + " " + month + ", " + year + " " + hour + ":" + minute + (amPm == Calendar.AM ? " am" : " pm");
        return dateFormatted;
    }

    public static String getDateFormat(String timeStamp) {
        String finaltime;
        String year = timeStamp.substring(0, 4);
        try {
            int m = Integer.parseInt(timeStamp.substring(5, 7));
            int d = Integer.parseInt(timeStamp.substring(8, 10));
            int hour = Integer.parseInt(timeStamp.substring(11, 13));
            String min = timeStamp.substring(14, 16);
            String time;
            if (hour == 12) {
                time = hour + ":" + min + "pm";
            } else if (hour == 0) {
                time = "12:" + min + "am";
            } else if (hour > 12) {
                time = (hour - 12) + ":" + min + "pm";
            } else {
                time = hour + ":" + min + "am";
            }
            String month = "";
            switch (m) {
                case 1:
                    month = "Jan";
                    break;
                case 2:
                    month = "Feb";
                    break;
                case 3:
                    month = "Mar";
                    break;
                case 4:
                    month = "Apr";
                    break;
                case 5:
                    month = "May";
                    break;
                case 6:
                    month = "Jun";
                    break;
                case 7:
                    month = "Jul";
                    break;
                case 8:
                    month = "Aug";
                    break;
                case 9:
                    month = "Sep";
                    break;
                case 10:
                    month = "Oct";
                    break;
                case 11:
                    month = "Nov";
                    break;
                case 12:
                    month = "Dec";
                    break;
            }
            finaltime = d + " " + month + "," + year + " " + time;
        } catch (Exception e) {
            finaltime = "Error in date";
        }
        return finaltime;
    }
}
