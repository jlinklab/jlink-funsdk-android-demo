package demo.xm.com.xmfunsdkdemo.utils;



public class ParseTimeUtil {

    private ParseTimeUtil() {
        // prevent  instance
    }

    public static String parseTime(int hours, int minute) {

        String hour, min, time;
        if ((hour = String.valueOf(hours)).length() == 1) {
            time = "0" + hour;
        } else {
            time = hour;
        }
        if ((min = String.valueOf(minute)).length() == 1) {
            min = "0" + min;
            time = time + ":" + min;
        } else {
            time = time + ":" + min;
        }
        return time.trim();
    }


    public static String combineTime(int value) {
        String finalString = "";
        if (String.valueOf(value).length() < 2) {
            finalString = "0" + String.valueOf(value);
        } else {
            finalString = String.valueOf(value);
        }
        return finalString;
    }
}
