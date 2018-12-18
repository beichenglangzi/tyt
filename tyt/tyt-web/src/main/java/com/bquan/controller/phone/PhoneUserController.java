package com.bquan.controller.phone;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bquan.bean.AjaxResponse;
import com.bquan.bean.UserBean;
import com.bquan.bean.Pager.Order;
import com.bquan.entity.mysql.Coupon;
import com.bquan.entity.mysql.Orders;
import com.bquan.entity.mysql.Product;
import com.bquan.entity.mysql.SendCode;
import com.bquan.entity.mysql.User;
import com.bquan.entity.mysql.UserLine;
import com.bquan.entity.mysql.UserLineP;
import com.bquan.entity.mysql.UserLine.UserLineType;
import com.bquan.entity.mysql.UserToken;
import com.bquan.service.read.CommissionRateReadService;
import com.bquan.service.read.CommissionReadService;
import com.bquan.service.read.CouponReadService;
import com.bquan.service.read.OrdersReadService;
import com.bquan.service.read.ProductReadService;
import com.bquan.service.read.SendCodeReadService;
import com.bquan.service.read.TipsReadService;
import com.bquan.service.read.UserCouponReadService;
import com.bquan.service.read.UserLineReadService;
import com.bquan.service.read.UserReadService;
import com.bquan.service.read.UserTokenReadService;
import com.bquan.service.read.WebDomainReadService;
import com.bquan.service.write.UserCouponWriteService;
import com.bquan.service.write.UserTokenWriteService;
import com.bquan.service.write.UserWriteService;
import com.bquan.util.DateUtils;
import com.bquan.util.DictionaryUtil;
import com.bquan.util.JsonUtil;
import com.bquan.util.MD5Util;

/**
 * 用户信息
 * 
 * @author hedaokun
 * 
 */
@Controller
@RequestMapping(value = "/phone/phoneUser")
public class PhoneUserController extends BasePhoneController {

	@Autowired
	private UserLineReadService userLineReadService;
	@Autowired
	private ProductReadService productReadService;
	@Autowired
	private UserReadService userReadService;
	@Autowired
	private CouponReadService couponReadService;
	@Autowired
	private UserCouponReadService userCouponReadService;
	@Autowired
	private UserCouponWriteService userCouponWriteService;
	@Autowired
	private CommissionReadService commissionReadService;
	@Autowired
	private UserWriteService userWriteService;
	@Autowired
	private CommissionRateReadService commissionRateReadService;
	@Autowired
	private TipsReadService tipsReadService;
	@Autowired
	private WebDomainReadService webDomainReadService;
	@Autowired
	private SendCodeReadService sendCodeReadService;
	@Autowired
	private UserTokenReadService userTokenReadService;
	@Autowired
	private UserTokenWriteService userTokenWriteService;
	@Autowired
	private OrdersReadService ordersReadService;

	/**
	 * 1.用户注册
	 * 
	 * @return
	 */
	@RequestMapping(value = "/register")
	@ResponseBody
	public Map<String, Object> register(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> responseMap = new HashMap<String, Object>();

		AjaxResponse ajaxRes = new AjaxResponse();
		try {
			/**
			 * 请求需要的参数
			 */
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String code = request.getParameter("code");

			/**
			 * 参数的校验
			 */
			if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(code)) {
				responseMap.put("status", "fail");
				responseMap.put("msg", "参数不能为空");
			}

			if (userReadService.getUser(username) != null) {
				responseMap.put("status", "fail");
				responseMap.put("msg", "账号已被注册！");
			}

			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("username", username);
			List<SendCode> codeList = sendCodeReadService.getTargetList(queryMap);
			if (codeList.size() == 0) {
				responseMap.put("status", "fail");
				responseMap.put("msg", "验证码错误！");
				return responseMap;
			}

			// 检验验证码是否过期
			SendCode sendCode = codeList.get(0);
			if ((new Date().getTime() - sendCode.getSendTime().getTime()) > 60 * 60 * 1000) {
				responseMap.put("status", "fail");
				responseMap.put("msg", "验证码已过期，请重新获取！");
				return responseMap;
			}

			// 未查询到验证码或者验证码不匹配
			if (!(code.equals(codeList.get(0).getCode()))) {
				responseMap.put("status", "fail");
				responseMap.put("msg", "验证码错误！");
				return responseMap;
			}

			// 生成分享码
			String share_code = userReadService.getRandomCode();

			/**
			 * 保存用户
			 */
			User usr = new User();
			usr.setShareCode(share_code);
			usr.setUsername(username);
			usr.setPassword(MD5Util.MD5(password));
			usr.setStatus(true);
			usr.setIsBlack(true);
			usr.setCreateDate(new Date());
			usr.setUserSource(true);
			usr.setLevel("0");// 设置等级为普通用户
			usr.setLeftCount(0);
			usr.setVipEndTime(new Date()); // 插件的vip截至时间
			usr.setPhoneEndTime(new Date()); // 手机端vip截至时间
			usr.setIsVipSend(false); // 标记佣金未赠送
			usr.setIsRegisterSend(false); // 此字段暂时无任何意义
			usr.setFmcode(""); // 邀请码，购买vip后，会给此用户赠送佣金
			usr.setIp(getIp(request));
			usr.setLoginDate(new Date());
			usr.setVersion("");
			// 赠送10分钟（false代表未赠送10分钟，首次登陆的时候会赠送）
			usr.setIsEmailSend(false);
			userWriteService.insert(usr);

			/**
			 * 赠送会员优惠券40和20的优惠券
			 */
			try {
				// 获取优惠券和会员信息
				Coupon coupon40 = couponReadService.getDataBySign("zchb40");
				Coupon coupon20 = couponReadService.getDataBySign("zchb20");
				userCouponWriteService.generateUserCoupon(coupon40, usr);
				userCouponWriteService.generateUserCoupon(coupon20, usr);
			} catch (Exception e) {
				// TODO: handle exception
			}

			responseMap.put("status", "success");
			responseMap.put("msg", "注册成功！");
			return responseMap;
		} catch (Exception e) {
			responseMap.put("status", "fail");
			responseMap.put("msg", "注册失败！");
			return responseMap;
		}
	}

	/**
	 * 2.用户登录
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/login")
	@ResponseBody
	public Map<String, Object> gologin(HttpServletRequest request, HttpServletResponse response) {

		/**
		 * 获取请求参数
		 */
		Map<String, Object> responseMap = new HashMap<String, Object>();
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password");

		try {
			// 生成token
			UUID uuid = UUID.randomUUID();
			String randomString = uuid.toString();

			/**
			 * 参数的校验
			 */
			// 用户名不能为空
			if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
				responseMap.put("status", "fail");
				responseMap.put("msg", "参数不能为空！");
				return responseMap;
			}

			User user = userReadService.getUser(username);

			// 用户名不存在
			if (user == null) {
				responseMap.put("status", "fail");
				responseMap.put("msg", "用户不存在！");
				return responseMap;
			}

			// 密码错误
			if (!MD5Util.MD5(password).equals(user.getPassword())) {
				responseMap.put("status", "fail");
				responseMap.put("msg", "密码错误！");
				return responseMap;
			}

			/**
			 * 数据库中已经存在登陆的，就把已登录的踢下线
			 */
			List<UserToken> userTokenList = userTokenReadService.getByUsername(username);
			if (userTokenList != null && userTokenList.size() > 0) {
				for (UserToken uk : userTokenList) {
					userTokenWriteService.delete(uk);
				}
			}

			// 保存新登陆的token
			UserToken userToken = new UserToken();
			userToken.setCreateDate(new Date());
			userToken.setModifyDate(new Date());
			userToken.setToken(randomString);
			userToken.setUsername(username);
			userTokenWriteService.insert(userToken);

			responseMap.put("token", randomString);
			responseMap.put("level", user.getLevel());
			responseMap.put("status", "success");
			responseMap.put("msg", "登陆成功");
			return responseMap;
		} catch (Exception e) {
			responseMap.put("status", "fail");
			responseMap.put("msg", "登陆失败！");
			return responseMap;
		}
	}

	/**
	 * 获取用户信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getInfo")
	@ResponseBody
	public Map<String, Object> getInfo(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		String username = request.getParameter("username").trim();
		if (StringUtils.isEmpty(username)) {
			responseMap.put("status", "fail");
			responseMap.put("msg", "参数不能为空！");
			return responseMap;
		}
		User user = userReadService.getUser(username);
		if (user == null) {
			responseMap.put("status", "fail");
			responseMap.put("msg", "用户不存在！");
			return responseMap;
		}
		long viplefttime = 0;
		long vipendtime = 0;
		if (user.getPhoneEndTime() != null && user.getPhoneEndTime().after(new Date())) {
			vipendtime = user.getPhoneEndTime().getTime();
			viplefttime = user.getPhoneEndTime().getTime() - new Date().getTime();
		}

		responseMap.put("level", user.getLevel());
		responseMap.put("vipendtime", vipendtime);
		responseMap.put("viplefttime", viplefttime);
		responseMap.put("status", "success");
		responseMap.put("msg", "成功");
		return responseMap;
	}

	/**
	 * 获取线路信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getLine")
	@ResponseBody
	public Map<String, Object> getLine(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("userLine", userLineReadService.getByType(UserLineType.phone));
		responseMap.put("status", "success");
		responseMap.put("msg", "成功");
		return responseMap;
	}

	/**
	 * 进行token的校验
	 * 
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/checkToken")
	@ResponseBody
	public AjaxResponse checkToken(@RequestParam(value = "username", defaultValue = "") String username,
			@RequestParam(value = "token", defaultValue = "") String token) {
		AjaxResponse ajaxRes = new AjaxResponse();
		if (StringUtils.isEmpty(token)) {
			ajaxRes.setCode(-1);
			ajaxRes.setMsg("校验失败");
			return ajaxRes;
		}
		List<UserToken> list = userTokenReadService.getByToken(token);
		if (list != null && list.size() > 0) {
			ajaxRes.setCode(0);
			ajaxRes.setMsg("校验成功");
			return ajaxRes;
		} else {
			ajaxRes.setCode(-1);
			ajaxRes.setMsg("校验失败");
			return ajaxRes;
		}
	}

	/**
	 * 找回密码
	 * 
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/getpassword")
	@ResponseBody
	public AjaxResponse getpassword(@RequestParam(value = "username", defaultValue = "") String username,
			@RequestParam(value = "code", defaultValue = "") String code,
			@RequestParam(value = "password", defaultValue = "") String password,
			@RequestParam(value = "repassword", defaultValue = "") String repassword) {
		AjaxResponse ajaxRes = new AjaxResponse();
		if (StringUtils.isEmpty(username)) {
			ajaxRes.setCode(AjaxResponse.FAILURE);
			ajaxRes.setMsg("账号不能为空");
			return ajaxRes;
		}
		if (StringUtils.isEmpty(code)) {
			ajaxRes.setMsg("邮箱验证码不能为空！");
			ajaxRes.setCode(AjaxResponse.FAILURE);
			return ajaxRes;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", username);
		map.put("orderBy", "sendCode.send_time");
		map.put("orderDesc", "desc");
		List<SendCode> codeList = sendCodeReadService.getTargetList(map);

		if (codeList.size() == 0) {
			ajaxRes.setMsg("验证码错误！");
			ajaxRes.setCode(AjaxResponse.FAILURE);
			return ajaxRes;
		}

		// 检验验证码是否过期
		SendCode sendCode = codeList.get(0);
		if ((new Date().getTime() - sendCode.getSendTime().getTime()) > 60 * 60 * 1000) {
			ajaxRes.setMsg("验证码已过期，请重新获取！");
			ajaxRes.setCode(AjaxResponse.FAILURE);
			return ajaxRes;
		}

		// 未查询到验证码或者验证码不匹配
		if (!(code.equals(codeList.get(0).getCode()))) {
			ajaxRes.setMsg("验证码错误！");
			ajaxRes.setCode(AjaxResponse.FAILURE);
			return ajaxRes;
		}

		if (StringUtils.isEmpty(password) || StringUtils.isEmpty(repassword)) {
			ajaxRes.setMsg("密码为空！");
			ajaxRes.setCode(AjaxResponse.FAILURE);
			return ajaxRes;
		}

		if (!password.equals(repassword)) {
			ajaxRes.setMsg("密码和确认密码不一致！");
			ajaxRes.setCode(AjaxResponse.FAILURE);
			return ajaxRes;
		}

		// 查询用户
		User user = userReadService.getUser(username);
		if (user == null) {
			ajaxRes.setMsg("账号不存在！");
			ajaxRes.setCode(AjaxResponse.FAILURE);
			return ajaxRes;
		}

		user.setPassword(MD5Util.MD5(password));
		userWriteService.update(user);

		ajaxRes.setCode(AjaxResponse.SUCCESS);
		ajaxRes.setMsg("密码修改成功，请通过新密码登录！");
		return ajaxRes;

	}

	/**
	 * 获取版本信息
	 * 
	 * @param request
	 * @param response
	 * @param version
	 *            版本号
	 * @param type
	 *            手机类型 （0 安卓 1 ios）
	 * @return
	 */
	@RequestMapping(value = "/getVersion")
	@ResponseBody
	public Map<String, Object> getVersion(HttpServletRequest request, HttpServletResponse response, String type) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		String version = request.getParameter("version").trim();
		boolean isup = isupdate(version, type);
		if (type.equals("0")) {
			if (isup) {
				responseMap.put("msg", "有新版本需要更新");
				responseMap.put("code", "1");
				responseMap.put("url", "http://ydvpn.9ni.com:8080/tyt/download/app-debug.apk");
			} else {
				responseMap.put("msg", "已是最新版本无需要更新");
				responseMap.put("code", "0");
			}
		} else {
			if (isup) {
				responseMap.put("msg", "有新版本需要更新");
				responseMap.put("code", "1");
			} else {
				responseMap.put("msg", "已是最新版本无需要更新");
				responseMap.put("code", "0");
			}
		}
		return responseMap;
	}

	public boolean isupdate(String version, String type) {
		boolean isUpdated = false;
		String versionUrl;
		if (type.equals("0")) {
			versionUrl = "http://ydvpn.9ni.com:8080/tyt/download/phonever.txt";
		} else {
			versionUrl = "http://ydvpn.9ni.com:8080/tyt/download/ios.txt";
		}
		URL url = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader netVer = null;
		// 读取网络上的版本号
		try {
			url = new URL(versionUrl);
			is = url.openStream();
			isr = new InputStreamReader(is);

			netVer = new BufferedReader(isr);
			String netVerStr = netVer.readLine();
			String localVerStr = version;

			if (netVerStr.equals(localVerStr)) {
				// msg.append("当前文件是最新版本\n");
				isUpdated = false;
			} else {
				isUpdated = true;
			}

		} catch (MalformedURLException ex) {
		} catch (IOException ex) {
		} finally {
			// 释放资源
			try {
				netVer.close();
				isr.close();
				is.close();
			} catch (IOException ex1) {
			}
		}
		return isUpdated;
	}

	/**
	 * 2.手机游客用户登录
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/phonelogin", method = RequestMethod.POST)
	public String phoneLogin(HttpServletRequest request, HttpServletResponse response) {
		/**
		 * 获取请求参数
		 */
		Map<String, Object> responseMap = new HashMap<String, Object>();
		String username = request.getParameter("username").trim(); // 安卓手机唯一标识
		// String type = request.getParameter("type").trim(); // 手机类型（0 安卓 1 ios
		// 2 游客）
		String share_code = null;
		try {
			// 生成token
			// UUID uuid = UUID.randomUUID();
			// String randomString = uuid.toString();
			/**
			 * 参数的校验
			 */
			// 用户名不能为空
			if (StringUtils.isEmpty(username)) {
				responseMap.put("status", "fail");
				responseMap.put("msg", "参数不能为空！");
				return output(response, JsonUtil.toJson(responseMap));
			}
			User user = userReadService.getUser(username);
			// 用户名不存在
			if (user == null) {
				// 生成分享码
				Calendar cal = Calendar.getInstance();
				share_code = userReadService.getRandomCode();
				User usr = new User();
				usr.setShareCode(share_code);
				usr.setUsername(username);
				usr.setPassword(MD5Util.MD5(MD5Util.MD5("123")));
				usr.setStatus(true);
				usr.setIsBlack(true);
				usr.setCreateDate(new Date());
				usr.setUserSource(true);
				usr.setLevel("0");// 设置等级为普通用户
				usr.setLeftCount(0);
				usr.setVipEndTime(cal.getTime());
				usr.setSendDate(null);
				usr.setIsVipSend(false); // 标记佣金未赠送
				usr.setIsRegisterSend(false); // 此字段暂时无任何意义
				usr.setFmcode(""); // 邀请码，购买vip后，会给此用户赠送佣金
				usr.setIp(getIp(request));
				usr.setLoginDate(new Date());
				usr.setVersion("");
				// 赠送10分钟（false代表未赠送10分钟，首次登陆的时候会赠送）
				usr.setIsEmailSend(true);
				usr.setPhonenum(username);
				usr.setType("2");
				userWriteService.insert(usr);
				user = userReadService.getUser(username);
			}
			/**
			 * 判断用户的vip时间，到期后将等级更新为普通用户
			 */
			if (user.getVipEndTime() != null && user.getVipEndTime().before(new Date())) {
				user.setLevel("0");
				userWriteService.update(user);
				responseMap.put("status", "fail");
				responseMap.put("msg", "体验时间已经到期,不能游客登录！");
				return output(response, JsonUtil.toJson(responseMap));
			}
			else if(Integer.parseInt(user.getLevel())>0)
			{
				responseMap.put("status", "fail");
				responseMap.put("msg", "会员不能游客登录！");
				return output(response, JsonUtil.toJson(responseMap));
			}
			/**
			 * 免费用户赠送vip时间
			 */
			try {
				sendTime(user.getPhonenum());
			} catch (Exception e) {

			}

			// 查询用户的vip时间
			long vipendtime = 0;
			long viplefttime = 0;
			if (user.getVipEndTime() == null || user.getVipEndTime().before(new Date())) {
				vipendtime = new Date().getTime() / 1000;
				viplefttime = 0;
			} else {
				vipendtime = user.getVipEndTime().getTime() / 1000;
				viplefttime = vipendtime - new Date().getTime() / 1000;
			}

			// 查询用户的套餐信息
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", username);
			map.put("orderStatus", "2");
			map.put("orderBy", "orders_pay_date");
			map.put("orderDesc", "desc");
			List<Orders> productList = ordersReadService.getTargetList(map);
			if (productList.size() > 0) {
				responseMap.put("productList", productList.get(0));
			} else {
				responseMap.put("productList", "");
			}
			responseMap.put("viplefttime", viplefttime);
			responseMap.put("vipendtime", user.getVipEndTime());
			responseMap.put("status", "success");
			responseMap.put("share_code", user.getShareCode());
			responseMap.put("msg", "登陆成功");
			return output(response, JsonUtil.toJson(responseMap));
		} catch (Exception e) {
			responseMap.put("status", "fail");
			responseMap.put("msg", "登陆失败！");
			return output(response, JsonUtil.toJson(responseMap));
		}
	}

	/**
	 * 2.用户登录
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/phoneviplogin", method = RequestMethod.POST)
	public String phoneviplogin(HttpServletRequest request, HttpServletResponse response) {

		/**
		 * 获取请求参数
		 */
		Map<String, Object> responseMap = new HashMap<String, Object>();
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password");

		try {

			// 生成token
			UUID uuid = UUID.randomUUID();
			String randomString = uuid.toString();

			/**
			 * 参数的校验
			 */
			// 用户名不能为空
			if (StringUtils.isEmpty(username)) {
				responseMap.put("msg", "账号不能为空！");
				responseMap.put("code", AjaxResponse.FAILURE);
				return output(response, JsonUtil.toJson(responseMap));
			}

			User user = userReadService.getUser(username);

			// 用户名不存在
			if (user == null) {
				responseMap.put("msg", "用户名不存在！");
				responseMap.put("code", AjaxResponse.FAILURE);
				return output(response, JsonUtil.toJson(responseMap));
			}

			// 密码错误
			if (!MD5Util.MD5(password).equals(user.getPassword())) {
				responseMap.put("msg", "密码错误！");
				responseMap.put("code", AjaxResponse.FAILURE);
				return output(response, JsonUtil.toJson(responseMap));
			}

			/**
			 * 数据库中已经存在登陆的，就把已登录的踢下线
			 */
			logout(user);

			/**
			 * 判断用户的vip时间，到期后将等级更新为普通用户
			 */
			if (user.getVipEndTime() != null && user.getVipEndTime().before(new Date())) {
				user.setLevel("0");
				userWriteService.update(user);
			}

			/**
			 * 免费用户赠送vip时间
			 */
			try {
				sendTime(user.getPhonenum());
			} catch (Exception e) {
				System.out.println(username + "赠送10分钟失败");
			}

			/**
			 * 更新用户登录信息
			 */
			user.setIp(getIp(request));
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			user.setLoginDate(new Date());
			// user.setVersion(version);
			user.setToken(randomString);
			userWriteService.update(user);

			// 保存新登陆的token
			UserToken userToken = new UserToken();
			userToken.setCreateDate(new Date());
			userToken.setModifyDate(new Date());
			userToken.setToken(randomString);
			userToken.setUsername(username);
			userTokenWriteService.insert(userToken);
			// redis中保存 token和username的对应关系
			// redisUtil.set(randomString+"-index", username);
			// redisUtil.set(username+"-index", randomString);

			/**
			 * 记录登录日志
			 */
			// LogLoginModel logLogin = new LogLoginModel();
			// logLogin.setUsername(username);
			// logLogin.setVersion(version);
			// logLogin.setCreate_date(new Date());
			// logLoginService.insert(logLogin);

			/**
			 * 设置返回给客户端的信息
			 * 
			 */
			// 查询用户的套餐信息
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", username);
			map.put("orderStatus", "2");
			map.put("orderBy", "orders_pay_date");
			map.put("orderDesc", "desc");
			List<Orders> productList = ordersReadService.getTargetList(map);
			if (productList.size() > 0) {
				responseMap.put("productList", productList.get(0));
			} else {
				responseMap.put("productList", "");
			}

			UserBean bean = new UserBean();
			bean.setId(user.getId());
			BeanUtils.copyProperties(bean, user);
			if (user.getVipEndTime() != null) {
				bean.setVip_end_time(String.valueOf(user.getVipEndTime().getTime()));
			}
			bean.setTime_now(String.valueOf(new Date().getTime()));
			bean.setToken(randomString);
			bean.setQq(DictionaryUtil.getDictionaryValue("dictionary_tyt_qq"));
			bean.setShare_code(user.getShareCode());

			responseMap.put("user", bean);
			responseMap.put("msg", "登录成功！");
			responseMap.put("code", AjaxResponse.SUCCESS);
		} catch (Exception e) {
			responseMap.put("msg", "登录失败！");
			responseMap.put("code", AjaxResponse.FAILURE);
		}

		return output(response, JsonUtil.toJson(responseMap));
	}

	/**
	 * 套餐信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/phoneproduct", method = RequestMethod.POST)
	public String phoneproduct(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderBy", "product.day");
		map.put("orderDesc", "asc");
		List<Product> productList = productReadService.getTargetList(map);
		if (productList.size() > 0) {
			responseMap.put("productList", productList);
			responseMap.put("status", "success");
			responseMap.put("code", "0");
		}
		if (productList.size() <= 0) {
			responseMap.put("productList", "");
			responseMap.put("status", "fail");
			responseMap.put("code", "-1");
		}
		return output(response, JsonUtil.toJson(responseMap));
	}

	/**
	 * 手机注册
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/phoneregister", method = RequestMethod.POST)
	public String phoneregister(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password");
		String type = request.getParameter("type").trim(); // 手机类型（0 安卓 1 ios 2
															// 游客 3pc）
		String phonenum = request.getParameter("phonenum").trim(); // 手机唯一标识

		// 用户名不能为空
		if (StringUtils.isEmpty(phonenum)) {
			responseMap.put("status", "fail");
			responseMap.put("msg", "手机唯一标识不能为空！");
			return output(response, JsonUtil.toJson(responseMap));
		}

		List<User> user = userReadService.selectphonenumtime(phonenum);

		// 查询用户
		User u = userReadService.getUser(username);
		if (u != null) {
			responseMap.put("status", "fail");
			responseMap.put("msg", "该账户已被注册！");
			return output(response, JsonUtil.toJson(responseMap));
		}

		Calendar cal = Calendar.getInstance();
		String share_code = userReadService.getRandomCode();
		User usr = new User();
		usr.setShareCode(share_code);
		usr.setUsername(username);
		usr.setPassword(MD5Util.MD5(MD5Util.MD5(password)));
		usr.setStatus(true);
		usr.setIsBlack(true);
		usr.setCreateDate(new Date());
		usr.setUserSource(true);
		usr.setLevel("0");// 设置等级为普通用户
		usr.setLeftCount(0);
		if (user.size() > 0) {
			usr.setVipEndTime(user.get(0).getVipEndTime());
		} else {
			usr.setVipEndTime(cal.getTime());
		}
		usr.setSendDate(null);
		usr.setIsVipSend(false); // 标记佣金未赠送
		usr.setIsRegisterSend(false); // 此字段暂时无任何意义
		usr.setFmcode(""); // 邀请码，购买vip后，会给此用户赠送佣金
		usr.setIp(getIp(request));
		usr.setLoginDate(new Date());
		usr.setVersion("");
		// 赠送10分钟（false代表未赠送10分钟，首次登陆的时候会赠送）
		usr.setIsEmailSend(true);
		usr.setType(type);
		usr.setPhonenum(phonenum);
		int num = userWriteService.insert(usr);
		if (num > 0) {
			responseMap.put("share_code", share_code);
			responseMap.put("msg", "注册成功！");
			responseMap.put("status", "success");
		} else {
			responseMap.put("msg", "注册失败！");
			responseMap.put("status", "fail");
		}
		return output(response, JsonUtil.toJson(responseMap));
	}

	/**
	 * 购买记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/orderslog", method = RequestMethod.POST)
	public String orderslog(HttpServletRequest request, HttpServletResponse response, String username) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", username);
		map.put("orderStatus", "2");
		map.put("orderBy", "orders_pay_date");
		map.put("orderDesc", "desc");
		List<Orders> productList = ordersReadService.getTargetList(map);
		if (productList.size() > 0) {
			for (Orders o : productList) {
				o.setPayDateStr(DateUtils.formatDateTime(o.getPayDate()));
			}
			responseMap.put("productList", productList);
		} else {
			responseMap.put("productList", "");
		}
		return output(response, JsonUtil.toJson(responseMap));
	}

	/**
	 * 修改密码
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/updatepassword", method = RequestMethod.POST)
	public String updatepassword(HttpServletRequest request, HttpServletResponse response, String username,
			String oldpassword, String newpassword) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		// 用户名不能为空
		if (StringUtils.isEmpty(username)) {
			responseMap.put("status", "fail");
			responseMap.put("msg", "参数不能为空！");
			return output(response, JsonUtil.toJson(responseMap));
		}
		User user = userReadService.getUser(username);

		// 用户名不存在
		if (user == null) {
			responseMap.put("msg", "用户名不存在！");
			responseMap.put("code", AjaxResponse.FAILURE);
			return output(response, JsonUtil.toJson(responseMap));
		}

		// 密码错误
		if (!MD5Util.MD5(oldpassword).equals(user.getPassword())) {
			responseMap.put("msg", "原密码错误！");
			responseMap.put("code", AjaxResponse.FAILURE);
			return output(response, JsonUtil.toJson(responseMap));
		}
		user.setPassword(MD5Util.MD5(newpassword));
		int num = userWriteService.update(user);
		if (num > 0) {
			responseMap.put("status", "success");
			responseMap.put("msg", "密码修改成功！");
		}
		return output(response, JsonUtil.toJson(responseMap));
	}

	/**
	 * 用户信息（购买套餐刷新）
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @throws IllegalAccessException
	 */
	@RequestMapping(value = "/getUser", method = RequestMethod.POST)
	public String getUser(HttpServletRequest request, HttpServletResponse response, String username)
			throws IllegalAccessException, Exception {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		// 用户名不能为空
		if (StringUtils.isEmpty(username)) {
			responseMap.put("status", "fail");
			responseMap.put("msg", "参数不能为空！");
			return output(response, JsonUtil.toJson(responseMap));
		}
		// 查询用户
		User u = userReadService.getUser(username);
		if (u != null) {
			UserBean bean = new UserBean();
			BeanUtils.copyProperties(bean, u);
			if (u.getVipEndTime() != null) {
				bean.setVip_end_time(String.valueOf(u.getVipEndTime().getTime()));
			}
			bean.setTime_now(String.valueOf(new Date().getTime()));
			responseMap.put("user", bean);
		}
		// 查询用户的套餐信息
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", username);
		map.put("orderStatus", "2");
		map.put("orderBy", "orders_pay_date");
		map.put("orderDesc", "desc");
		List<Orders> productList = ordersReadService.getTargetList(map);
		if (productList.size() > 0) {
			responseMap.put("productList", productList.get(0));
		} else {
			responseMap.put("productList", "");
		}
		return output(response, JsonUtil.toJson(responseMap));
	}

	/**
	 * 用户线路（带层级关系）
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @throws IllegalAccessException
	 */
	@RequestMapping(value = "/getUserline", method = RequestMethod.POST)
	public String getUserline(HttpServletRequest request, HttpServletResponse response, String username)
			throws IllegalAccessException, Exception {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		List<UserLineP> l = new ArrayList<UserLineP>();
		List<UserLine> pidline = userLineReadService.getPinfp();
		for (UserLine u : pidline) {
			UserLineP p = userLineReadService.getAllLine(u);
			l.add(p);
		}
		responseMap.put("status", "success");
		responseMap.put("msg", "成功");
		responseMap.put("userline", l);
		return output(response, JsonUtil.toJson(responseMap));
	}

	/**
	 * 获取随机线路
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @throws IllegalAccessException
	 */
	@RequestMapping(value = "/getRandomline", method = RequestMethod.POST)
	public String getRandomline(HttpServletRequest request, HttpServletResponse response, String username)
			throws IllegalAccessException, Exception {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		List<UserLine> userline = userLineReadService.getAllList();
		int random = RandomUtils.nextInt(0, 19);
		responseMap.put("status", "success");
		responseMap.put("msg", "成功");
		responseMap.put("userline", userline.get(random));
		return output(response, JsonUtil.toJson(responseMap));
	}

}
