package com.platform.mvc.group;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupValidator extends Validator {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(GroupValidator.class);
	
	protected void validate(Controller controller) {
		String actionKey = getActionKey();
		if (actionKey.equals("/group/save")){
			
		} else if (actionKey.equals("/group/update")){
			
		}
	}
	
	protected void handleError(Controller controller) {
		controller.keepModel(Group.class);
		
		String actionKey = getActionKey();
		if (actionKey.equals("/group/save")){
			
		} else if (actionKey.equals("/group/update")){
			
		}
	}
}
