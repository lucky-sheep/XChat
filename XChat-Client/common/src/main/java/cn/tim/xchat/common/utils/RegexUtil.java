package cn.tim.xchat.common.utils;

import java.util.regex.Pattern;

public class RegexUtil {
    private static final String EMAIL = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    static Pattern emailPattern = Pattern.compile(EMAIL);

    public static boolean isEmail(String str){
        return emailPattern.matcher(str).matches();
    }
}
