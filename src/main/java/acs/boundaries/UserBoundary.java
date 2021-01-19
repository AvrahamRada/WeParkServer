package acs.boundaries;

import acs.util.UserId;
import acs.util.UserRole;

public class UserBoundary {
	
	private UserId userId;
	private UserRole role;
	private String username;
	private String licensePlate;
	
	public UserBoundary() {
	}

	public UserBoundary(UserId userId, UserRole role, String username, String licensePlate) {
		super();
		this.userId = userId;
		this.role = role;
		this.username = username;
		this.licensePlate = licensePlate;
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}
	
	public void validation(){
		
		if(role == null)
			throw new RuntimeException("role was not instantiate");
		if(username == null)
			throw new RuntimeException("userName was not instantiate");
		if(licensePlate == null)
			throw new RuntimeException("licensePlate was not instantiate");
		if(licensePlate == "")
			throw new RuntimeException("licensePlate can't be an empty string");
		if(userId == null) {
			throw new RuntimeException("userId was not instantiate");
		}
		userId.validation();
	}
}
