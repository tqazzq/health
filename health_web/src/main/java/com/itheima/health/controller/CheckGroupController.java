package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Tian Qing
 * @Daate: Created in 16:10 2020/6/23
 */
@RestController
@RequestMapping("/checkgroup")
public class CheckGroupController {

    @Reference
    private CheckGroupService checkGroupService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('CHECKGROUP_ADD')")
    public Result add(@RequestBody CheckGroup checkGroup, Integer[] checkitemIds) {
        checkGroupService.add(checkGroup, checkitemIds);
        return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }

    //分页查询检查组
    @PostMapping("/findPage")
    @PreAuthorize("hasAnyAuthority('CHECKGROUP_QUERY')")
    public Result findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult<CheckGroup> pageResult = checkGroupService.findPage(queryPageBean);
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, pageResult);
    }

    //根据Id查询
    @RequestMapping("/findById")
    @PreAuthorize("hasAnyAuthority('CHECKITEM_QUERY')")
    public Result findById(Integer id) {
        CheckGroup checkGroup = checkGroupService.findById(id);
        if (checkGroup != null) {
            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, checkGroup);
        }
        return new Result(false, MessageConstant.QUERY_CHECKGROUP_FAIL);
    }

    //查询改检查组包含的所有检查项id
    @RequestMapping("/findCheckItemIdsByCheckGroupId")

    public Result findCheckItemIdsByCheckGroupId(Integer id) {
        List<Integer> list = checkGroupService.findCheckItemIdsByCheckGroupId(id);
        return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS, list);
    }

    //发送请求提交数据模型 修改检查组
    @PostMapping("/edit")
    @PreAuthorize("hasAnyAuthority('CHECKGROUP_EDIT')")
    public Result edit(@RequestBody CheckGroup checkGroup, Integer[] checkitemIds) {
        try {
            checkGroupService.edit(checkGroup, checkitemIds);
        } catch (Exception e) {
            return new Result(false, MessageConstant.EDIT_CHECKGROUP_FAIL);
        }
        return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
    }

    //删除检查组
    @RequestMapping("/deleteById")
    @PreAuthorize("hasAnyAuthority('CHECKGROUP_DELETE')")
    public Result deleteById(Integer id) {
        checkGroupService.deleteById(id);
        return new Result(true, MessageConstant.DELETE_CHECKGROUP_SUCCESS);
    }

    //查询所有
    @RequestMapping("/findAll")
    @PreAuthorize("hasAnyAuthority('CHECKGROUP_QUERY')")
    public Result findAll() {
        List<CheckGroup> checkGroupList = checkGroupService.findAll();
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroupList);
    }
}
