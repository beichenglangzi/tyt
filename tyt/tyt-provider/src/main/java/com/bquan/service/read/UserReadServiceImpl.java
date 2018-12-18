package com.bquan.service.read;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.bquan.dao.read.UserReadMapper;
import com.bquan.entity.mysql.User;
import com.bquan.util.RandomCodeUtil;


public class UserReadServiceImpl extends BaseReadServiceImpl<User> implements UserReadService {
 
	@Resource
	private UserReadMapper userReadMapper;
	
	@Override
	public UserReadMapper getBaseReadMapper() {
		return userReadMapper;
	}
	
	public User getUser(String username) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("username", username);
		List<User> userList = userReadMapper.select(map);
		return userList.size()>0?userList.get(0):null;
	}

	public Integer count(String time) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("likeTime", time);
		return userReadMapper.count(map);
	}

	public User getByShareCode(String shareCode) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("shareCode", shareCode);
		List<User> userList = userReadMapper.select(map);
		return userList.size()>0?userList.get(0):null;
	}

	public Integer countUser(Date beginDate, Date endDate,boolean isVip) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("timeBy", "user.create_date");
		Calendar be = Calendar.getInstance();
		be.setTime(beginDate);
		Calendar en = Calendar.getInstance();
		en.setTime(endDate);
		
		be.set(
				be.get(Calendar.YEAR), 
				be.get(Calendar.MONTH), 
				be.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		map.put("beginDate", be.getTime());
		en.set(
				en.get(Calendar.YEAR), 
				en.get(Calendar.MONTH), 
				en.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
		map.put("endDate", en.getTime());
		if(isVip){
			map.put("extraSql", " and (user.level=2 or user.level=3 or user.level=4)");
		}
		return count(map);
	}
	
	public String getRandomCode() {
		// 生成6位的随机码
		String randomCode = RandomCodeUtil.getRandomCode(6);
		// 检测随机码在数据库中是否存在
		User user = getByShareCode(randomCode);
		// 如果存在重新生成，直到生成数据库中不存在的随机码
		while(user!=null){
			randomCode = RandomCodeUtil.getRandomCode(6);
			user = getByShareCode(randomCode);
		}
		return randomCode;
	}

	@Override
	public List<User> selectFmcode(String beginDate,String endDate) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("beginDate", beginDate);
		map.put("endDate", endDate);
		return userReadMapper.selectFmcode(map);
	}

	@Override
	public List<User> selectphonenum(String phonenum) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("phonenum", phonenum);
		List<User> userList = userReadMapper.select(map);
		return userList;
	}

	@Override
	public List<User> selectphonenumtime(String phonenum) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("phonenum", phonenum);
		map.put("type", "2");
		List<User> userList = userReadMapper.select(map);
		return userList;
	}

}