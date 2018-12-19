package com.massestech.javabasetemplate.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.massestech.common.mybatis.provider.SelectTemplate;
import com.massestech.common.mybatis.sqlfilter.SqlFilterBuilder;
import com.massestech.common.mybatis.sqlfilter.join.JoinMainTableSqlFilter;
import com.massestech.common.mybatis.sqlfilter.where.TableSqlFilter;
import com.massestech.common.mybatis.utils.ResultsUtils;
import com.massestech.common.utils.ExcelUtils;
import com.massestech.common.utils.ServiceUtil;
import com.massestech.common.web.PageInfoView;
import com.massestech.javabasetemplate.controller.domain.ExcelUser;
import com.massestech.javabasetemplate.controller.domain.UserView;
import com.massestech.javabasetemplate.domain.BookEntity;
import com.massestech.javabasetemplate.domain.EnumerateEntity;
import com.massestech.javabasetemplate.domain.UserEntity;
import com.massestech.javabasetemplate.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Long save(UserEntity userEntity) {
        userMapper.insert(userEntity);
        // 获取数据的id
        return userEntity.getId();
    }

    public void update(UserEntity userEntity) {
        userMapper.updateSelective(userEntity);
    }

    public void delete(Long id) {
        userMapper.deleteById(id);
    }

    public UserEntity queryById(Long id) {
        UserEntity userEntity = userMapper.findById(id);
        return userEntity;
    }

    public PageInfo<UserView> query(PageInfoView<UserView> pageInfoView) {
        // 所有查询没有指定排序的情况下,默认都是使用创建时间倒序
        // 相当于select * from user where age = #{age} and userName like #{%userName%} and deleted = 0 order by createTime desc
        UserEntity userEntity = ServiceUtil.instantiate(pageInfoView, UserEntity.class);
        // 使用like模糊查询
        SqlFilterBuilder.buildWhere(userEntity).like("userName", userEntity.getUserName());
        List<UserView> list = userMapper.pageQuery(userEntity);
        PageInfo<UserView> pageInfo=new PageInfo<>(list);
        return pageInfo;
    }

    public int sum() {
        // 相当于select ifnull(sum(age),0) from table where deleted = 0 and userName = 'lkp'
        UserEntity userEntity = SqlFilterBuilder.buildColumn(UserEntity.class).sum("age").getEntity();
        userEntity.setUserName("lkp");
        int sum = userMapper.sum(userEntity);
        return sum;
    }

    public List<UserView> queryByIds(String ids) {
        UserEntity userEntity = SqlFilterBuilder.buildWhere(UserEntity.class).in("id", ids).getEntity();
        // 设置使用id进行倒序
        userEntity.setOrderByDesc("id");
        // select * from user where deleted = 0 and id in (ids) order by id desc
        List<UserView> userViews = userMapper.pageQuery(userEntity);
        return userViews;
    }

    public List<UserView> queryByTime(Date beginTime, Date endTime) {
        UserEntity userEntity = SqlFilterBuilder.buildWhere(UserEntity.class).between("createdTime", beginTime, endTime).getEntity();
        List<UserView> userViews = userMapper.pageQuery(userEntity);
        return userViews;
    }

    public int upload(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        // 跳过第一行
        List<ExcelUser> list = ExcelUtils.excel2Model(inputStream, ExcelUser.class, 0, 1);

        // 解析列表
        String listStr = JSON.toJSONString(list);
        List<UserEntity> entityList = JSON.parseArray(listStr, UserEntity.class);
        int i = userMapper.insertBatch(entityList);
        return i;
    }

    public List<UserView> export() {
        UserEntity userEntity = new UserEntity();
        List<UserView> list = userMapper.pageQuery(userEntity);
        return list;
    }

    public UserEntity join() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(5L);
        SqlFilterBuilder.buildLeftJoin(userEntity).leftJoin(EnumerateEntity.class).on("sexStatus", "key")
                .columnAndField("keyStr", "sexStr");
        UserEntity join = userMapper.join(userEntity);
        return join;
    }

    public UserEntity joinOne() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(5L);
        SqlFilterBuilder.buildLeftJoin(userEntity).leftJoin(EnumerateEntity.class, "enumerate").on("sexStatus", "key");
        Map map = userMapper.joinOne(userEntity);
        UserEntity user = ResultsUtils.toModel(map, userEntity);
        return user;
    }


    public UserEntity joinNesty() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(5L);
        SqlFilterBuilder.buildLeftJoin(userEntity).leftJoin(BookEntity.class, "bookEntityList")
                    .on("id", "userId").relationId("id")
                .leftJoin(EnumerateEntity.class)
                    .on("sexStatus", "key")
                .columnAndField("keyStr", "sexStr");
        List<Map> joinNesty = userMapper.joinNesty(userEntity);
        UserEntity user = ResultsUtils.toModel(joinNesty, userEntity);
        return user;
    }

    // select * from tableA where property = (select childTableProperty from tableB where column = #{column} and deleted = 0) and deleted = 0
    // select * from tableA where property in (select childTableProperty from tableB where column = #{column} and deleted = 0) and deleted = 0
    public void test() {
        UserEntity userEntity = new UserEntity();

        UserEntity childEntity = new UserEntity();
        childEntity.setAge(10);
        childEntity.setUserName("kp");
        SqlFilterBuilder.buildWhere(childEntity).eq("userName", "lkp");
        SqlFilterBuilder.buildWhere(userEntity).inTableSql("id", "id", childEntity)
                .eq("userName", "kkx");

        SelectTemplate<UserEntity> selectTemplate = new SelectTemplate();
        String list = selectTemplate.findList(userEntity);
        System.out.println(list);

//        SELECT id,user_name,age,sex_status,created_time,last_update_time
//        FROM user
//        WHERE ( deleted=0 and id in (select id from user where  and user_name = #{sqlFilterAdapter.sqlFilterParamMap.param_2387034200079926202_0} and age=#{sqlFilterAdapter.sqlFilterParamMap.param_-8731221078123934630_2}) and user_name = #{sqlFilterAdapter.sqlFilterParamMap.param_1128393104621381204_1})
//        ORDER BY created_time desc
    }


    // 需要合并查询的条件
    public static void main(String[] args) {

//        select a.* from tableA a left join tableB b on a.id = b.parentId
//        select a.* from (select * from tableA where condition = #{condition} limit ? ,?) a left join tableB b on a.id = b.parentId

//        // 构建查询条件
//        Query query = SqlFilterBuilder.buildTable("主表model").leftJoin("子表model").on("条件").relationId("关联id");
//        Map map = mapper查询;
//        // 在处理数据的时候顺便根据query的条件去进行count查询,然后封装到page里面
//        Page<Model> page = util.dealData(query, map);


    }

}
