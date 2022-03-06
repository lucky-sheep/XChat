package cn.tim.xchat.common.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss")
//            .ofPattern("yyyy-MM-dd")
            .withLocale(Locale.CHINA)
            .withZone(ZoneId.systemDefault());

    public static String formatDate(Instant instant){
        return dateTimeFormatter.format(instant);
    }
}
