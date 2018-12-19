package com.massestech.javabasetemplate.controller;

import com.github.pagehelper.PageInfo;
import com.massestech.common.utils.DateUtils;
import com.massestech.common.utils.ExcelUtils;
import com.massestech.common.web.PageInfoView;
import com.massestech.common.web.RestResponse;
import com.massestech.common.web.SimpleController;
import com.massestech.javabasetemplate.controller.domain.UserView;
import com.massestech.javabasetemplate.domain.UserEntity;
import com.massestech.javabasetemplate.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "用户列表")
@RequestMapping("/user")
@RestController
public class UserController extends SimpleController {

    @Autowired
    private UserService userService;

    @ApiOperation("新增")
    @PostMapping
    public RestResponse save(@RequestBody UserView userView) {
        // 校验以及转换
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userView, userEntity);
        Long id = userService.save(userEntity);
        return success(id);
    }

    @ApiOperation("修改")
    @PutMapping
    public RestResponse update(@RequestBody UserView userView) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userView, userEntity);
        userService.update(userEntity);
        return success();
    }

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public RestResponse delete(@PathVariable Long id) {
        userService.delete(id);
        return success();
    }

    @ApiOperation("根据id查询单条")
    @GetMapping("/{id}")
    public RestResponse queryById(@PathVariable Long id) {
        UserEntity userEntity = userService.queryById(id);
        return success(userEntity);
    }

    @ApiOperation("分页查询")
    @PostMapping("/query")
    public RestResponse<PageInfo<UserView>> query(@RequestBody PageInfoView<UserView> pageInfoView) {
        PageInfo<UserView> result = userService.query(pageInfoView);
        return success(result);
    }

    @ApiOperation("得到年龄的总和,演示sum函数")
    @GetMapping("/sum")
    public RestResponse sum() {
        int sum = userService.sum();
        return success(sum);
    }

    @ApiOperation("根据ids查询,id之间用,隔开")
    @GetMapping("queryByIds/{ids}")
    public RestResponse<List<UserView>> queryByIds(@PathVariable String ids) {
        List<UserView> list = userService.queryByIds(ids);
        return success(list);
    }

    @ApiOperation("查询位于创建时间位于某时间段内的数据")
    @GetMapping("queryByTime/{beginTime}/{endTime}")
    public RestResponse<List<UserView>> queryByTime(@PathVariable Date beginTime, @PathVariable Date endTime) {
        List<UserView> list = userService.queryByTime(beginTime, endTime);
        return success(list);
    }

    @ApiOperation(value = "导入excel")
    @ResponseBody
    @RequestMapping(value="/upload",method = RequestMethod.POST)
    public RestResponse upload(@ApiParam(value="文件", required = true) @RequestParam("file") MultipartFile file) throws Exception {
        int upload = userService.upload(file);
        return success("成功导入数据" + upload + "条");
    }

    @ApiOperation(value = "导出excel")
    @GetMapping("/export")
    @ResponseBody
    public ResponseEntity<?> export() throws IOException {
        List<UserView> list = userService.export();
        String dateString = DateUtils.formatDateByStyle(new Date(), "yyyyMMdd");
        String filePath = "static/download/user_template.xlsx";
        Map<String,Object> data = new HashMap<>();

        data.put("list", list);
        String downLoadFileName =  "用户列表" + "--" + dateString + ".xlsx";
        return ExcelUtils.getResponseEntity(data, filePath, downLoadFileName);
    }

    @ApiOperation("测试连表查询")
    @GetMapping("join")
    public RestResponse join() {
        UserEntity join = userService.join();
        return success(join);
    }

    @ApiOperation("测试连表查询,返回一条")
    @GetMapping("joinOne")
    public RestResponse joinOne() {
        UserEntity user = userService.joinOne();
        return success(user);
    }

    @ApiOperation("测试连表查询")
    @GetMapping("joinNesty")
    public RestResponse joinNesty() {
        UserEntity user = userService.joinNesty();
        return success(user);
    }

}
