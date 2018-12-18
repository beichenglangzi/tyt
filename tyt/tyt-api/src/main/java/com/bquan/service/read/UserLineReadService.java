package com.bquan.service.read;

import java.util.HashMap;
import java.util.List;

import com.bquan.bean.UserLineBean;
import com.bquan.entity.mysql.User;
import com.bquan.entity.mysql.UserLine;
import com.bquan.entity.mysql.UserLineP;
import com.bquan.entity.mysql.UserLine.UserLineType;

/**
 * 节点线路 Service读数据接口
 * @author liuxiaokang
 * @createTime 2016-08-24
 */
public interface UserLineReadService extends BaseReadService<UserLine>{

	public List<UserLineBean> convertData(List<UserLine> userLineList);
	
	/**
	 * 通过线路类型获取线路
	 * @param type
	 * @return
	 */
	public List<UserLine> getByType(UserLineType type);
	
	/**
	 * 从redis中加载所有的线路信息
	 * @return
	 */
	public List<UserLineBean> getAllLineFromRedis();
	
	/**
	 * 把所有的信息加载到redis中去
	 */
	public void loadAllDataToRedis();
	
	public List<UserLineBean> getAllLineFromRedis(User user);
	
	/*
	 * 得到线路父节下面的子节点
	 */
	public UserLineP getAllLine(UserLine pidline);
	
	/*
	 * 得到线路父节点
	 */
	public List<UserLine> getPinfp();
}



