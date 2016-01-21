package com.platform.mvc.station;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StationValidator extends Validator {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(StationValidator.class);
	
	protected void validate(Controller controller) {
		String actionKey = getActionKey();
		if (actionKey.equals("/station/save")){
			
		} else if (actionKey.equals("/station/update")){
			
		}
	}
	
	protected void handleError(Controller controller) {
		controller.keepModel(Station.class);
		
		String actionKey = getActionKey();
		if (actionKey.equals("/station/save")){
			
		} else if (actionKey.equals("/station/update")){
			
		}
	}
}
