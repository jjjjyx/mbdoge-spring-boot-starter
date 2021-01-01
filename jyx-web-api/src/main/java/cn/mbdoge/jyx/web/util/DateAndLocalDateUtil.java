package cn.mbdoge.jyx.web.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author jyx
 */
public final class DateAndLocalDateUtil {

    private DateAndLocalDateUtil () {}

    public static Date localDateToDate(LocalDateTime localDate) {
        ZonedDateTime zonedDateTime = localDate.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime timestampToLocalDateTime(Long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate timestampToLocalDate(Long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Long localDateTimeToTimestamp(LocalDateTime time) {
        Instant instant = time.atZone(ZoneId.systemDefault()).toInstant();
        return instant.toEpochMilli();
    }

    public static String localDateTimeToStringByNoHorizontal(LocalDateTime time) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return dateTimeFormatter.format(LocalDateTime.now());
    }
}
