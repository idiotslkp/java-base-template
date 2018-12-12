package com.massestech.common.mybatis.sqlfilter;

import java.util.Map;
import java.util.Random;

/**
 * 带有查询查询map的sqlFilter
 */
public abstract class ParamMapSqlFilter extends AbstractSqlFilter {

    protected Map<String, Object> sqlFilterParamMap;

    /** 用于避免参数重复的随机数 */
    protected long randomLong;

    protected ParamMapSqlFilter(String property, Object value, Map<String, Object> sqlFilterParamMap) {
        super(property, value);
        this.sqlFilterParamMap = sqlFilterParamMap;
        randomLong = new Random().nextLong();
    }

    /**
     * 存放参数,并且获取到key
     * @param value
     * @return
     */
    protected String setValueAndGetKey(Object value) {
        String key = "param_" + randomLong + "_" + sqlFilterParamMap.size();
        String valueKey = "#{sqlFilterAdapter.sqlFilterParamMap." + key + "}";
        sqlFilterParamMap.put(key, value);
        return valueKey;
    }

    /**
     * 获取key
     * @return
     */
    protected String getValueKey() {
        String key = "param_" + randomLong + "_" + sqlFilterParamMap.size();
        String valueKey = "#{sqlFilterAdapter.sqlFilterParamMap." + key + "}";
        return valueKey;
    }

}
