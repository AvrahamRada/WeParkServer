package acs.data;

import java.util.Date;
import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
//import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//import org.springframework.data.mongodb.core.mapping.Document;
//
//import acs.action.ActionAttributes;
//import acs.action.ActionId;
//import acs.action.InvokedBy;
import acs.util.Element;

//@Entity
//@Table(name="ACTIONS")
@Document(collection = "ACTIONS")
public class ActionEntity {
	
	@Id
	private String actionId; 						// ELEMENT_ID PK VARCHAR(255)
	private String type; 							// ELEMENT_ID PK VARCHAR(255)
	private String elementId ; 						// DOMAIN VARCHAR(255)
													// ID VARCHAR(255)
	private Date createdTimestamp; 					// CREATED_TIME_STAMP TIMESTAMP
	private String invokedBy; 						// DOMAIN VARCHAR(255)
													// EMAIL VARCHAR(255)
	private Map<String,Object> actionAttributes; 	// ACTION_ATTRIBUTES CLOB
	
	public ActionEntity() {
	}
	
	public ActionEntity(String actionId, String type, String elementId, Date createdTimestamp, String invokedBy,
			Map<String,Object> actionAttributes) {
		super();
		this.actionId = actionId;
		this.type = type;
		this.elementId = elementId;
		this.createdTimestamp = createdTimestamp;
		this.invokedBy = invokedBy;
		this.actionAttributes = actionAttributes;
	}

	@Id
	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	public String getElement() {
		return elementId;
	}

	public void setElement(String elementId) {
		this.elementId = elementId;
	}

//	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}


	public String getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(String invokedBy) {
		this.invokedBy = invokedBy;
	}

	//@Lob
	//@Convert(converter = acs.logic.util.MapToJsonConverter.class)
	public Map<String,Object> getActionAttributes() {
		return actionAttributes;
	}

	public void setActionAttributes(Map<String,Object> actionAttributes) {
		this.actionAttributes = actionAttributes;
	}
}
