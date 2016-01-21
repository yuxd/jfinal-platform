package com.platform.mvc.operator;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperatorValidator extends Validator {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(OperatorValidator.class);
	
	protected void validate(Controller controller) {
		String actionKey = getActionKey();
		if (actionKey.equals("/operator/save")){
			
		} else if (actionKey.equals("/operator/update")){
			
		}
	}
	
	protected void handleError(Controller controller) {
		controller.keepModel(Operator.class);
		
		String actionKey = getActionKey();
		if (actionKey.equals("/operator/save")){
			
		} else if (actionKey.equals("/operator/update")){
			
		}
	}
}
