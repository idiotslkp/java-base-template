package com.massestech.common.web;

import com.massestech.common.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * String转Date 参数转换器
 * @author xuzhifan
 * @version 1.0
 * @since 2017/7/25
 */
@Component
public class String2DateConverter implements Converter<String, Date> {

    public static final String DATE_TIME_PATTER = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_MIN_PATTER = "yyyy-MM-dd HH:mm";
    public static final String DATE_PATTER = "yyyy-MM-dd";
    @Override
    public Date convert(String source) {
        if (StringUtils.isEmpty(source) || "null".equals(source)) {
            return null;
        }
        source = StringUtils.trim(source);
        if (source.length() >= 10
                && Pattern.matches(
                "^(\\d{4}(-\\d{1,2}){2}$)|^(\\d{4}(-\\d{1,2}){2} \\d{1,2}\\:\\d{1,2})$|(^\\d{4}(-\\d{1,2}){2} (\\d{1,2}\\:){2}\\d{1,2}$)", source)) {
            String pattern;
            if (source.length() > 10) {
                if (source.length() == 16) {
                    pattern = DATE_TIME_MIN_PATTER;
                } else {
                    pattern = DATE_TIME_PATTER;
                }
            } else {
                pattern = DATE_PATTER;
            }
            return DateUtils.parse(source, pattern);
        }
        return null;
    }

}
