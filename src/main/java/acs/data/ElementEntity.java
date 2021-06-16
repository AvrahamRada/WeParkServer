package acs.data;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import acs.util.CreatedBy;

//@Entity
//@Table(name="ELEMENTS")
@Document(collection = "ELEMENTS")
public class ElementEntity {

	@Id
	private String elementId;						// ELEMENT_ID PK VARCHAR(255)
	private String type; 							// TYPE VARCHAR(255)
	private String name; 							// NAME VARCHAR(255)
	private Boolean active; 						// ACTIVE BOOLEAN
	private Date createdTimestamp; 					// CREATED_TIME_STAMP TIMESTAMP
	private String createdBy; 						// DOMAIN VARCHAR(255)
													// ID VARCHAR(255)
	private Map<String, Object> elementAttributes; 	// ELEMENT_ATTRIBUTES CLOB

	// add another entity collection related to this one using ONE-TO-MANY relationship
//	@DBRef(lazy = true)
//	private Set<ElementEntity> childrenElements;
		
	// add another entity related to this one using MANY-TO-ONE relationship
//	@DBRef(lazy = true)
//	private ElementEntity origin;
		

	public ElementEntity() {
	}

	public ElementEntity(String elementId, String type, String name, Boolean active, Date createdTimestamp,
			String createdBy, Map<String, Object> elementAttributes) {
		super();
		this.elementId = elementId;
		this.type = type;
		this.name = name;
		this.active = active;
		this.createdTimestamp = createdTimestamp;
		this.createdBy = createdBy;
		this.elementAttributes = elementAttributes;
	}
	
	@Id
	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type != null) {
			this.type = type;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null) {
			this.name = name;
		}
	}
	
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		if (active != null) {
			this.active = active;
		}
	}
	
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {

		this.createdBy = createdBy;

	}

	public Map<String, Object> getElementAttributes() {
		return elementAttributes;
	}

	public void setElementAttributes(Map<String, Object> elementAttributes) {
		if (elementAttributes != null) {
			for (Map.Entry<String, Object> entry : elementAttributes.entrySet()) {
				if (entry.getValue() == null) {
					elementAttributes.remove(entry.getKey());
				}
			}
			this.elementAttributes = elementAttributes;
		}
	}
}
