package com.platform.mvc.image;

import com.jfinal.render.Render;
import com.platform.beetl.render.MyCaptchaRender;
import com.platform.mvc.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 验证码
 * @author 董华健
 */
//@Controller(controllerKey = "/jf/platform/authImg")
public class AuthImgController extends BaseController {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(AuthImgController.class);
	
	public void index() {
		Render render = new MyCaptchaRender();
		render(render);
	}
	
}


