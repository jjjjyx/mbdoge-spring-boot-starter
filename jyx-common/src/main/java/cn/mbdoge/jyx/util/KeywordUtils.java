package cn.mbdoge.jyx.util;



import java.util.regex.Pattern;

/**
 *
 * sql 查询时 对keyword 进行处理
 *
 * @author jyx
 */
public final class KeywordUtils {
    private final static Pattern ESCAPE = Pattern.compile("([%\\\\_])");
    public static String escapeKeyword (String keyword) {
        return ESCAPE.matcher(keyword).replaceAll("\\\\$1");
    }


    public static String warpKeyword (String keyword) {
        return '%' + escapeKeyword(keyword) + '%';
    }
}
