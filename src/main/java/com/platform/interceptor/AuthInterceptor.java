package com.platform.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.PropKit;
import com.platform.constant.ConstantAuth;
import com.platform.constant.ConstantInit;
import com.platform.constant.ConstantWebContext;
import com.platform.mvc.base.BaseController;
import com.platform.mvc.group.Group;
import com.platform.mvc.operator.Operator;
import com.platform.mvc.role.Role;
import com.platform.mvc.station.Station;
import com.platform.mvc.syslog.Syslog;
import com.platform.mvc.user.User;
import com.platform.tools.ToolDateTime;
import com.platform.tools.ToolWeb;
import com.platform.tools.security.ToolIDEA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Date;

/**
 * 权限认证拦截器
 * @author 董华健
 * 描述：
 * 1.处理权限验证
 * 2.处理全局异常
 * 3.处理权限相关的工具类方法
 */
public class AuthInterceptor implements Interceptor {

	private static Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

	@Override
	public void intercept(Invocation invoc) {
		BaseController contro = (BaseController) invoc.getController();
		HttpServletRequest request = contro.getRequest();
		HttpServletResponse response = contro.getResponse();

		log.info("获取reqSysLog!");
		Syslog reqSysLog = contro.getAttr(ConstantWebContext.reqSysLogKey);
		contro.setReqSysLog(reqSysLog);

		log.info("获取用户请求的URI，两种形式，参数传递和直接request获取");
		String uri = invoc.getActionKey(); // 默认就是ActionKey
		if (invoc.getMethodName().equals(ConstantWebContext.request_toUrl)) {
			uri = ToolWeb.getParam(request, ConstantWebContext.request_toUrl); // 否则就是toUrl的值
		}

		log.info("druid特殊处理");
		if (uri.startsWith("/platform/druid")) {
			uri = "/platform/druid/iframe.html"; // 所有的druid授权都绑定到一个iframe.html授权
		}

		log.info("获取当前用户!");
		boolean userAgentVali = true; // 是否验证userAgent，默认是
		if (uri.equals("/jf/platform/ueditor") || uri.equals("/jf/platform/upload")) { // 针对ueditor特殊处理
			userAgentVali = false;
		}
		User user = getCurrentUser(request, response, userAgentVali);// 当前登录用户
		if (null != user) {
			reqSysLog.set(Syslog.column_userids, user.getPKValue());
			contro.setAttr(ConstantWebContext.request_cUser, user);
			contro.setAttr(ConstantWebContext.request_cUserIds, user.getPKValue());
		}

		log.info("获取URI对象!");
		Object operatorObj = Operator.dao.cacheGet(uri);

		log.info("判断URI是否存在!");
		if (null == operatorObj) {
			log.info("URI不存在!");

			log.info("访问失败时保存日志!");
			reqSysLog.set(Syslog.column_status, "0");// 失败
			reqSysLog.set(Syslog.column_description, "URL不存在");
			reqSysLog.set(Syslog.column_cause, "1");// URL不存在

			log.info("返回失败提示页面!");
			toView(contro, ConstantAuth.auth_no_url, "权限认证过滤器检测：URI不存在");
			return;
		}

		log.info("URI存在!");
		Operator operator = (Operator) operatorObj;
		reqSysLog.set(Syslog.column_operatorids, operator.getPKValue());

		if (operator.get("privilegess").equals("1")) {// 是否需要权限验证
			log.info("需要权限验证!");
			if (user == null) {
				log.info("权限认证过滤器检测:未登录!");

				reqSysLog.set(Syslog.column_status, "0");// 失败
				reqSysLog.set(Syslog.column_description, "未登录");
				reqSysLog.set(Syslog.column_cause, "2");// 2 未登录

				toView(contro, ConstantAuth.auth_no_login, "权限认证过滤器检测：未登录");
				return;
			}

			if (!hasPrivilegeUrl(user.getPKValue(), uri)) {// 权限验证
				log.info("权限验证失败，没有权限!");

				reqSysLog.set(Syslog.column_status, "0");// 失败
				reqSysLog.set(Syslog.column_description, "没有权限!");
				reqSysLog.set(Syslog.column_cause, "0");// 没有权限

				log.info("返回失败提示页面!");
				toView(contro, ConstantAuth.auth_no_permissions, "权限验证失败，您没有操作权限");
				return;
			}
		}

		log.info("不需要权限验证、权限认证成功!!!继续处理请求...");

		log.info("是否需要表单重复提交验证!");
		if (operator.getStr(Operator.column_formtoken).equals("1")) {
			String tokenRequest = ToolWeb.getParam(request, ConstantWebContext.request_formToken);
			String tokenCookie = ToolWeb.getCookieValueByName(request, ConstantWebContext.cookie_token);
			if (null == tokenRequest || tokenRequest.equals("")) {
				log.info("tokenRequest为空，无需表单验证!");

			} else if (null == tokenCookie || tokenCookie.equals("") || !tokenCookie.equals(tokenRequest)) {
				log.info("tokenCookie为空，或者两个值不相等，把tokenRequest放入cookie!");
				ToolWeb.addCookie(response, "", "/", true, ConstantWebContext.cookie_token, tokenRequest, 0);

			} else if (tokenCookie.equals(tokenRequest)) {
				log.info("表单重复提交!");
				toView(contro, ConstantAuth.auth_form, "请不要重复提交表单");
				return;

			} else {
				log.error("表单重复提交验证异常!!!");
			}
		}

		log.info("权限认真成功更新日志对象属性!");
		reqSysLog.set(Syslog.column_status, "1");// 成功
		Date actionStartDate = ToolDateTime.getDate();// action开始时间
		reqSysLog.set(Syslog.column_actionstartdate, ToolDateTime.getSqlTimestamp(actionStartDate));
		reqSysLog.set(Syslog.column_actionstarttime, actionStartDate.getTime());

		try {
			invoc.invoke();
		} catch (Exception e) {
			String expMessage = e.getMessage();
			// 开发模式下的异常信息
			if(Boolean.parseBoolean(PropKit.get(ConstantInit.config_devMode))){
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				e.printStackTrace(new PrintWriter(buf, true));
				expMessage = buf.toString();
			}
			
			log.error("业务逻辑代码遇到异常时保存日志!");
			reqSysLog.set(Syslog.column_status, "0");// 失败
			reqSysLog.set(Syslog.column_description, expMessage);
			reqSysLog.set(Syslog.column_cause, "3");// 业务代码异常

			log.error("返回失败提示页面!Exception = " + e.getMessage());
			
//			if(e instanceof RuntimeException){
//				expMessage = "自定义异常描述11" + expMessage;
//			} else if(e instanceof RuntimeException){
//				expMessage = "自定义异常描述22" + expMessage;
//			}

			toView(contro, ConstantAuth.auth_exception, "业务逻辑代码遇到异常Exception = " + expMessage);
		} 
	}

	/**
	 * 提示信息展示页
	 * @param contro
	 * @param type
	 * @param msg
	 */
	private void toView(BaseController contro, String type, String msg) {
		if (type.equals(ConstantAuth.auth_no_login)) {// 未登录处理
			contro.redirect("/jf/platform/login");
			return;
		}

		contro.setAttr("msg", msg);
		
		String isAjax = contro.getRequest().getHeader("X-Requested-With");
		if(isAjax != null && isAjax.equalsIgnoreCase("XMLHttpRequest")){
			contro.render("/common/msgAjax.html"); // Ajax页面
		}else{
			contro.render("/common/msg.html"); // 完整html页面
		}
	}

	/**
	 * 判断用户是否拥有某个url的操作权限
	 * 
	 * @param userIds
	 * @param url
	 * @return
	 */
	public static boolean hasPrivilegeUrl(String userIds, String url) {
		// 基于缓存查询operator
		Operator operator = Operator.dao.cacheGet(url);
		if (null == operator) {
			log.error("URL缓存不存在：" + url);
			return false;
		}

		// 基于缓存查询user
		Object userObj = User.dao.cacheGet(userIds);
		if (null == userObj) {
			log.error("用户缓存不存在：" + userIds);
			return false;
		}
		User user = (User) userObj;

		// 权限验证对象
		String operatorIds = operator.getPKValue() + ",";
		String groupIds = user.getStr(User.column_groupids);
		String stationIds = user.getStr(User.column_stationids);

		// 根据分组查询权限
		if (null != groupIds) {
			String[] groupIdsArr = groupIds.split(",");
			for (String groupIdsTemp : groupIdsArr) {
				Group group = Group.dao.cacheGet(groupIdsTemp);
				String roleIdsStr = group.getStr(Group.column_roleids);
				if (null == roleIdsStr || roleIdsStr.equals("")) {
					continue;
				}
				String[] roleIdsArr = roleIdsStr.split(",");
				for (String roleIdsTemp : roleIdsArr) {
					Role role = Role.dao.cacheGet(roleIdsTemp);
					String operatorIdsStr = role.getStr(Role.column_operatorids);
					if (operatorIdsStr.indexOf(operatorIds) != -1) {
						return true;
					}
				}
			}
		}

		// 根据岗位查询权限
		if (null != stationIds) {
			String[] stationIdsArr = stationIds.split(",");
			for (String ids : stationIdsArr) {
				Station station = Station.dao.cacheGet(ids);
				String operatorIdsStr = station.getStr(Station.column_operatorids);
				if (null == operatorIdsStr || operatorIdsStr.equals("")) {
					continue;
				}
				if (operatorIdsStr.indexOf(operatorIds) != -1) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 获取当前登录用户
	 * @param request
	 * @param response
	 * @param userAgentVali 是否验证 User-Agent
	 * @return
	 */
	public static User getCurrentUser(HttpServletRequest request, HttpServletResponse response, boolean userAgentVali) {
		String loginCookie = ToolWeb.getCookieValueByName(request, ConstantWebContext.cookie_authmark);
		if (null != loginCookie && !loginCookie.equals("")) {
			// 1.解密认证数据
			String data = ToolIDEA.decrypt(loginCookie);
			if(null == data || data.isEmpty()){
				ToolWeb.addCookie(response, "", "/", true, ConstantWebContext.cookie_authmark, null, 0);
				return null;
			}
			String[] datas = data.split(".#.");	//arr[0]：时间戳，arr[1]：USERID，arr[2]：USER_IP， arr[3]：USER_AGENT
			
			// 2. 分解认证数据
			long loginDateTimes;
			String userIds = null;
			String ips = null;
			String userAgent = null;
			boolean autoLogin = false;
			try {
				loginDateTimes = Long.parseLong(datas[0]); // 时间戳
				userIds = datas[1]; // 用户id
				ips = datas[2]; // ip地址
				userAgent = datas[3]; // USER_AGENT
				autoLogin = Boolean.valueOf(datas[4]); // 是否自动登录
			} catch (Exception e) {
				ToolWeb.addCookie(response, "", "/", true, ConstantWebContext.cookie_authmark, null, 0);
				return null;
			}
			
			// 3.用户当前数据
			String newIp = ToolWeb.getIpAddr(request);
			String newUserAgent = request.getHeader("User-Agent");
			
			Date start = ToolDateTime.getDate();
			start.setTime(loginDateTimes); // 用户自动登录开始时间
			int day = ToolDateTime.getDateDaySpace(start, ToolDateTime.getDate()); // 已经登录多少天
			
			int maxAge = PropKit.getInt(ConstantInit.config_maxAge_key);
			
			// 4. 验证数据有效性
			if (ips.equals(newIp) && (userAgentVali ? userAgent.equals(newUserAgent) : true) && day <= maxAge) {
				// 如果不记住密码，单次登陆有效时间验证
				if(!autoLogin){
					int minute = ToolDateTime.getDateMinuteSpace(start, new Date());
					int session = PropKit.getInt(ConstantInit.config_session_key);
					if(minute > session){
						return null;
					}else{
						// 重新生成认证cookie，目的是更新时间戳
						long date = ToolDateTime.getDateByTime();
						StringBuilder token = new StringBuilder();// 时间戳.#.USERID.#.USER_IP.#.USER_AGENT.#.autoLogin
						token.append(date).append(".#.").append(userIds).append(".#.").append(ips).append(".#.").append(userAgent).append(".#.").append(autoLogin);
						String authmark = ToolIDEA.encrypt(token.toString());
						
						// 添加到Cookie
						int maxAgeTemp = -1; // 设置cookie有效时间
						ToolWeb.addCookie(response,  "", "/", true, ConstantWebContext.cookie_authmark, authmark, maxAgeTemp);
					}
				}
				
				// 返回用户数据
				Object userObj = User.dao.cacheGet(userIds);
				if (null != userObj) {
					User user = (User) userObj;
					return user;
				}
			}
		}

		return null;
	}

	/**
	 * 设置当前登录用户到cookie
	 * @param request
	 * @param response
	 * @param user
	 * @param autoLogin
	 */
	public static void setCurrentUser(HttpServletRequest request, HttpServletResponse response, User user, boolean autoLogin) {
		// 1.设置cookie有效时间
		int maxAgeTemp = -1;
		if (autoLogin) {
			maxAgeTemp = PropKit.getInt(ConstantInit.config_maxAge_key);
		}

		// 2.设置用户名到cookie
		ToolWeb.addCookie(response, "", "/", true, "userName", user.getStr("username"), maxAgeTemp);

		// 3.生成登陆认证cookie
		String userIds = user.getPKValue();
		String ips = ToolWeb.getIpAddr(request);
		String userAgent = request.getHeader("User-Agent");
		long date = ToolDateTime.getDateByTime();
		
		StringBuilder token = new StringBuilder();// 时间戳.#.USERID.#.USER_IP.#.USER_AGENT.#.autoLogin
		token.append(date).append(".#.").append(userIds).append(".#.").append(ips).append(".#.").append(userAgent).append(".#.").append(autoLogin);
		String authmark = ToolIDEA.encrypt(token.toString());
		
		// 4. 添加到Cookie
		ToolWeb.addCookie(response,  "", "/", true, ConstantWebContext.cookie_authmark, authmark, maxAgeTemp);
	}
	
	/**
	 * 设置验证码
	 * @param response
	 * @param authCode
	 */
	public static void setAuthCode(HttpServletResponse response, String authCode){
		// 1.生成验证码加密cookie
		String authCodeCookie = ToolIDEA.encrypt(authCode);
		
		// 2.设置登陆验证码cookie
		int maxAgeTemp = -1;
		ToolWeb.addCookie(response,  "", "/", true, ConstantWebContext.request_authCode, authCodeCookie, maxAgeTemp);
	}

	/**
	 * 获取验证码
	 * @param request
	 * @return
	 */
	public static String getAuthCode(HttpServletRequest request){
		// 1.获取cookie加密数据
		String authCode = ToolWeb.getCookieValueByName(request, ConstantWebContext.request_authCode);
		if (null != authCode && !authCode.equals("")) {
			// 2.解密数据
			authCode = ToolIDEA.decrypt(authCode);
			return authCode;
		}
		return null;
	}

}
