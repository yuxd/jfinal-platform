package com.platform.mvc.syslog;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import com.platform.constant.ConstantInit;
import com.platform.mvc.base.BaseService;
import com.platform.tools.ToolDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Date;

public class SysLogService extends BaseService {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(SysLogService.class);
	
	public static final SysLogService service = Enhancer.enhance(SysLogService.class);
	
	/**
	 * 定时清理数据
	 */
	public void timerDataClear(){
		Date date = ToolDateTime.getDate(-365, 0, 0, 0, 0); // 设置时间为365天前
		Timestamp timestamp = ToolDateTime.getSqlTimestamp(date);
		Db.use(ConstantInit.db_dataSource_main).update(getSql(Syslog.sqlId_clear), timestamp);
	}
	
}
