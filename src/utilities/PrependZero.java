package utilities;

public class PrependZero {

    /**
     * Takes an integer and makes it into a two-character decimal string by prepending zeroes to it
     * @param number The number to be converted
     * @return The string with 2 digits
     */
    public static String twoDigits(int number) {
        return String.format("%02d", number);
    }

    /**
     * Takes an integer and makes it into a six-character hexadecimal string by prepending zeroes to it
     * @param number The number to be converted
     * @return The string with 6 digits
     */
    public static String sixDigits(int number) {
        return String.format("%06x", number);
    }
}
