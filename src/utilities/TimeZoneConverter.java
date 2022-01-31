package utilities;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
}
