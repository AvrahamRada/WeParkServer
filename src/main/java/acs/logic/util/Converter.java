package acs.logic.util;

import acs.util.ActionId;
import acs.util.Element;
import acs.util.ElementId;
import acs.util.UserId;

public class Converter {

	protected final String DELIMITER = "#";

	public String convertToEntityId(String domain, String id) {
		return domain + DELIMITER + id;
	}

	public UserId convertToUserId(String userId) {
		String userIdIdSplit[] = userId.split(DELIMITER);
		return new UserId(userIdIdSplit[0], userIdIdSplit[1]);
	}

	public ElementId convertToElementId(String elementId) {
		String elementIdSplit[] = elementId.split(DELIMITER);
		return new ElementId(elementIdSplit[0], elementIdSplit[1]);
	}

	public ActionId convertToActionId(String actionId) {
		String actionIdSplit[] = actionId.split(DELIMITER);
		return new ActionId(actionIdSplit[0], actionIdSplit[1]);
	}

	public Element convertToElement(String elementId) {
		String elementIdSplit[] = elementId.split(DELIMITER);
		return new Element(new ElementId(elementIdSplit[0], elementIdSplit[1]));
	}

}
