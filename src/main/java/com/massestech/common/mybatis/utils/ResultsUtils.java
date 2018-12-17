package com.massestech.common.mybatis.utils;


import com.github.pagehelper.PageInfo;
import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.sqlfilter.join.JoinMainTableSqlFilter;
import com.massestech.common.mybatis.sqlfilter.join.LeftJoibSqlFilter;
import com.massestech.javabasetemplate.domain.UserEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import java.util.*;

/**
 * 结果集工具类.使用id为主键
 */
@Slf4j
public abstract class ResultsUtils {

    private static ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    private static ObjectFactory objectFactory = new DefaultObjectFactory();
    private static ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

//    _bookEntity_id 关联id
//    [
//    {
//      "_bookEntity_price": "30",
//      "createTime": 1537352385000,
//      "_bookEntity_id": "7",
//      "_bookEntity_name": "语文",
//      "id": "5",
//      "sexStatus": 1,
//      "_bookEntity_userId": "5",
//      "userName": "鸣人",
//      "age": 33,
//      "lastUpdateTime": 1537352385000,
//      "sexStr": "女"
//    },
//    {
//      "_bookEntity_price": "40",
//      "createTime": 1537352385000,
//      "_bookEntity_id": "8",
//      "_bookEntity_name": "数学",
//      "id": "5",
//      "sexStatus": 1,
//      "_bookEntity_userId": "5",
//      "userName": "鸣人",
//      "age": 33,
//      "lastUpdateTime": 1537352385000,
//      "sexStr": "女"
//    }
//  ]

    /**
     * 封装单条的嵌套数据
     * @param resultMap
     * @param baseEntity
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toModel(Map resultMap, BaseEntity baseEntity, Class<T>... clazz) {
        JoinMainTableSqlFilter joinMainTableSqlFilter = (JoinMainTableSqlFilter) baseEntity.getSqlFilterAdapter().getJoinSqlFilter();
        T newObject = getObject(joinMainTableSqlFilter, clazz);
        MetaObject metaObject =  MetaObject.forObject(newObject, objectFactory, objectWrapperFactory, reflectorFactory);
        Map<String, MetaObject> nestyMap = new HashMap<>();
        MetaObject nestyObject = null;
        // 循环连接的表，然后定义对应的model，然后将model设置到主model里面.
        for (LeftJoibSqlFilter leftJoibSqlFilter : joinMainTableSqlFilter.getLeftJoibSqlFilterList()) {
            if (leftJoibSqlFilter.isNesty()) {
                BaseEntity joinEntity = objectFactory.create(leftJoibSqlFilter.getJoinEntityClass());
                nestyObject = MetaObject.forObject(joinEntity, objectFactory, objectWrapperFactory, reflectorFactory);
                // 获取字段的名称
                String alias = leftJoibSqlFilter.getAlias().substring(1);
                nestyMap.put(alias, nestyObject);
                metaObject.setValue(alias, joinEntity);
            }
        }

        for (Object key : resultMap.keySet()) {
            String fieldName = key.toString();
            // 凡是带有以'_'线开头的字段，都是连表的字段
            boolean isNestyModelField = key.toString().startsWith("_");

            // 以下划线 '_' 为开头的是嵌套的model字段
            if (isNestyModelField) {
                // 位于主model里面的字段名
                String alias = fieldName.substring(1, fieldName.lastIndexOf("_"));
                // 嵌套model里面的字段名
                fieldName = fieldName.substring(fieldName.lastIndexOf("_") + 1);
                nestyObject = nestyMap.get(alias);
                nestyObject.setValue(fieldName, resultMap.get(key));
            } else {
                metaObject.setValue(fieldName, resultMap.get(key));
            }
        }

        return newObject;
    }

    /**
     * 将map转换为model
     * @param resultMapList
     * @param joinMainTableSqlFilter
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toModel(List<Map> resultMapList, JoinMainTableSqlFilter joinMainTableSqlFilter, Class<T>... clazz) {
        T newObject = getObject(joinMainTableSqlFilter, clazz);
        Object id = null;
        MetaObject metaObject = null;
        // 嵌套对象的结果集map，key是连接表的别名,也是model里面的嵌套字段的名称
        Map<String, NestyModel> nestyModelMap = getNestyModelMap(joinMainTableSqlFilter);
        // key:relationId_关联id的value
        Map<String, List> nestyListMap = new HashMap<>();
        // 关联对象的map
        Map<Object, MetaObject> relationMap = new HashMap<>();

        // 遍历结果集，封装成嵌套的model
        for (int i = 0; i < resultMapList.size(); i++) {
            Map resultMap = resultMapList.get(i);
            if (i == 0) {
                id  = resultMap.get("id");
                metaObject = MetaObject.forObject(newObject, objectFactory, objectWrapperFactory, reflectorFactory);
                // 每个model里面的嵌套结果集初始化.
                for (String alias : nestyModelMap.keySet()) {
                    // 还没有初始化的，给他初始化一次.
                    if (null == metaObject.getValue(alias)) {
                        NestyModel nestyModel = nestyModelMap.get(alias);
                        Object nestyFieldValue = null;
                        if (nestyModel.isListType()) {
                            nestyFieldValue = new ArrayList<>();
                        } else {
                            nestyFieldValue = objectFactory.create(nestyModel.getJoinEntityClass());
                        }
                        metaObject.setValue(alias, nestyFieldValue);
                    }
                }
            } else {
                if (!id.equals(resultMapList.get(i).get("id"))) {
                    // todo 异常，因为查询结果只能允许一条，多个不同的id说明查询出来的结果集大于1条.
                    log.info("该查询只能返回一条结果集，但是结果却返回了多条，请检查.");
                }
            }

            for (String relationIdAndValue : nestyListMap.keySet()) {

            }

            boolean isFirst = true;
            // 封装字段值
            for (Object key : resultMap.keySet()) {
                String fieldName = key.toString();
                // 凡是带有以'_'线开头的字段，都是连表的字段
                boolean isNestyModelField = key.toString().startsWith("_");
                // 这里只封装第一条结果集,并且不是嵌套的model里面的字段
                if (i == 0 && !isNestyModelField) {
                    metaObject.setValue(fieldName, resultMap.get(key));
                }
                // 以下划线 '_' 为开头的是嵌套的model字段
                if (isNestyModelField) {
                    MetaObject nestyObject = null;

                    String alias = fieldName.substring(1, fieldName.lastIndexOf("_"));
                    fieldName = fieldName.substring(fieldName.lastIndexOf("_") + 1);
                    Object value = metaObject.getValue(alias);
                    // 判断值是否是嵌套对象，如果是嵌套的list对象，那么需要进行在list里面根据关联id去进行对象的创建
                    if (value instanceof List) {
                        // 根据关联id去进行筛选.

                    } else if (i == 0) {
                        // 只有第一次的时候需要去进行设置
                        if (null == relationMap.get(value)) {
                            nestyObject = MetaObject.forObject(value, objectFactory, objectWrapperFactory, reflectorFactory);
                            relationMap.put(value, nestyObject);
                        } else {
                            nestyObject = relationMap.get(value);
                        }
                        nestyObject.setValue(fieldName, resultMap.get(key));
                    }
                }
            }
        }

        return newObject;
    }

    private static <T> T getObject(JoinMainTableSqlFilter joinMainTableSqlFilter, Class<T>[] clazz) {
        T newObject = null;
        if (null == clazz || clazz.length == 0) {
            newObject = (T) objectFactory.create(joinMainTableSqlFilter.getBaseEntity().getClass());
        } else {
            newObject = objectFactory.create(clazz[0]);
        }
        return newObject;
    }

    /**
     * 获取嵌套对象的结果集map
     * @param joinMainTableSqlFilter
     * @return
     */
    private static Map<String, NestyModel> getNestyModelMap(JoinMainTableSqlFilter joinMainTableSqlFilter) {
        Map<String, NestyModel> nestyModelMap = new HashMap<>();
        // 获取嵌套子对象在目标对象中是否是list类型的map
//        Map<String, Boolean> fieldAndFieldTypeIsListsMap = SqlUtil.fieldAndFieldTypeIsListsMap.get(joinMainTableSqlFilter.getBaseEntity().getClass());
        List<LeftJoibSqlFilter> leftJoibSqlFilterList = joinMainTableSqlFilter.getLeftJoibSqlFilterList();
        // 字段名--字段值     字段名--关联id
        for (LeftJoibSqlFilter leftJoibSqlFilter : leftJoibSqlFilterList) {
            // 判断是否是嵌套对象,嵌套查询的，需要定义嵌套查询的字段对象。否则当成普通参数去进行封装
            if (leftJoibSqlFilter.isNesty()) {
                NestyModel nestyModel = new NestyModel();
                // 设置关联id，方便后续在遍历迭代的时候，根据关联id去封装对象的时候，过滤掉重复的结果集
                String relationId = leftJoibSqlFilter.getRelationId();
                nestyModel.setRelationId(relationId);

                // 获取该字段在model里面的名称
                String alias = leftJoibSqlFilter.getAlias();
                Class<? extends BaseEntity> joinEntityClass = leftJoibSqlFilter.getJoinEntityClass();
                // 这里判断一下是否是list的属性，如果是的那么需要创建一个list对象，如果不是直接反射创建嵌套的对象即可。
//                if (fieldAndFieldTypeIsListsMap.get(alias)) {
                if (null == leftJoibSqlFilter.getRelationId()) {
                    List nestyModelList = new ArrayList<>();
                    nestyModel.setFieldValue(nestyModelList);
                    nestyModel.setListType(true);
                    nestyModel.setJoinEntityClass(joinEntityClass);
                } else {
                    BaseEntity baseEntity = objectFactory.create(joinEntityClass);
                    nestyModel.setFieldValue(baseEntity);
                    nestyModel.setListType(false);
                }
                nestyModelMap.put(alias, nestyModel);
            }
        }
        return nestyModelMap;
    }

    /**
     * 将list中的map转换为Model
     * @param resultMapList
     * @param joinMainTableSqlFilter
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(List<Map> resultMapList, JoinMainTableSqlFilter joinMainTableSqlFilter, Class<T> clazz) {
        return null;
    }

    /**
     * 将list中的map转换为Model,并分页
     * @param resultMapList
     * @param joinMainTableSqlFilter
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> PageInfo<T> toPage(List<Map> resultMapList, JoinMainTableSqlFilter joinMainTableSqlFilter, Class<T> clazz) {
        return null;
    }

    public static void main(String[] args) {
        JoinMainTableSqlFilter joinMainTableSqlFilter = new JoinMainTableSqlFilter(new UserEntity());
//        UserEntity userEntity = toModel(new HashMap(), joinMainTableSqlFilter);
//        System.out.println(userEntity);
        String kk = "_test_hello";
        System.out.println(kk.substring(kk.lastIndexOf("_") + 1));
        System.out.println(kk.substring(1, kk.lastIndexOf("_")));
    }

}

/**
 * 嵌套对象的相关信息
 */
@Data
class NestyModel {

    /** 关联id */
    private String relationId;
    /** 字段的值 */
    private Object fieldValue;
    /** 是否是list类型 */
    private boolean listType;
    /** 连接表的class */
    private Class joinEntityClass;

}
