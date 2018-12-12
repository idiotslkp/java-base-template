package com.massestech.common.mybatis.sqlfilter.where;

import com.massestech.common.mybatis.sqlfilter.ParamMapSqlFilter;

import java.util.List;
import java.util.Map;

/**
 * property in ( value )
 */
public class InSqlFilter extends ParamMapSqlFilter {

    public InSqlFilter(String property, Object value, Map<String, Object> sqlFilterParamMap) {
        super(property, value, sqlFilterParamMap);
    }

    @Override
    protected void appendSql() {
        sb.append(this.getColumnName()).append(" in (");
        // 'str,str,str'
        if (value instanceof String) {
            String[] split = ((String) value).split(",");
            for (String param : split) {
                String key = setValueAndGetKey(param);
                sb.append(key);
            }
        } else if (value.getClass().isArray()) {
            // 数组类型
            Object[] objs = (Object[]) value;
            for (Object param : objs) {
                String key = setValueAndGetKey(param);
                sb.append(key);
            }
        } else if (value instanceof List) {
            // 集合类型
            List list = (List) value;
            for (Object param : list) {
                String key = setValueAndGetKey(param);
                sb.append(key);
            }
        } else {
            throw new RuntimeException("不支持的in类型。请检查，in只支持String，Arrays，List类型");
        }
        sb.append(" ) ");
    }

}
