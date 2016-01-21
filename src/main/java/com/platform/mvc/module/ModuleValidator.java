package com.platform.mvc.module;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModuleValidator extends Validator {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ModuleValidator.class);
	
	protected void validate(Controller controller) {
		String actionKey = getActionKey();
		if (actionKey.equals("/module/save")){
			
		} else if (actionKey.equals("/module/update")){
			
		}
	}
	
	protected void handleError(Controller controller) {
		controller.keepModel(Module.class);
		
		String actionKey = getActionKey();
		if (actionKey.equals("/module/save")){
			
		} else if (actionKey.equals("/module/update")){
			
		}
	}
}
