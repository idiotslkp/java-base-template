package com.massestech.common.mybatis.provider.base;

import com.massestech.common.domain.EntityAware;
import com.massestech.common.mybatis.utils.ReflectUtils;
import com.massestech.common.mybatis.utils.SqlUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 新增的模板
 * @param <T>
 */
public abstract class AbstractInsertTemplate<T extends EntityAware> extends AbstractTemplate<T> {

    /**
     * 新增
     */
    public String insert(T obj) {
        SQL sql = new SQL();
        sql.INSERT_INTO(ReflectUtils.tableName(obj.getClass()));

        Map<String, String> insertMap = returnInsertColumnsMap(obj);
        sql.VALUES(insertMap.get(SqlUtil.TAB_COLUMN), insertMap.get(SqlUtil.COLUMN_VALUE));

        return sql.toString();
    }

    /**
     * 批量新增, insert into table(列名,列名) values (值,值) ,(值,值)
     */
    public String insertBatch(@Param("objList") List objList) {
        if (objList == null || objList.size() == 0) {
            throw new IllegalArgumentException("新增的列表不能为空.");
        }
        T obj = (T) objList.get(0);
        String tableName = ReflectUtils.tableName(obj.getClass());
        if (tableName == null) {
            throw new IllegalArgumentException("新增的list列表里面的entity,没有对应的@Table注解.");
        }
        // 得到新增的列信息
        Map<String, String> insertMap = returnInsertColumnsMapBatch(obj);
        // insert into table(列名,列名)
        StringBuilder sb  = new StringBuilder("insert into ").append(tableName).append("(" + insertMap.get(SqlUtil.TAB_COLUMN) + ")").append(" values ");
        // values (值,值) ,(值,值)
        String insertValueColumns = insertMap.get(SqlUtil.COLUMN_VALUE);
        for(int i = 0; i < objList.size(); ++i) {
            sb.append("(").append(insertValueColumns.replaceAll("_index_", String.valueOf(i)));
            if (i < objList.size() - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    /**
     * 返回新增的字段map,map包含了新增的字段(key为),以及对应的值,
     * @return
     */
    public Map<String, String> returnInsertColumnsMap(T obj) {
        Map<String, String> columnMap = new HashMap();
        StringBuilder tabColumnSb = new StringBuilder();
        StringBuilder columnValueSb = new StringBuilder();
        preProcessInsertSql(obj, tabColumnSb, columnValueSb);   // 前置处理

        processInsertSql(obj, tabColumnSb, columnValueSb);  // 处理sql

        postProcessInsertSql(obj, tabColumnSb, columnValueSb);  // 后置处理

        columnMap.put(SqlUtil.TAB_COLUMN, tabColumnSb.toString());
        columnMap.put(SqlUtil.COLUMN_VALUE, columnValueSb.toString());
        return columnMap;
    }

    /**
     * 默认的实现.
     * @param obj
     * @param tabColumnSb
     * @param columnValueSb
     */
    protected void processInsertSql(T obj, StringBuilder tabColumnSb, StringBuilder columnValueSb) {
        Class<? extends EntityAware> entityClass = obj.getClass();
        Iterator fieldItertor = SqlUtil.getColumnList(entityClass).iterator();
        while(fieldItertor.hasNext()) {
            Map<String, String> map = (Map)fieldItertor.next();
            if (!ReflectUtils.isNull(obj, map.get(SqlUtil.MODEL_ATTRIBUTE))) {
                if (tabColumnSb.length() > 0) {
                    tabColumnSb.append(",");
                }
                if (columnValueSb.length() > 0) {
                    columnValueSb.append(",");
                }
                tabColumnSb.append(map.get(SqlUtil.TAB_COLUMN));
                columnValueSb.append("#{").append(map.get(SqlUtil.MODEL_ATTRIBUTE)).append("}");
            }
        }

    }

    /**默认提供的空实现*/
    protected void preProcessInsertSql(T obj, StringBuilder tabColumnSb, StringBuilder columnValueSb) {
    }
    protected void postProcessInsertSql(T obj, StringBuilder tabColumnSb, StringBuilder columnValueSb) {
    }

    public Map<String, String> returnInsertColumnsMapBatch(T obj) {
        Map<String, String> columnMap = new HashMap();
        StringBuilder tabColumnSb = new StringBuilder();
        StringBuilder columnValueSb = new StringBuilder();

        preProcessInsertBatchSql(obj, tabColumnSb, columnValueSb);

        processInsertBatchSql(obj, tabColumnSb, columnValueSb);

        postProcessInsertBatchSql(obj, tabColumnSb, columnValueSb);

        columnValueSb.append(")");
        columnMap.put(SqlUtil.TAB_COLUMN, tabColumnSb.toString());
        columnMap.put(SqlUtil.COLUMN_VALUE, columnValueSb.toString());
        return columnMap;
    }

    protected void processInsertBatchSql(T obj, StringBuilder tabColumnSb, StringBuilder columnValueSb){
        Iterator columnIterator = SqlUtil.getColumnList(obj.getClass()).iterator();
        while(columnIterator.hasNext()) {
            Map<String, String> map = (Map)columnIterator.next();

            if (tabColumnSb.length() > 0) {
                tabColumnSb.append(",");
            }
            tabColumnSb.append(map.get(SqlUtil.TAB_COLUMN));

            if (columnValueSb.length() > 0) {
                columnValueSb.append(",");
            }

            columnValueSb.append("#{objList[_index_].").append(map.get(SqlUtil.MODEL_ATTRIBUTE)).append("}");
        }
    }


    /**默认提供的空实现*/
    protected void preProcessInsertBatchSql(T obj, StringBuilder tabColumnSb, StringBuilder columnValueSb) {
    }
    protected void postProcessInsertBatchSql(T obj, StringBuilder tabColumnSb, StringBuilder columnValueSb) {
    }

}
