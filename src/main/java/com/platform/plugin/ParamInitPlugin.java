package com.platform.plugin;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.IPlugin;
import com.platform.constant.ConstantInit;
import com.platform.constant.ConstantRender;
import com.platform.mvc.base.BaseService;
import com.platform.mvc.dict.Dict;
import com.platform.mvc.group.Group;
import com.platform.mvc.operator.Operator;
import com.platform.mvc.param.Param;
import com.platform.mvc.role.Role;
import com.platform.mvc.station.Station;
import com.platform.mvc.user.User;
import com.platform.mvc.user.UserInfo;
import com.platform.tools.ToolCache;
import com.platform.tools.ToolSqlXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统初始化缓存操作类
 * @author 董华健  2012-10-16 下午1:16:56
 */
public class ParamInitPlugin implements IPlugin {
	
	private static Logger log = LoggerFactory.getLogger(ParamInitPlugin.class);

	/**
	 * 数据批处理大小，每批次处理一万行
	 */
	protected static final int splitDataSize = 10000;
	
    /**
     * 用户缓存key前缀
     */
	public static String cacheStart_user = "user_";

    /**
     * 用户缓存key前缀
     */
	public static String cacheStart_userInfo = "userInfo_";

    /**
     * 分组缓存key前缀
     */
	public static String cacheStart_group = "group_";

    /**
     * 角色缓存key前缀
     */
	public static String cacheStart_role = "role_";

    /**
     * 岗位缓存key前缀
     */
	public static String cacheStart_station = "station_";
    
	/**
     * 功能缓存key前缀
     */
	public static String cacheStart_operator = "operator_";
    
	/**
     * 字典缓存key前缀
     */
	public static String cacheStart_dict = "dict_";
    
	/**
     * 字典子节点缓存key前缀
     */
	public static String cacheStart_dict_child =  "dict_child_";
    
	/**
     * 参数缓存key前缀
     */
	public static String cacheStart_param = "param_";
    
	/**
     * 参数子节点缓存key前缀
     */
	public static String cacheStart_param_child =  "param_child_";

	@Override
	public boolean start() {
		log.info("缓存参数初始化 start ...");

		// 1.缓存用户
		platform_cacheUser();
		platform_cacheUserInfo();
		
		// 2.缓存组
		platform_cacheGroup();

		// 3.缓存角色
		platform_cacheRole();

		// 4.缓存岗位
		platform_cacheStation();

		// 5.缓存功能
		platform_cacheOperator();

		// 6.缓存字典
		platform_cacheDict();

		// 6.缓存参数
		platform_cacheParam();

		log.info("缓存参数初始化 end ...");
		return true;
	}

	@Override
	public boolean stop() {
		return false;
	}

	/**
	 * 缓存所有用户
	 * @author 董华健    2012-10-16 下午1:16:48
	 */
	public static void platform_cacheUser() {
		log.info("缓存加载：User start");
		Map<String, Object> vars = new HashMap<String, Object>();
		String db_type = PropKit.get(ConstantInit.db_type_key);
		vars.put("db_type", db_type);
		String sql = ToolSqlXml.getSql(User.sqlId_paging, vars, ConstantRender.sql_renderType_beetl);
		
		int batchCount = BaseService.service.getBatchCount(ConstantInit.db_dataSource_main, " from pt_user ", splitDataSize);
		List<User> userList = null;
		for (int i = 0; i < batchCount; i++) {
			if(db_type.equals(ConstantInit.db_type_postgresql)){
				userList = User.dao.find(sql, splitDataSize, i * splitDataSize);
				
			}else if(db_type.equals(ConstantInit.db_type_mysql)){
				userList = User.dao.find(sql, splitDataSize, i * splitDataSize);
				
			}else if(db_type.equals(ConstantInit.db_type_oracle)){
				userList = User.dao.find(sql, i * splitDataSize + splitDataSize, i * splitDataSize);
			}
			
			for (User user : userList) {
				ToolCache.set(ParamInitPlugin.cacheStart_user + user.getPKValue(), user);
				ToolCache.set(ParamInitPlugin.cacheStart_user + user.getStr(User.column_username), user);
			}
			userList = null;
		}
		log.info("缓存加载：User end");
	}

	/**
	 * 缓存所有用户
	 * @author 董华健    2012-10-16 下午1:16:48
	 */
	public static void platform_cacheUserInfo() {
		log.info("缓存加载：UserInfo start");
		Map<String, Object> vars = new HashMap<String, Object>();
		String db_type = PropKit.get(ConstantInit.db_type_key);
		vars.put("db_type", db_type);
		String sql = ToolSqlXml.getSql(UserInfo.sqlId_paging, vars, ConstantRender.sql_renderType_beetl);
		
		int batchCount = BaseService.service.getBatchCount(ConstantInit.db_dataSource_main, " from pt_userInfo ", splitDataSize);
		List<UserInfo> userInfoList = null;
		for (int i = 0; i < batchCount; i++) {
			if(db_type.equals(ConstantInit.db_type_postgresql)){
				userInfoList = UserInfo.dao.find(sql, splitDataSize, i * splitDataSize);
				
			}else if(db_type.equals(ConstantInit.db_type_mysql)){
				userInfoList = UserInfo.dao.find(sql, splitDataSize, i * splitDataSize);
				
			}else if(db_type.equals(ConstantInit.db_type_oracle)){
				userInfoList = UserInfo.dao.find(sql, i * splitDataSize + splitDataSize, i * splitDataSize);
			}
			
			for (UserInfo userInfo : userInfoList) {
				ToolCache.set(ParamInitPlugin.cacheStart_user + userInfo.getStr(UserInfo.column_email), userInfo);
				ToolCache.set(ParamInitPlugin.cacheStart_user + userInfo.getStr(UserInfo.column_mobile), userInfo);
			}
			userInfoList = null;
		}
		log.info("缓存加载：UserInfo end");
	}

	/**
	 * 缓存所有组
	 * @author 董华健    2012-10-16 下午1:17:20
	 */
	public static void platform_cacheGroup() {
		log.info("缓存加载：Group start");
		Map<String, Object> vars = new HashMap<String, Object>();
		String db_type = PropKit.get(ConstantInit.db_type_key);
		vars.put("db_type", db_type);
		String sql = ToolSqlXml.getSql(Group.sqlId_paging, vars, ConstantRender.sql_renderType_beetl);
		
		int batchCount = BaseService.service.getBatchCount(ConstantInit.db_dataSource_main, " from pt_group ", splitDataSize);
		List<Group> groupList = null;
		for (int i = 0; i < batchCount; i++) {
			if(db_type.equals(ConstantInit.db_type_postgresql)){
				groupList = Group.dao.find(sql, splitDataSize, i * splitDataSize);
				
			}else if(db_type.equals(ConstantInit.db_type_mysql)){
				groupList = Group.dao.find(sql, splitDataSize, i * splitDataSize);
				
			}else if(db_type.equals(ConstantInit.db_type_oracle)){
				groupList = Group.dao.find(sql, i * splitDataSize + splitDataSize, i * splitDataSize);
			}
			
			for (Group group : groupList) {
				ToolCache.set(ParamInitPlugin.cacheStart_group + group.getPKValue(), group);
			}
			groupList = null;
		}
		
		log.info("缓存加载：Group end");
	}

	/**
	 * 缓存所有角色
	 * @author 董华健    2012-10-16 下午1:17:20
	 */
	public static void platform_cacheRole() {
		log.info("缓存加载：Role start");
		Map<String, Object> vars = new HashMap<String, Object>();
		String db_type = PropKit.get(ConstantInit.db_type_key);
		vars.put("db_type", db_type);
		String sql = ToolSqlXml.getSql(Role.sqlId_paging, vars, ConstantRender.sql_renderType_beetl);
		
		int batchCount = BaseService.service.getBatchCount(ConstantInit.db_dataSource_main, " from pt_role ", splitDataSize);
		List<Role> roleList = null;
		for (int i = 0; i < batchCount; i++) {
			if(db_type.equals(ConstantInit.db_type_postgresql)){
				roleList = Role.dao.find(sql, splitDataSize, i * splitDataSize);
				
			}else if(db_type.equals(ConstantInit.db_type_mysql)){
				roleList = Role.dao.find(sql, splitDataSize, i * splitDataSize);
				
			}else if(db_type.equals(ConstantInit.db_type_oracle)){
				roleList = Role.dao.find(sql, i * splitDataSize + splitDataSize, i * splitDataSize);
			}
			
			for (Role role : roleList) {
				ToolCache.set(ParamInitPlugin.cacheStart_role + role.getPKValue(), role);
			}
			roleList = null;
		}
		log.info("缓存加载：Role end");
	}
	
	/**
	 * 缓存所有的岗位
	 * @author 董华健    2013-07-16 下午1:17:20
	 */
	public static void platform_cacheStation() {
		log.info("缓存加载：Station start");
		Map<String, Object> vars = new HashMap<String, Object>();
		String db_type = PropKit.get(ConstantInit.db_type_key);
		vars.put("db_type", db_type);
		String sql = ToolSqlXml.getSql(Station.sqlId_paging, vars, ConstantRender.sql_renderType_beetl);
		
		int batchCount = BaseService.service.getBatchCount(ConstantInit.db_dataSource_main, " from pt_station ", splitDataSize);
		List<Station> stationList = null;
		for (int i = 0; i < batchCount; i++) {
			if(db_type.equals(ConstantInit.db_type_postgresql)){
				stationList = Station.dao.find(sql, splitDataSize, i * splitDataSize);
				
			}else if(db_type.equals(ConstantInit.db_type_mysql)){
				stationList = Station.dao.find(sql, splitDataSize, i * splitDataSize);
				
			}else if(db_type.equals(ConstantInit.db_type_oracle)){
				stationList = Station.dao.find(sql, i * splitDataSize + splitDataSize, i * splitDataSize);
			}
			
			for (Station station : stationList) {
				ToolCache.set(ParamInitPlugin.cacheStart_station + station.getPKValue(), station);
			}
			stationList = null;
		}
		
		log.info("缓存加载：Station end");
	}

	/**
	 * 缓存操作
	 * @author 董华健    2012-10-16 下午1:17:12
	 */
	public static void platform_cacheOperator() {
		log.info("缓存加载：Operator start");
		Map<String, Object> vars = new HashMap<String, Object>();
		String db_type = PropKit.get(ConstantInit.db_type_key);
		vars.put("db_type", db_type);
		String sql = ToolSqlXml.getSql(Operator.sqlId_paging, vars, ConstantRender.sql_renderType_beetl);
		
		int batchCount = BaseService.service.getBatchCount(ConstantInit.db_dataSource_main, " from pt_operator ", splitDataSize);
		List<Operator> operatorList = null;
		for (int i = 0; i < batchCount; i++) {
			if(db_type.equals(ConstantInit.db_type_postgresql)){
				operatorList = Operator.dao.find(sql, splitDataSize, i * splitDataSize);
				
			}else if(db_type.equals(ConstantInit.db_type_mysql)){
				operatorList = Operator.dao.find(sql, splitDataSize, i * splitDataSize);
				
			}else if(db_type.equals(ConstantInit.db_type_oracle)){
				operatorList = Operator.dao.find(sql, i * splitDataSize + splitDataSize, i * splitDataSize);
			}
			
			for (Operator operator : operatorList) {
				ToolCache.set(ParamInitPlugin.cacheStart_operator + operator.getPKValue(), operator);
				ToolCache.set(ParamInitPlugin.cacheStart_operator + operator.getStr(Operator.column_url), operator);
			}
			operatorList = null;
		}
		
		log.info("缓存加载：Operator end");
	}

	/**
	 * 缓存业务字典
	 * @author 董华健    2012-10-16 下午1:17:04
	 */
	public static void platform_cacheDict() {
		log.info("缓存加载：Dict start");
		String sql = ToolSqlXml.getSql(Dict.sqlId_all);
		List<Dict> dictList = Dict.dao.find(sql);
		for (Dict dict : dictList) {
			Dict.dao.cacheAdd(dict.getPKValue());
			dict = null;
		}
		log.info("缓存加载：Dict end, size = " + dictList.size());
		dictList = null;
	}

	/**
	 * 缓存业务参数
	 * @author 董华健    2012-10-16 下午1:17:04
	 */
	public static void platform_cacheParam() {
		log.info("缓存加载：Param start");
		String sql = ToolSqlXml.getSql(Param.sqlId_all);
		List<Param> paramList = Param.dao.find(sql);
		for (Param param : paramList) {
			Param.dao.cacheAdd(param.getPKValue());
			param = null;
		}
		log.info("缓存加载：Param end, size = " + paramList.size());
		paramList = null;
	}

}
