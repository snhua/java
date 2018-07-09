package com.snhua.time;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    /**
     * @return 返回当前时间截
     */
    public static Integer nowTimestamp() {
        return (int) LocalDateTime.now().atOffset(ZoneOffset.ofHours(8)).toEpochSecond();
    }

    /**
     * @return 返回当天00:00时间截
     */
    public static long zeroTimestampOfDate() {
        LocalDateTime now = LocalDateTime.now();
        return LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0)
                .toEpochSecond(ZoneOffset.of("+8"));
    }


    /**
     * @return 当前时间, 字符串形式
     */
    public static String timeString() {
        return LocalDateTime.now().atOffset(ZoneOffset.ofHours(8)).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

    }

    /**
     * @return 当前日期, 字符串形式
     */
    public static String dateString() {
        return LocalDateTime.now().atOffset(ZoneOffset.ofHours(8)).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    }

    /**
     * 时间截转日期
     *
     * @param create_time 时间截
     * @return 日期
     */
    public static String timestampToDate(Number create_time) {
        return LocalDateTime.ofEpochSecond(create_time.longValue(), 0, ZoneOffset.UTC)
                .atOffset(ZoneOffset.ofHours(8)).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    }

    /**
     * 字符串形式的时间转时间截
     *
     * @param time
     * @return
     */
    public static Integer timeToTimestamp(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            return (int) (sdf.parse(time).getTime() / 1000);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
