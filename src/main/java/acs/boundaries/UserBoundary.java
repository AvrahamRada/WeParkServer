package acs.boundaries;

import acs.util.UserId;
import acs.util.UserRole;

public class UserBoundary {
	
	private UserId userId;
	private UserRole role;
	private String username;
	//private String avatar;
	
	public UserBoundary() {
	}

	public UserBoundary(UserId userId, UserRole role, String username/*, String avatar*/) {
		super();
		this.userId = userId;
		this.role = role;
		this.username = username;
//		this.avatar = avatar;
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

//	public String getAvatar() {
//		return avatar;
//	}
//
//	public void setAvatar(String avatar) {
//		this.avatar = avatar;
//	}
	
	public void validation(){
		
		if(role == null)
			throw new RuntimeException("role was not instantiate");
		if(username == null)
			throw new RuntimeException("userName was not instantiate");
//		if(avatar == null)
//			throw new RuntimeException("avatar was not instantiate");
//		if(avatar == "")
//			throw new RuntimeException("avatar can't be an empty string");
		if(userId == null) {
			throw new RuntimeException("userId was not instantiate");
		}
		userId.validation();
	}
	
	@Override
	public String toString() {
		return "UserBoundary [userId=" + this.userId + ", role=" + this.role + ", username=" + this.username + "]";
	}
}
