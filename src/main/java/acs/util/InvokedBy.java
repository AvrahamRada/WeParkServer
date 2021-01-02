package acs.util;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

//@Embeddable
public class InvokedBy {
	
	private UserId userId;
	
	public InvokedBy() {
	}

	public InvokedBy(UserId userId) {
		super();
		this.userId = userId;
	}

//	@Embedded
	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	public void validation() {
		if(this.userId == null) {
			throw new RuntimeException("\n** ERROR ** \nuserId is NULL.");
		}
		this.userId.validation();
	}
}
