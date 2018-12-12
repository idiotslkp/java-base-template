package com.massestech.common.mybatis.provider;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.provider.base.AbstractDeleteTemplate;

/**
 * 删除模板实现类
 * @param <T>
 */
public class DeleteTemplate<T extends BaseEntity> extends AbstractDeleteTemplate<T> {

    @Override
    protected String deleteSetSql() {
        return "deleted = 1, last_update_time=now()";
    }

    @Override
    protected String deleteByIdSetSql() {
        return "deleted = 1, last_update_time=now()";
    }

    @Override
    protected String deleteByIdWhereSql() {
        return "id =#{id} and deleted = 0";
    }


    @Override
    protected String deleteBatchWhereSql(Object[] ids) {
        StringBuilder sb = new StringBuilder();
        sb.append("id in (");
        setIds(ids, sb);
        sb.append(") and deleted = 0");
        return sb.toString();
    }


    @Override
    protected void preProcessWhereSql(T obj, StringBuilder whereSb) {
        // 查询条件判断是否已经包含了deleted=0.因为项目里面只需要查出还没有删掉的数据
        if (whereSb.indexOf("deleted") == -1) {
            if (!"".equals(whereSb.toString())) {
                whereSb.append(" and deleted=0");
            } else {
                whereSb.append(" deleted=0");
            }
        }
    }

}
