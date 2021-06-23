package acs.logic.util;

import org.springframework.stereotype.Component;
import acs.boundaries.ElementBoundary;
import acs.data.ElementEntity;
import acs.util.CreatedBy;

@Component
public class ElementConverter extends Converter {

	// ElementEntity --> ElementBoundary
	public ElementBoundary fromEntity(ElementEntity entity) {
		ElementBoundary rv = new ElementBoundary();
		rv.setActive(entity.getActive());
		rv.setCreatedBy(new CreatedBy(convertToUserId(entity.getCreatedBy())));
		rv.setCreatedTimestamp(entity.getCreatedTimestamp());
		rv.setElementAttributes(entity.getElementAttributes());
		rv.setElementId(convertToElementId(entity.getElementId()));
		rv.setName(entity.getName());
		rv.setType(entity.getType());

		return rv;
	}

	// ElementBoundary --> ElementEntity
	public ElementEntity toEntity(ElementBoundary boundary) {
		ElementEntity rv = new ElementEntity();
		rv.setActive(boundary.getActive());
		rv.setCreatedBy(convertToEntityId(boundary.getCreatedBy().getUserId().getDomain(),
				boundary.getCreatedBy().getUserId().getEmail()));
		rv.setCreatedTimestamp(boundary.getCreatedTimestamp());
		rv.setElementAttributes(boundary.getElementAttributes());
		rv.setElementId(convertToEntityId(boundary.getElementId().getDomain(), boundary.getElementId().getId()));
		rv.setName(boundary.getName());
		rv.setType(boundary.getType());

		return rv;
	}
}
