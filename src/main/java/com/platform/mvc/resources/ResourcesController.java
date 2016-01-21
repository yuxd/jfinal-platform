package com.platform.mvc.resources;

import com.platform.mvc.base.BaseController;
import com.platform.plugin.ParamInitPlugin;
import com.platform.plugin.SqlXmlPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 资源监控
 * @author 董华健
 */
@SuppressWarnings("unused")
//@Controller(controllerKey = "/jf/platform/resources")
public class ResourcesController extends BaseController {
	
	private static Logger log = LoggerFactory.getLogger(ResourcesController.class);

	/**
	 * 首页
	 */
	public void index(){;
		setAttrs(ResourcesService.service.pv());
		setAttrs(ResourcesService.service.getResources());
		render("/platform/resources/index.html");
	}
	
	/**
	 * 刷新参数缓存，重新加载系统初始化参数
	 */
	public void refreshParamCache(){
		new ParamInitPlugin().start();
		redirect("/jf/platform/resources");
	}

	/**
	 * 刷新Sql缓存，重新加载Sql XML
	 */
	public void refreshSqlCache(){
		SqlXmlPlugin.init(false);
		redirect("/jf/platform/resources");
	}
	
}
