package acs.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import acs.util.UserRole;

@Document(collection = "USERS")
public class UserEntity {

	@Id
	private String userId; // USER_ID PK VARCHAR(255)
	private UserRole role; // ROLE VARCHAR(255)
	private String username; // USERNAME VARCHAR(255)
	private String licensePlate; // AVATAR VARCHAR(255)

	public UserEntity() {
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
}
