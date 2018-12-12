package com.massestech.common.mybatis.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CamelToUnderline {
    public static final char UNDERLINE = '_';

    /**
     * 驼峰转下划线.
     * @param param
     * @return
     */
    public static String camelToUnderline(String param) {
        if (StringUtils.isEmpty(param)) {
            return "";
        } else {
            int len = param.length();
            StringBuilder sb = new StringBuilder(len);

            for(int i = 0; i < len; ++i) {
                char c = param.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append('_').append(Character.toLowerCase(c));
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        }
    }

    public static String underlineToCamel(String param) {
        if (StringUtils.isEmpty(param)) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            Pattern pattern = Pattern.compile("([A-Za-z\\d]+)(_)?");
            Matcher matcher = pattern.matcher(param);

            while(matcher.find()) {
                String word = matcher.group();
                sb.append(matcher.start() == 0 ? Character.toLowerCase(word.charAt(0)) : Character.toUpperCase(word.charAt(0)));
                int index = word.lastIndexOf(95);
                if (index > 0) {
                    sb.append(word.substring(1, index));
                } else {
                    sb.append(word.substring(1));
                }
            }

            return sb.toString();
        }
    }
}
