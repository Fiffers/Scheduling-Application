package utilities;

public class PrependZero {

    /**
     * Takes an integer and makes it into a two-character string by prepending zeroes to it
     * @param number The number to be converted
     * @return The string with 2 digits
     */
    public static String twoDigits(int number) {
        return String.format("%02d", number);
    }
}
