package utilities;

public class RemoveSquareBrackets {
    public static String go(String string) {
        string = string.replaceAll("\\[", "").replaceAll("\\]","");
        return string;
    }
}
