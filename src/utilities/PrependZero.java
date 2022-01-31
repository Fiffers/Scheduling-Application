package utilities;

public class PrependZero {
    public static String twoDigits(int number) {
        String formatted = String.format("%02d", number);
        return formatted;
    }
    public String fourDigits(int number) {
        String formatted = String.format("%04d", number);
        return formatted;
    }
}
