package com.bquan.controller.pc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONArray;
import com.bquan.bean.AjaxResponse;
import com.bquan.bean.Pagination;
import com.bquan.bean.WebDomainBean;
import com.bquan.entity.mysql.WebDomain;
import com.bquan.service.read.TipsReadService;
import com.bquan.service.read.WebDomainReadService;
import com.bquan.service.write.WebDomainWriteService;
import com.bquan.util.JsonUtil;
import com.bquan.util.RedisUtil;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * 域名信息
 * 
 * @author liuxiaokang
 * 
 */
@Controller
@RequestMapping(value = "/pc/webDomain")
public class PcWebDomainController extends BasePcController {

	@Autowired
	private TipsReadService tipsReadService;
	@Autowired
	private WebDomainWriteService webDomainWriteService;
	@Autowired
	private WebDomainReadService webDomainReadService;
	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 添加用户域名
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addMain(HttpServletRequest request, HttpServletResponse response) {
		AjaxResponse ajaxRes = new AjaxResponse();
		List<WebDomain> l = new ArrayList<WebDomain>();
		/**
		 * 获取参数
		 */
		String domain = request.getParameter("domain");
		String user_id = request.getParameter("user_id");
		String depname = request.getParameter("depname");
		String domains[] = null;
		if (StringUtils.isEmpty(domain) || StringUtils.isEmpty(user_id) || StringUtils.isEmpty(depname)) {
			ajaxRes.setMsg("添加失败！");
			ajaxRes.setCode(AjaxResponse.FAILURE);
			return output(response, JsonUtil.toJson(ajaxRes));
		}
		try {
			if (domain.indexOf(",") > 0) {
				domains = domain.split(",");
				for (int i = 0; i < domains.length; i++) {
					WebDomain webDomain = new WebDomain();
					webDomain.setDomain(domains[i]);
					webDomain.setUserId(user_id);
					webDomain.setCreateDate(new Date());
					webDomain.setType("0");
					webDomain.setStatus("0");
					webDomain.setIsWall("0");
					l.add(webDomain);
				}
				webDomainWriteService.insertBatch(l);
			} else {
				WebDomain webDomain = new WebDomain();
				webDomain.setDomain(domain);
				webDomain.setUserId(user_id);
				webDomain.setCreateDate(new Date());
				webDomain.setType("0");
				webDomain.setStatus("0");
				webDomain.setIsWall("0");
				webDomainWriteService.insert(webDomain);
			}
			// 把最新的科学列表信息存进缓存中
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", 0);
			map.put("userId", user_id);
			map.put("isWall", 0);
			map.put("depname", depname);
			List<WebDomain> userDomainList = webDomainReadService.finddeptlist(map);
			List<WebDomainBean> userdomainList = webDomainReadService.convertData(userDomainList);
			redisUtil.setoj(depname + "userdomain-t", userdomainList);

			ajaxRes.setRecord(userdomainList);
			ajaxRes.setMsg("添加成功！");
			ajaxRes.setCode(AjaxResponse.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			ajaxRes.setMsg("添加失败！");
			ajaxRes.setCode(AjaxResponse.FAILURE);
		}
		return output(response, JsonUtil.toJson(ajaxRes));
	}

	/**
	 * 获取用户域名
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getUserDomain", method = RequestMethod.POST)
	public String getUserDomain(HttpServletRequest request, HttpServletResponse response) {

		AjaxResponse ajaxRes = new AjaxResponse();

		/**
		 * 获取参数
		 */
		String type = request.getParameter("type");// 0个人 1系统
		String user_id = request.getParameter("user_id");
		String is_wall = request.getParameter("is_wall");// 0 需要翻墙 1 不需要翻墙
		String depname = request.getParameter("depname");

		try {
			Pagination page = new Pagination();
			String key = "";
			if ("0".equals(type)) {
				key = depname + "userdomain-t";
			} else {
				key = "systemdomain-t";
			}
			List<WebDomainBean> userdomainList = null;
			if (redisUtil.exists(key)) {
				userdomainList = (List<WebDomainBean>) redisUtil.getoj(key);
			} else {
				if (type.equals("0")) {  
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("type", type);
					map.put("userId", user_id);
					map.put("isWall", is_wall);
					map.put("depname", depname);
					List<WebDomain> webDomainList = webDomainReadService.finddeptlist(map);
					userdomainList = webDomainReadService.convertData(webDomainList);
					redisUtil.setoj(key, userdomainList);
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("type", type);
					map.put("userId", user_id);
					map.put("isWall", is_wall);
					List<WebDomain> webDomainList = webDomainReadService.getTargetList(map);
					userdomainList = webDomainReadService.convertData(webDomainList);
					redisUtil.setoj(key, userdomainList);
				}
			}
			page.setResults(userdomainList);
			ajaxRes.setPage(page);
			ajaxRes.setMsg("查询成功!");
			ajaxRes.setCode(AjaxResponse.SUCCESS);
		} catch (Exception e) {
			ajaxRes.setMsg("查询失败!");
			ajaxRes.setCode(AjaxResponse.FAILURE);
			e.printStackTrace();
		}
		return output(response, JsonUtil.toJson(ajaxRes));
	}

	/**
	 * 删除用户域名
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/deleteUserDom", method = RequestMethod.POST)
	public String deleteUserDom(HttpServletRequest request, HttpServletResponse response) {

		AjaxResponse ajaxRes = new AjaxResponse();

		// 域名的id
		String id = request.getParameter("id");
		String depname = request.getParameter("depname");
		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(depname)) {
			ajaxRes.setMsg("请求参数异常!");
			ajaxRes.setCode(AjaxResponse.FAILURE);
			return output(response, JsonUtil.toJson(ajaxRes));
		}
		try {
			WebDomain webDomain = webDomainReadService.get(id);
			if (webDomain == null) {
				ajaxRes.setMsg("该域名不存在!");
				ajaxRes.setCode(AjaxResponse.FAILURE);
				return output(response, JsonUtil.toJson(ajaxRes));
			}
			webDomainWriteService.delete(webDomain);

			// 把最新的科学列表信息存进缓存中
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", 0);
			map.put("userId", webDomain.getUserId());
			map.put("isWall", 0);
			map.put("depname", depname);
			List<WebDomain> userDomainList = webDomainReadService.finddeptlist(map);
			List<WebDomainBean> userdomainList = webDomainReadService.convertData(userDomainList);
			// 保存数据到redis中
			redisUtil.setoj(depname + "userdomain-t", userdomainList);

			ajaxRes.setMsg("删除成功!");
			ajaxRes.setCode(AjaxResponse.SUCCESS);
		} catch (Exception e) {
			ajaxRes.setMsg("删除失败!");
			ajaxRes.setCode(AjaxResponse.FAILURE);
		}
		return output(response, JsonUtil.toJson(ajaxRes));
	}

}
