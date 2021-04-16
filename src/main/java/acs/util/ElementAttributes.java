package acs.util;

public class ElementAttributes {
	
	private Boolean isOccupied;
	
	public ElementAttributes() {
	}
	

	public ElementAttributes(Boolean isOccupied) {
		super();
		this.isOccupied = isOccupied;
	}


	public Boolean getIsOccupied() {
		return isOccupied;
	}

	public void setIsOccupied(Boolean isOccupied) {
		this.isOccupied = isOccupied;
	}
	
	public void validation() {
		
		if(isOccupied == null) {
			throw new RuntimeException("isOccupid was not instantiate");
		}
		
	}
	
}
