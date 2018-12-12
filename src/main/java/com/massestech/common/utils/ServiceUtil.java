package com.massestech.common.utils;

import com.github.pagehelper.PageHelper;
import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.mapper.BaseMapper;
import com.massestech.common.web.PageInfoView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * service辅助工具类
 */
@Slf4j
public class ServiceUtil {

    /**
     * 分页
     * @param request
     */
    public static void startPage(PageInfoView request) {
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        pageNum = pageNum<=0 ? 1 : pageNum;
        pageSize = pageSize<=0 ? 20 : pageSize;
        PageHelper.startPage(pageNum, pageSize);
    }

    /**
     * 创建查询
     * @param pageInfoView
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T instantiate(PageInfoView pageInfoView, Class<T> clazz) {
        // 分页
        ServiceUtil.startPage(pageInfoView);
        T instantiate = BeanUtils.instantiate(clazz);
        BeanUtils.copyProperties(pageInfoView.getParam(), instantiate);
        return instantiate;
    }

    /**
     * 校验
     * @param eo 对应的model
     * @param mapper mappper
     * @param msg 错误时的提示信息
     * @param <T>
     */
    public static <T extends BaseEntity> void valid(T eo, BaseMapper mapper, String msg) {
        int result = mapper.countCondition(eo);
        if (0 < result) {
            throwException(msg);
        }
    }

    /**
     * 抛出自定义异常,统一抛出,便于后续统一维护
     * @param msg
     */
    public static void throwException(String msg) {
        log.info(msg);
        // todo 后续需要调整为业务系统异常.
        throw new RuntimeException(msg);
    }

    /**
     * 批量新增
     * @param list
     * @param <T>
     */
    public static <T extends BaseEntity> void insertBatch(List<T> list, BaseMapper mapper) {
        if (!CollectionUtils.isEmpty(list)) {
            mapper.insertBatch(list);
        }
    }


}
