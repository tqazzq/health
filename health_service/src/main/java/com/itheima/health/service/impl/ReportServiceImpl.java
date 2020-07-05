package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.service.ReportService;
import com.itheima.health.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 21:34 2020/7/3
 */
@Service(interfaceClass = ReportService.class)
public class ReportServiceImpl implements ReportService {
    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    /**
     * 查询运营数据
     *
     * @return
     */
    @Override
    public Map<String, Object> getBusinessReportDate() {
        Map<String, Object> reportData = new HashMap<>();
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //星期一
        String monday = sdf.format(DateUtils.getFirstDayOfWeek(today));
        //星期天
        String sunday = sdf.format(DateUtils.getLastDayOfWeek(today));
        //本月最后一天
        String lastDayOfMonth = sdf.format(DateUtils.getLastDayOfThisMonth());
        //1号
        String firstDayOfMonth = sdf.format(DateUtils.getFirstDay4ThisMonth());
        String reportDate = sdf.format(today);
        //会员数量
        //todayNewMember 今日新增会员
        int todayNewMember = memberDao.findMemberCountByDate(reportDate);
        //totalMember会员总数
        int totalMember = memberDao.findMemberTotalCount();
        //thisWeekNewMember 本周新增会员数
        int thisWeekNewMember = memberDao.findMemberCountAfterDate(monday);
        //thisMonthNewMember 本月新增会员数
        int thisMonthNewMember = memberDao.findMemberCountAfterDateByMonth(firstDayOfMonth);
        //订单统计
        //todayOrderNumber 今日预约数
        int todayOrderNumber = orderDao.findOrderCountByDate(reportDate);
        //todayVisitsNumber 今日到诊数
        int todayVisitsNumber = orderDao.findVisitsCountByDate(reportDate);
        //thisWeekOrderNumber 本周预约数
        int thisWeekOrderNumber = orderDao.findOrderCountBetweenDate(monday, sunday);
        //thisWeekVisitsNumber 本周到诊数
        int thisWeekVisitsNumber = orderDao.findVisitsCountAfterDate(monday);
        //thisMonthOrderNumber 本月预约数
        int thisMonthOrderNumber = orderDao.findOrderCountOfMonth(firstDayOfMonth, lastDayOfMonth);
        //thisMonthVisitsNumber 本月到诊数
        int thisMonthVisitsNumber = orderDao.findVisitsCountOfMonth(firstDayOfMonth);
        //热门套餐
        List<Map<String, Object>> hotSetmeal = orderDao.findHostSetmeal();
        reportData.put("reportDate", reportDate);
        reportData.put("todayNewMember", todayNewMember);
        reportData.put("totalMember", totalMember);
        reportData.put("thisWeekNewMember", thisWeekNewMember);
        reportData.put("thisMonthNewMember", thisMonthNewMember);
        reportData.put("todayOrderNumber", todayOrderNumber);
        reportData.put("todayVisitsNumber", todayVisitsNumber);
        reportData.put("thisWeekOrderNumber", thisWeekOrderNumber);
        reportData.put("thisWeekVisitsNumber", thisWeekVisitsNumber);
        reportData.put("thisMonthOrderNumber", thisMonthOrderNumber);
        reportData.put("thisMonthVisitsNumber", thisMonthVisitsNumber);
        reportData.put("hotSetmeal", hotSetmeal);
        return reportData;
    }


}
