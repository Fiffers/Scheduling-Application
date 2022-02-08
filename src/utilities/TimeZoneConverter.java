package utilities;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeZoneConverter {

    public static ZonedDateTime stringToZonedDateTime(String dateTime, ZoneId zoneID) {
        ZonedDateTime zonedDateTime = null;
        try {
            dateTime += zoneID;
            DateTimeFormatter format = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ssz");
            zonedDateTime = ZonedDateTime.parse(dateTime, format);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zonedDateTime;
    }

    public static ZonedDateTime toZone(ZonedDateTime time, ZoneId zoneID) {
        ZonedDateTime convertedDateTime = null;
        try {
            convertedDateTime = time.withZoneSameInstant(zoneID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertedDateTime;
    }

    public static Timestamp toSQL(ZonedDateTime time) {
        Timestamp timestamp = Timestamp.valueOf(time.toLocalDateTime());
        return timestamp;
    }

    public static String makeReadable(ZonedDateTime time) {

        String month  = PrependZero.twoDigits(time.getMonthValue());
        String day    = PrependZero.twoDigits(time.getDayOfMonth());
        String year   = String.valueOf(time.getYear());
        String hour   = PrependZero.twoDigits(time.getHour());
        String minute = PrependZero.twoDigits(time.getMinute());

        String string = year + "-" + month + "-" + day + " " + hour + ":" + minute;
        return string;
    }
}
