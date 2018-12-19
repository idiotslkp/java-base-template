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

//    findLeftJoin
//    findNestyLeftJoin

//    findLeftJoinList
//    findNestyLeftJoinList

//    findLeftJoinPage
//    findNestyLeftJoinPage

    /**
     * 封装单条的嵌套数据，后续需要优化，没有字段的，那个值就跳过，不去进行设置.
     * @param resultMap
     * @param baseEntity
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toModel(Map resultMap, BaseEntity baseEntity, Class<T>... clazz) {
        T newObject = getObject(baseEntity.getClass(), clazz);
        MetaObject metaObject =  MetaObject.forObject(newObject, objectFactory, objectWrapperFactory, reflectorFactory);

        JoinMainTableSqlFilter joinMainTableSqlFilter = (JoinMainTableSqlFilter) baseEntity.getSqlFilterAdapter().getJoinSqlFilter();
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
                setValue(metaObject, alias, joinEntity);
            }
        }
        // 遍历，将结果集设置到model里面
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
                setValue(nestyObject, fieldName, resultMap.get(key));
            } else {
                setValue(metaObject, fieldName, resultMap.get(key));
            }
        }

        return newObject;
    }

    /**
     * 将map转换为model
     * @param resultMapList
     * @param baseEntity
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toModel(List<Map> resultMapList, BaseEntity baseEntity, Class<T>... clazz) {
        JoinMainTableSqlFilter joinMainTableSqlFilter = (JoinMainTableSqlFilter) baseEntity.getSqlFilterAdapter().getJoinSqlFilter();
        T newObject = getObject(baseEntity.getClass(), clazz);
        Object id = null;
        MetaObject metaObject = null;
        MetaObject nestyMetaObject = null;

        // 嵌套对象的结果集map，key是连接表的别名,也是model里面的嵌套字段的名称
        Map<String, NestyModel> nestyMap = getNestyModelMap(joinMainTableSqlFilter);
        // 存放嵌套的list对象的字段名的集合
        List<String> listTypeAliasList = null;

        // key:id值_关联id名_关联id值
        Map<String, MetaObject> nestyListMap = new HashMap<>();
        // 关联对象的map
        Map<String, MetaObject> nestyModelMap = new HashMap<>();

        // 遍历结果集，封装成嵌套的model
        for (int i = 0; i < resultMapList.size(); i++) {
//            String
            Map resultMap = resultMapList.get(i);
            if (i == 0) {
                id  = resultMap.get("id");
                metaObject = MetaObject.forObject(newObject, objectFactory, objectWrapperFactory, reflectorFactory);
                listTypeAliasList = new ArrayList<>();
                // 每个model里面的嵌套结果集初始化.
                for (String alias : nestyMap.keySet()) {
                    // 还没有初始化的，给他初始化一次.
                    if (null == metaObject.getValue(alias)) {
                        NestyModel nestyModel = nestyMap.get(alias);
                        Object nestyFieldValue = null;
                        // 判断是否是list类型
                        if (nestyModel.isListType()) {
                            nestyFieldValue = new ArrayList<>();
                            listTypeAliasList.add(alias);
                        } else {
                            nestyFieldValue = objectFactory.create(nestyModel.getJoinEntityClass());
                            nestyMetaObject = MetaObject.forObject(nestyFieldValue, objectFactory, objectWrapperFactory, reflectorFactory);
                            nestyModelMap.put(alias, nestyMetaObject);
                        }
                        setValue(metaObject, alias, nestyFieldValue);
                    }
                }
            } else {
                if (!id.equals(resultMapList.get(i).get("id"))) {
                    // todo 异常，因为查询结果只能允许一条，多个不同的id说明查询出来的结果集大于1条.
                    log.info("该查询只能返回一条结果集，但是结果却返回了多条，请检查.");
                }
            }

            // 遍历list的别名，然后看看该
            for (String alias : listTypeAliasList) {
                String nestyListKey = id + "_" + alias + "_" + resultMap.get("_" + alias + "_" + "id");
                if (nestyListMap.get(nestyListKey) == null) {
                    NestyModel nestyModel = nestyMap.get(alias);
                    Object obj =  objectFactory.create(nestyModel.getJoinEntityClass());
                    nestyMetaObject = MetaObject.forObject(obj, objectFactory, objectWrapperFactory, reflectorFactory);
                    List nestyList = (List) metaObject.getValue(alias);
                    nestyList.add(obj);
                    nestyListMap.put(nestyListKey, nestyMetaObject);
                }
            }

            // 封装字段值
            for (Object key : resultMap.keySet()) {
                String fieldName = key.toString();
                // 凡是带有以'_'线开头的字段，都是连表的字段
                boolean isNestyModelField = key.toString().startsWith("_");
                // 这里只封装第一条结果集,并且不是嵌套的model里面的字段
                if (i == 0 && !isNestyModelField) {
                    setValue(metaObject, fieldName, resultMap.get(key));
                }
                // 以下划线 '_' 为开头的是嵌套的model字段
                if (isNestyModelField) {

                    String alias = fieldName.substring(1, fieldName.lastIndexOf("_"));
                    fieldName = fieldName.substring(fieldName.lastIndexOf("_") + 1);
                    Object value = metaObject.getValue(alias);
                    // 判断值是否是嵌套对象，如果是嵌套的list对象，那么需要进行在list里面根据关联id去进行对象的创建
                    if (value instanceof List) {
                        // 根据关联id去进行筛选.
                        String nestyListKey = id + "_" + alias + "_" + resultMap.get("_" + alias + "_" + "id");

                        nestyMetaObject = nestyListMap.get(nestyListKey);

                        setValue(nestyMetaObject, fieldName, resultMap.get(key));
//                        if (nestyListMap.get(nestyListKey) == null) {
////                            nestyMetaObject =
//                            NestyModel nestyModel = nestyMap.get(alias);
//                            nestyMetaObject = MetaObject.forObject(objectFactory.create(nestyModel.getJoinEntityClass()), objectFactory, objectWrapperFactory, reflectorFactory);
//                            nestyListMap.put(nestyListKey, nestyMetaObject);
//                        } else {
//                            nestyMetaObject = nestyListMap.get(nestyListKey);
//
//                        }
                    } else if (i == 0) {
                        // 只有第一次的时候需要去进行设置
                        nestyMetaObject = nestyModelMap.get(alias);
                        setValue(nestyMetaObject, fieldName, resultMap.get(key));
                    }
                }
            }
        }

        return newObject;
    }

    private static <T> T getObject(Class<? extends BaseEntity> entityClass, Class<T>[] clazz) {
        T newObject = null;
        if (null == clazz || clazz.length == 0) {
            newObject = (T) objectFactory.create(entityClass);
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
                String alias = leftJoibSqlFilter.getAlias().substring(1);
                Class<? extends BaseEntity> joinEntityClass = leftJoibSqlFilter.getJoinEntityClass();
                // 这里判断一下是否是list的属性，如果是的那么需要创建一个list对象，如果不是直接反射创建嵌套的对象即可。
                if (null != leftJoibSqlFilter.getRelationId()) {
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

    /**
     * 将字段名对应的值，设置到model里面中去，只设置有setter方法的字段.
     * @param metaObject
     * @param name
     * @param value
     */
    private static void setValue(MetaObject metaObject, String name, Object value) {
        // 只对有set的字段进行设置
        if (metaObject.hasSetter(name)) {
            metaObject.setValue(name, value);
        }
    }

    public static void main(String[] args) {
        JoinMainTableSqlFilter joinMainTableSqlFilter = new JoinMainTableSqlFilter(new UserEntity());
        String kk = "_test_hello";
        System.out.println(kk.substring(kk.lastIndexOf("_") + 1));
        System.out.println(kk.substring(1, kk.lastIndexOf("_")));
        UserEntity userEntity = new UserEntity();
        MetaObject metaObject = MetaObject.forObject(userEntity, objectFactory, objectWrapperFactory, reflectorFactory);
        System.out.println(metaObject.hasSetter("test"));
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
