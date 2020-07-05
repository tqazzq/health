package com.itheima.health.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.dao.SetmealDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiNiuUtils;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 23:31 2020/6/25
 */
@Service(interfaceClass = SetmealService.class)
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${out_put_path}")
    private String out_put_path;

    @Override
    @Transactional
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        //获取套餐的id
        Integer setmealId = setmeal.getId();
        //添加套餐和检查组的关系
        if (null != setmealId) {
            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.addSetmealCheckGroup(setmealId, checkgroupId);
            }
        }
        //新增套餐后重新生成静态页面
        generateMobileStaticHtml();
    }

    /**
     * 调用方法生成静态页面
     */
    private void generateMobileStaticHtml() {
        try {
            //套餐列表静态页面
            generateSetmealListHtml();
            //套餐详情静态页面
            generateSetmealDetailHtml();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //静态详情页面生成所有的静态详情页面(测试)
    }

    private void generateSetmealDetailHtml() throws Exception {
        //获取模板套餐列表模板
        Template template = freeMarkerConfigurer.getConfiguration().getTemplate("moblie_setmeal.ftl");
        //获取数据模型
        List<Setmeal> setmealList = setmealDao.findAll();
        //图片地址
        setmealList.forEach(s ->{
            s.setImg(QiNiuUtils.DOMAIN + s.getImg());
        });
        //map集合存入数据
        Map<String,Object> map = new HashMap<>();
        map.put("setmealList",setmealList);
        //获取文件地址
        File setmealListFile = new File(out_put_path, "m_setmeal.html");
        //输入
        template.process(map,new BufferedWriter(new OutputStreamWriter(new FileOutputStream(setmealListFile),"utf-8")));
    }

    /**
     * 查询所有的检查组检查项生成静态页面
     */
    private void generateSetmealListHtml() throws Exception {
        // 把所有套餐都生成详情页面 方便测试
        List<Setmeal> list = setmealDao.findAll();
        // setmealList中的套餐是没有详情信息，即没有检查组也没有检查项的信息，要查询一遍
        for (Setmeal setmeal : list) {
            // 获取套餐详情
            Setmeal setmealDatil = setmealDao.findById(setmeal.getId());
            // 设置套餐的图片路径
            setmealDatil.setImg(QiNiuUtils.DOMAIN + setmealDatil.getImg());
            // 生成详情页面
            generateDetailHtml(setmealDatil);
        }

    }

    /**
     * 调用方法生成具体的套餐静态页面
     *
     * @param setmealDatil
     */
    private void generateDetailHtml(Setmeal setmealDatil) throws Exception {
//        获取模板套餐列表的模板
        Template template = freeMarkerConfigurer.getConfiguration().getTemplate("moblie_setmeal_detail.ftl");
        //初始化一个集合用于存贮套餐对象
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("setmeal", setmealDatil);
        File setmealDatailFile = new File(out_put_path, "setmeal_" + setmealDatil.getId() + ".html");
        template.process(dataMap, new BufferedWriter(new OutputStreamWriter(new FileOutputStream(setmealDatailFile), "utf-8")));
    }

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult<Setmeal> findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());
        //根据添加模糊查询
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())) { //如果有搜索的查询 则进行模糊查询
            queryPageBean.setQueryString("%" + queryPageBean.getQueryString() + "%");// 在原有的查询语句上加上模糊匹配的%
        }
        //条件查询
        Page<Setmeal> page = setmealDao.findByCondition(queryPageBean.getQueryString());
        //返回结果集和总记录数
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public Setmeal findById(Integer id) {
        Setmeal setmeal = setmealDao.findById(id);
        return setmeal;
    }

    @Override
    public List<Integer> findCheckgroupIdsBySetmealId(Integer id) {

        List<Integer> list = setmealDao.findCheckgroupIdsBySetmealId(id);
        return list;
    }

    @Override
    @Transactional
    public void update(Setmeal setmeal, Integer[] checkgroupIds) {
        //先更新套餐信息
        setmealDao.update(setmeal);
        //删除酒管系
        setmealDao.deleteSetmealCheckGroup(setmeal.getId());
        //建立新关系
        if (null != checkgroupIds) {
            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.addSetmealCheckGroup(setmeal.getId(), checkgroupId);
            }
        }
        generateMobileStaticHtml();
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        //先检查套餐有没有被订单使用,如果被使用了则不能删除
        int count = setmealDao.findOrderCountBySetmealId(id);
        if (count > 0) {
            throw new HealthException(MessageConstant.DELETE_STMEAL_IN_USE);
        }
        //没有被使用的话先删除套餐和检查组的关系再删除套餐
        setmealDao.deleteSetmealCheckGroup(id);
        setmealDao.deleteSetmeal(id);
    }

    @Override
    public List<Setmeal> findAll() {
        //新增套餐后重新生成静态页面
//        generateMobileStaticHtml(); 为了方便测试生成静态页面 新加的方法
        return setmealDao.findAll();
    }

    @Override
    public Setmeal findDetailById(Integer id) {
        return setmealDao.findByDetailById(id);
    }

    /**
     * 查询套餐占比用饼状图展示
     * @return
     */
    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }


}
