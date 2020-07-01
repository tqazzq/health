package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.Member;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 19:36 2020/6/30
 */
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Autowired
    private MemberDao memberDao;

    /**
     * 预约提交
     *
     * @param orderInfo
     * @return
     */
    @Transactional
    @Override
    public Order submit(Map<String, Object> orderInfo) {
        String orderDate = (String) orderInfo.get("orderDate");
        Date orderSettingDate = null;
        try {
            //日期格式判断
            orderSettingDate = new SimpleDateFormat("yyyy-MM-dd").parse(orderDate);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new HealthException("日期格式转换异常");
        }
        //通过日期查询预约设置信息
        OrderSetting orderSetting = orderSettingDao.findByorderSettingDate(orderSettingDate);
        //判断是否能预约(若今天没有设置预约人数则不能预约)
        if (null == orderSetting) {
            throw new HealthException("当前日期不能预约");
        }
        //若查询出的日期有预约信息则进行判断 if number <= reservations 则报错提示预约已满
        if (orderSetting.getNumber() <= orderSetting.getReservations()) {
            throw new HealthException("当前预约人数已满");
        }
        //未满则可以继续预约 设置预约人数加一
        orderSettingDao.editReservationsByOrderDate(orderSettingDate);
        //根据id预约等日期新建一个订单
        Order order = new Order();
        order.setOrderDate(orderSettingDate);
        //通过orderInfo设置orderDate 和setmeal 的id信息
        order.setSetmealId((Integer.valueOf((String) orderInfo.get("setmealId"))));//Object类型先转为String 再转为integer
        //通过手机查询是否为会员
        String telephone = (String) orderInfo.get("telephone");
        Member member = memberDao.findByTelephone(telephone);
        //会员不存在
        if (null == member) {
            //添加新会员并在套餐中添加会员Id
            member = new Member();
            member.setSex(((String) orderInfo.get("sex")));
            member.setIdCard(((String) orderInfo.get("idCard")));
            member.setName(((String) orderInfo.get("name")));
            member.setPhoneNumber(telephone);
            member.setRegTime(orderSettingDate);
            memberDao.addMember(member);
//            member.setRemark();
            order.setMemberId(member.getId());
        } else {
            //在这里设置订单的会员id
            order.setMemberId(member.getId());
            /**根据id查询会员是否存在相同的套餐 **这里是重点一定要用套餐去查询对比套餐有没有重复的每一项都要对比你才能确定他是不是重复的
             * 只对比id的话你第二次去预约即使是预约了其他项目它再根据id去查也是重复 不能预约是  很严重的逻辑错误
             *
             * */
            List<Order> list = memberDao.findOrderByMemberOrder(order);
            if (list.size() > 0 && null != list) {
                //存在返回报错不能重复预约
                throw new HealthException("不能重复预约套餐");
            }
        }
        /**预约类型与预约方式要在sql之后添加因为预约类型不能作为动态sql的查询条件 这样微信端电脑端预约一样的套餐因为字段不同不会报重复,
         *你写进去的话当做了条件,动态sql有一个不同他就不会返回是同一个套餐 而你若是在微信端和电脑端/电话预约预约同一天同一个套餐的话
         *在拿order去比对是否是相同的套餐的时候的因为选择的类型不一样他就判断为不同的套餐 就会存在你可以用不同方式重复预约同一天同一个套餐的错误逻辑现象
         *所以到诊与否 与预约类型不能作为区别是相同套餐的条件 只能根据日期 套餐id 会员id 3个条件来判断
         * */
        order.setOrderStatus(Order.ORDERSTATUS_NO);
        //预约方式
        order.setOrderType(((String) orderInfo.get("orderType")));
        //添加新套餐
        orderDao.add(order);
        return order;
    }

    @Override
    //查询预约信息
    public Map<String, Object> findById4Detail(Integer id) {
        return orderDao.findById4Detail(id);
    }
}
