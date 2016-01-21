package com.platform.mvc.role;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleValidator extends Validator {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(RoleValidator.class);
	
	protected void validate(Controller controller) {
		String actionKey = getActionKey();
		if (actionKey.equals("/role/save")){
			
		} else if (actionKey.equals("/role/update")){
			
		}
	}
	
	protected void handleError(Controller controller) {
		controller.keepModel(Role.class);
		
		String actionKey = getActionKey();
		if (actionKey.equals("/role/save")){
			
		} else if (actionKey.equals("/role/update")){
			
		}
	}
}
