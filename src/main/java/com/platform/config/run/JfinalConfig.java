package com.platform.config.run;

import com.jfinal.config.*;
import com.jfinal.i18n.I18nInterceptor;
import com.jfinal.plugin.activerecord.tx.TxByActionKeys;
import com.jfinal.plugin.activerecord.tx.TxByMethods;
import com.jfinal.plugin.activerecord.tx.TxByRegex;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.platform.config.mapping.PlatformMapping;
import com.platform.config.routes.PlatformRoutes;
import com.platform.constant.ConstantCache;
import com.platform.constant.ConstantInit;
import com.platform.handler.GlobalHandler;
import com.platform.interceptor.AuthInterceptor;
import com.platform.interceptor.ParamPkgInterceptor;
import com.platform.plugin.*;
import com.platform.thread.DataClear;
import com.platform.thread.ThreadSysLog;
import com.platform.thread.TimerResources;
import com.platform.tools.ToolBeetl;
import com.platform.tools.ToolCache;
import com.platform.tools.ToolString;
import org.beetl.ext.jfinal.BeetlRenderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jfinal API 引导式配置
 */
public class JfinalConfig extends JFinalConfig {
	
	private static Logger log = LoggerFactory.getLogger(JfinalConfig.class);
	
	/**
	 * 配置常量
	 */
	public void configConstant(Constants constants) {
		log.info("configConstant 缓存 properties");
		loadPropertyFile("init.properties");

		log.info("configConstant 设置字符集");
		constants.setEncoding(ToolString.encoding); 

		log.info("configConstant 设置是否开发模式");
		constants.setDevMode(getPropertyToBoolean(ConstantInit.config_devMode, false));

		log.info("configConstant 视图Beetl设置");
		constants.setMainRenderFactory(new BeetlRenderFactory());
		ToolBeetl.regiseter();
		
		log.info("configConstant 视图error page设置");
		constants.setError404View("/common/404.html");
		constants.setError500View("/common/500.html");

		log.info("configConstant i18n文件前缀设置设置");
		constants.setI18nDefaultBaseName(getProperty(ConstantInit.config_i18n_filePrefix));
	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes routes) { 
		log.info("configRoute 注解注册路由");
		new ControllerPlugin(routes).start(); // 注解路由扫描
		
		log.info("configRoute 手动注册路由");
		routes.add(new PlatformRoutes());
	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins plugins) {
		log.info("注册paltform ActiveRecordPlugin");
		new PlatformMapping(plugins);
		
		log.info("I18NPlugin 国际化键值对加载");
		plugins.add(new I18NPlugin());
		
		if(ToolCache.getCacheType().equals(ConstantCache.cache_type_ehcache)){
			log.info("EhCachePlugin EhCache缓存");
			plugins.add(new EhCachePlugin());
			
		}else if(ToolCache.getCacheType().equals(ConstantCache.cache_type_redis)){
			log.info("RedisPlugin Redis缓存");
			String redisIp = getProperty(ConstantInit.config_redis_ip);
			Integer redisPort = getPropertyToInt(ConstantInit.config_redis_port);
			RedisPlugin systemRedis = new RedisPlugin(ConstantCache.cache_name_redis_system, redisIp, redisPort);
			plugins.add(systemRedis);
		}
		
		log.info("SqlXmlPlugin 解析并缓存 xml sql");
		plugins.add(new SqlXmlPlugin());
		
		log.info("afterJFinalStart 缓存参数");
		plugins.add(new ParamInitPlugin());
		
		log.info("afterJFinalStart 配置文件上传命名策略插件");
		plugins.add(new FileRenamePlugin());
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors interceptors) {
		//log.info("configInterceptor 支持使用session");
		//me.add(new SessionInViewInterceptor());
		
		log.info("configInterceptor 权限认证拦截器");
		interceptors.add(new AuthInterceptor());
		
		log.info("configInterceptor 参数封装拦截器");
		interceptors.add(new ParamPkgInterceptor());
		
		log.info("configInterceptor 配置开启事物规则");
		interceptors.add(new TxByMethods("save", "update", "delete"));
		interceptors.add(new TxByRegex(".*save.*"));
		interceptors.add(new TxByRegex(".*update.*"));
		interceptors.add(new TxByRegex(".*delete.*"));
		interceptors.add(new TxByActionKeys("/jf/wx/message", "/jf/wx/message/index"));

		log.info("configInterceptor i18n拦截器");
		interceptors.add(new I18nInterceptor());
	}
	
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers handlers) {
		log.info("configHandler 全局配置处理器，主要是记录日志和request域值处理");
		handlers.add(new GlobalHandler());
	}
	
	/**
	 * 系统启动完成后执行
	 */
	public void afterJFinalStart() {
		log.info("afterJFinalStart 启动操作日志入库线程");
		ThreadSysLog.startSaveDBThread();

		boolean luceneIndex = getPropertyToBoolean(ConstantInit.config_luceneIndex, false);
		if(luceneIndex){
			log.info("afterJFinalStart 创建自动回复lucene索引");
		}
		
		log.info("afterJFinalStart 系统负载");
		TimerResources.start();
		
		log.info("afterJFinalStart 数据清理");
		DataClear.start();
	}
	
	/**
	 * 系统关闭前调用
	 */
	public void beforeJFinalStop() {
		log.info("beforeJFinalStop 释放日志入库线程");
		ThreadSysLog.setThreadRun(false);

		log.info("beforeJFinalStop 释放系统负载抓取线程");
		TimerResources.stop();

		log.info("beforeJFinalStop 数据清理");
		DataClear.stop();
	}
}
