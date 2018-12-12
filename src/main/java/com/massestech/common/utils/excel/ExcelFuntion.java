package com.massestech.common.utils.excel;

import com.massestech.common.utils.DateUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 该类存放excel的函数功能,目前只包含日期格式化,后续有需要那么会继续加进其他功能
 */
public class ExcelFuntion {

    private int index = 0;

    private Map<String, String> sumMap = new HashMap<>();

    /**
     * 日期转换成字符串,yyyy-MM-dd
     */
    public String formatDate(Date date) {
        return DateUtils.formatDate(date);
    }
    /**
     * 日期转换成字符串,style:格式自定义
     */
    public String formatDateByStyle(Date date, String style) {
        return DateUtils.formatDateByStyle(date, style);
    }

    /**
     * 求和
     * @param key
     * @param value
     * @return
     */
    public Object addSum(String key, Object value) {
        if (null == sumMap.get(key)) {
            sumMap.put(key, value.toString());
        } else {
            BigDecimal sumBigDecimal = new BigDecimal(sumMap.get(key));
            BigDecimal valueBigDecimal = new BigDecimal(value.toString());
            sumBigDecimal = sumBigDecimal.add(valueBigDecimal);
            sumMap.put(key, sumBigDecimal.toString());
        }
        return value;
    }

    /**
     * 获取求和的结果
     * @param key
     * @return
     */
    public BigDecimal sum(String key) {
        return sumMap.get(key) == null ? new BigDecimal(0) : new BigDecimal(sumMap.get(key));
    }


    /**
     * 获取下标
     * @return
     */
    public int index() {
        return ++index;
    }

}
