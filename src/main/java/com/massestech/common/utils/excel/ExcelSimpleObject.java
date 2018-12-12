package com.massestech.common.utils.excel;

import java.lang.reflect.Field;

/**
 * 该类用于辅助excel的解析.
 */
public class ExcelSimpleObject {

    /**字段名称*/
    private String fieldName;
    /**下一个字段名称*/
    private String nextFieldName;
    /**字段类型*/
    private Field field;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getNextFieldName() {
        return nextFieldName;
    }

    public void setNextFieldName(String nextFieldName) {
        this.nextFieldName = nextFieldName;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
