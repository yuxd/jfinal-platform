package com.platform.mvc.syslog;

import com.platform.constant.ConstantInit;
import com.platform.mvc.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统日志管理
 * @author 董华健
 */
//@Controller(controllerKey = "/jf/platform/sysLog")
public class SysLogController extends BaseController {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(SysLogController.class);
	
	/**
	 * 日志管理列表
	 */
	public void index() {
		defaultOrder(Syslog.column_startdate, "desc"); // 默认排序方式，指定列名和排序方式
		paging(ConstantInit.db_dataSource_main, splitPage, Syslog.sqlId_splitPage_select, Syslog.sqlId_splitPage_from);
		render("/platform/sysLog/list.html");
	}

	/**
	 * 查看日志
	 */
	public void view() {
		Syslog log =  Syslog.dao.findFirst(getSql(Syslog.sqlId_view), ids);
		setAttr("sysLog", log);
		render("/platform/sysLog/view.html");
	}

	/**
	 * 删除日志
	 */
	public void delete() {
		SysLogService.service.delete("pt_syslog", getPara() == null ? ids : getPara());
		redirect("/jf/platform/sysLog");
	}

}


