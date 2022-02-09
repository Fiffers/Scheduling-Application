package utilities;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeZoneConverter {

    /**
     * Converts a string, probably from an SQL query, to a ZonedDateTime
     * @param dateTime The string to convert
     * @param zoneID The zoneID you want to set it to
     * @return the converted ZonedDateTime
     */
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

    /**
     * Converts ZonedDateTime from one zone to another
     * @param time The ZonedDateTime to convert
     * @param zoneID The ZoneID you want to convert it to
     * @return the converted ZonedDateTime
     */
    public static ZonedDateTime toZone(ZonedDateTime time, ZoneId zoneID) {
        ZonedDateTime convertedDateTime = null;
        try {
            convertedDateTime = time.withZoneSameInstant(zoneID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertedDateTime;
    }

    /**
     * Converts ZoneDateTime into SQL timestamp
     * @param time the ZonedDateTime to convert
     * @return the Timestamp
     */
    public static Timestamp toSQL(ZonedDateTime time) {
        return Timestamp.valueOf(time.toLocalDateTime());
    }

    /**
     * Converts a ZonedDateTime into a format that is a bit more readable for a human being
     * @param time The ZonedDateTime to convert
     * @return a more readable string
     */
    public static String makeReadable(ZonedDateTime time) {

        String month  = PrependZero.twoDigits(time.getMonthValue());
        String day    = PrependZero.twoDigits(time.getDayOfMonth());
        String year   = String.valueOf(time.getYear());
        String hour   = PrependZero.twoDigits(time.getHour());
        String minute = PrependZero.twoDigits(time.getMinute());

        return year + "-" + month + "-" + day + " " + hour + ":" + minute;
    }
}
