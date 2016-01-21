package com.platform.mvc.menu;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuValidator extends Validator {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(MenuValidator.class);
	
	protected void validate(Controller controller) {
		String actionKey = getActionKey();
		if (actionKey.equals("/menu/save")){
			
		} else if (actionKey.equals("/menu/update")){
			
		}
	}
	
	protected void handleError(Controller controller) {
		controller.keepModel(Menu.class);
		
		String actionKey = getActionKey();
		if (actionKey.equals("/menu/save")){
			
		} else if (actionKey.equals("/menu/update")){
			
		}
	}
}
