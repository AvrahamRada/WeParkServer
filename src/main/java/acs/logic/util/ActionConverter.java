package acs.logic.util;

import org.springframework.stereotype.Component;
import acs.boundaries.ActionBoundary;
import acs.data.ActionEntity;
import acs.util.InvokedBy;

@Component
public class ActionConverter extends Converter{
	
//	ActionEntity --> ActionBoundary
	public ActionBoundary fromEntity(ActionEntity entity) {
		ActionBoundary rv = new ActionBoundary();
		rv.setActionId(convertToActionId(entity.getActionId()));
		rv.setActionAttributes(entity.getActionAttributes());
		rv.setCreatedTimestamp(entity.getCreatedTimestamp());
		rv.setElement(convertToElement(entity.getElement()));
		rv.setInvokedBy(new InvokedBy(convertToUserId(entity.getInvokedBy())));
		rv.setType(entity.getType());
		return rv;
	}

//	ActionBoundary --> ActionEntity
	public ActionEntity toEntity(ActionBoundary boundary) {
		ActionEntity rv = new ActionEntity();
		rv.setActionId(convertToEntityId(boundary.getActionId().getDomain(), boundary.getActionId().getId()));
		rv.setActionAttributes(boundary.getActionAttributes());
		rv.setCreatedTimestamp(boundary.getCreatedTimestamp());
		rv.setElement(convertToEntityId(boundary.getElement().getElementId().getDomain(),boundary.getElement().getElementId().getId()));
		rv.setInvokedBy(convertToEntityId(boundary.getInvokedBy().getUserId().getDomain(),boundary.getInvokedBy().getUserId().getEmail()));
		rv.setType(boundary.getType());
		return rv;
	}		
}
