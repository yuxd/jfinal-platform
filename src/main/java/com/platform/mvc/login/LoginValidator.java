package com.platform.mvc.login;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginValidator extends Validator {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(LoginValidator.class);
	
	protected void validate(Controller controller) {
		String actionKey = getActionKey();
		if (actionKey.equals("/login/vali")){
			validateString("username", 5, 16, "usernameMsg", "请输入用户名!");
			validateString("password", 6, 18, "passwordMsg", "请输入密码!");
		}
	}
	
	protected void handleError(Controller controller) {
		String actionKey = getActionKey();
		if (actionKey.equals("/login/vali")){
			controller.keepPara("username", "password");
			controller.render("/platform/login/login.html");
		}
	}
	
}
