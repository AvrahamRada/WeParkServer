package acs.util;




public class Element {
	
	private ElementId elementId;
	
	public Element() {
		// TODO Auto-generated constructor stub
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
			throw new RuntimeException("elementId was not instantiate");
		}
		elementId.validation();
	}
	
}
