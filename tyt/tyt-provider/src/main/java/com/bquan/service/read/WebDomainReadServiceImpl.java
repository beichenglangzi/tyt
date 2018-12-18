package com.bquan.service.read;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import com.bquan.bean.WebDomainBean;
import com.bquan.dao.read.WebDomainReadMapper;
import com.bquan.entity.mysql.WebDomain;

/**
 * 列表设置属性 Service层读接口实现
 * 
 * @author liuxiaokang
 * @createTime 2016-08-20
 */
public class WebDomainReadServiceImpl extends BaseReadServiceImpl<WebDomain> implements WebDomainReadService {

	@Resource
	private WebDomainReadMapper webDomainReadMapper;

	@Override
	public WebDomainReadMapper getBaseReadMapper() {
		return webDomainReadMapper;
	}

	public List<WebDomainBean> convertData(List<WebDomain> webDomainList) {
		List<WebDomainBean> resultList = new ArrayList<WebDomainBean>();
		for (WebDomain wb : webDomainList) {
			WebDomainBean bean = new WebDomainBean();
			bean.setCreate_date(wb.getCreateDate());
			bean.setDomain(wb.getDomain());
			bean.setId(wb.getId());
			bean.setIs_wall(wb.getIsWall());
			bean.setStatus(wb.getStatus());
			bean.setType(wb.getType());
			bean.setUser_id(wb.getUserId());
			resultList.add(bean);
		}
		return resultList;
	}

	public WebDomainBean convertToBean(WebDomain wb) {
		WebDomainBean bean = new WebDomainBean();
		bean.setCreate_date(wb.getCreateDate());
		bean.setDomain(wb.getDomain());
		if (wb.getId() == null) {
			bean.setId(0);
		} else {
			bean.setId(wb.getId());
		}
		bean.setIs_wall(wb.getIsWall());
		bean.setStatus(wb.getStatus());
		bean.setType(wb.getType());
		bean.setUser_id(wb.getUserId());
		return bean;
	}

	@Override
	public List<WebDomain> finddeptlist(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return webDomainReadMapper.finddeptlist(map);
	}

}
