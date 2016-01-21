package com.platform.mvc.ueditor;

import com.baidu.ueditor.ActionEnter;
import com.jfinal.kit.PathKit;
import com.platform.mvc.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Ueditor编辑器
 */
@SuppressWarnings("unused")
//@Controller(controllerKey = {"/jf/platform/ueditor"})
public class UeditorController extends BaseController {

	private static Logger log = LoggerFactory.getLogger(UeditorController.class);
	
	public void index() {
		String htmlText = new ActionEnter( getRequest(), PathKit.getWebRootPath() + File.separator ).exec();
		renderHtml(htmlText);
	}
	
}
