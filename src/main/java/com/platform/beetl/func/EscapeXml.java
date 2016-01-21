package com.platform.beetl.func;

import org.apache.commons.lang3.StringEscapeUtils;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 过滤xml文档函数
 * @author 董华健
 */
public class EscapeXml implements Function {

	private static Logger log = LoggerFactory.getLogger(EscapeXml.class);
	
	/**
	 * 过滤xml文档函数实现
	 */
	@Override
	public Object call(Object[] arg, Context context) {
		if(arg.length != 1 || null == arg[0] || !(arg[0] instanceof String)){
			return "";
		}
		String content = null;// 
		try {
			content = (String) arg[0];
		} catch (Exception e) {
			return "";
		}

		log.debug("EscapeXml，xml转换处理，content=" + content);
		
		return StringEscapeUtils.escapeXml11(content);
	}

}
