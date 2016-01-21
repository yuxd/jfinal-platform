package com.platform.mvc.systems;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemsValidator extends Validator {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(SystemsValidator.class);
	
	protected void validate(Controller controller) {
		String actionKey = getActionKey();
		if (actionKey.equals("/systems/save")){
			
		} else if (actionKey.equals("/systems/update")){
			
		}
	}
	
	protected void handleError(Controller controller) {
		controller.keepModel(Systems.class);
		
		String actionKey = getActionKey();
		if (actionKey.equals("/systems/save")){
			
		} else if (actionKey.equals("/systems/update")){
			
		}
	}
	
}
