package acs.boundaries;

import java.util.Date;
import java.util.Map;

import javax.management.RuntimeErrorException;

import acs.util.CreatedBy;
import acs.util.ElementAttributes;
import acs.util.ElementId;
import acs.util.Location;

public class ElementBoundary {

	private ElementId elementId;
	private String type;
	private String name;
	private Boolean active;
	private Date createdTimestamp;
	private CreatedBy createdBy;
	private Map<String, Object> elementAttributes;

	public ElementBoundary() {
	}

	public ElementBoundary(ElementId elementId, String type, String name, Boolean active, Date createdTimestamp,
			CreatedBy createdBy, Map<String, Object> elementAttributes) {

		super();
		this.elementId = elementId;
		this.type = type;
		this.name = name;
		this.active = active;
		this.createdTimestamp = createdTimestamp;
		this.createdBy = createdBy;
		this.elementAttributes = elementAttributes;
	}

	public ElementId getElementId() {
		return elementId;
	}

	public void setElementId(ElementId elementId) {
		this.elementId = elementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public CreatedBy getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(CreatedBy createdBy) {
		this.createdBy = createdBy;
	}
	
	public Map<String, Object> getElementAttributes() {
		return elementAttributes;
	}

	public void setElementAttributes(Map<String, Object> elementAttributes) {
		this.elementAttributes = elementAttributes;
	}

	public void validation() {

		if (type == null) {
			throw new RuntimeException("type was not instantiate");
		}

		if (name == null) {
			throw new RuntimeException("name was not instantiate");
		}
		if (active == null) {
			throw new RuntimeException("active was not instantiate");
		}
		
		if(elementAttributes == null) {
			throw new RuntimeException("elementAttributes was not instantiate");

		}

		for (Map.Entry<String, Object> entry : elementAttributes.entrySet()) {
			if (entry.getValue() == null) {
				throw new RuntimeException("value in elementAtrributes was not instantiate");
			}
		}

	}	
}