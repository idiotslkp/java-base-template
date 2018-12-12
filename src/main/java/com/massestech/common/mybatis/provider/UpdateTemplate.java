package com.massestech.common.mybatis.provider;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.provider.base.AbstractUpdateTemplate;


/**
 * 更新模板实现类
 * @param <T>
 */
public class UpdateTemplate<T extends BaseEntity> extends AbstractUpdateTemplate<T> {
    @Override
    protected void postProcessUpdateSetSql(T obj, StringBuilder updateSetSb, boolean isSetNotNull) {
        updateSetSb.append(",last_update_time=now()");
    }

    @Override
    protected String whereByIdSql() {
        return "deleted=0 and id=#{id}";
    }

}
