package acs.util;

public class Element {
	
	private ElementId elementId;
	
	public Element() {
	}

	public Element(ElementId elementId) {
		super();
		this.elementId = elementId;
	}

	public ElementId getElementId() {
		return elementId;
	}

	public void setElementId(ElementId elementId) {
		this.elementId = elementId;
	}

	public void validation() {
		if(this.elementId == null) {
			throw new RuntimeException("\n** ERROR ** \nelementId domain is NULL.");
		}
		elementId.validation();
	}
}
