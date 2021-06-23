package acs.util;

public class CreatedBy {

	private UserId userId;

	public CreatedBy() {
	}

	public CreatedBy(UserId userId) {
		super();
		this.userId = userId;
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	public void validation() {

		if (userId == null) {
			throw new RuntimeException("\n** ERROR ** \nuserId is NULL.");
		}
		userId.validation();
	}
}
