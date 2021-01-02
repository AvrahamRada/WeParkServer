package acs.util;

public class ActionAttributes {
	
	private String testAttribute;
	
	public ActionAttributes() {
	}

	public ActionAttributes(String testAttribute) {
		super();
		this.testAttribute = testAttribute;
	}

	
	public String getTestAttribute() {
		return testAttribute;
	}

	
	public void setTestAttribute(String testAttribute) {
		this.testAttribute = testAttribute;
	}
}
