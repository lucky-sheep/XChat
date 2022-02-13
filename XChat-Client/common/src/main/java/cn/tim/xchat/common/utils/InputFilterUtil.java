package cn.tim.xchat.common.utils;

import android.text.InputFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFilterUtil {
    public static InputFilter englishAndNumberFilter = (source, start, end,
                                          dest, dStart, dEnd) -> {
        Pattern p = Pattern.compile("[a-zA-Z|0-9]+");
        Matcher m = p.matcher(source.toString());
        if (!m.matches()) return "";
        return null;
    };
}
