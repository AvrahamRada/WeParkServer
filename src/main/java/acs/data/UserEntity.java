package acs.data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import acs.util.UserId;
import acs.util.UserRole;

//@Entity
@Document(collection = "USERS")
public class UserEntity {	//USERS

	@Id
	private String userId; 			// USER_ID PK VARCHAR(255)
	private UserRole role; 			// ROLE VARCHAR(255) 
	private String username; 		// USERNAME VARCHAR(255)
	private String licensePlate; 	//AVATAR VARCHAR(255)

	public UserEntity() {
//		this.userId = new UserId();
	}

	public UserEntity(String userId, String role, String username, String licensePlate) {
		super();
		this.userId = userId;
		this.role = UserRole.valueOf(role);
		this.username = username;
		this.licensePlate = licensePlate;
	}

	@Id
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	//@Enumerated(EnumType.STRING)
	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		if (role != null) {
			this.role = role;
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if (username != null) {
			this.username = username;
		}
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		if (licensePlate != null && licensePlate != "") {
			this.licensePlate = licensePlate;
		}
	}
	
//	// i added toString method
//	@Override
//		public String toString() {
//			return "UserEntity [userId=" + this.userId + ", role=" + this.role + ", username=" + this.username + "]";
//				
//		}
}
