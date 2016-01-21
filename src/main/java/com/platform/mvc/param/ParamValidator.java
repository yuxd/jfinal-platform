package com.platform.mvc.param;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParamValidator extends Validator {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ParamValidator.class);
	
	protected void validate(Controller controller) {
		String actionKey = getActionKey();
		if (actionKey.equals("/param/save")){
			
		} else if (actionKey.equals("/param/update")){
			
		}
	}
	
	protected void handleError(Controller controller) {
		controller.keepModel(Param.class);
		
		String actionKey = getActionKey();
		if (actionKey.equals("/param/save")){
			
		} else if (actionKey.equals("/param/update")){
			
		}
	}
}
