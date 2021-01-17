package acs.util;

public class NewUserDetails {
	private String email;
	private UserRole role;
	private String username;
	private String licensePlate;
	
	public NewUserDetails() {
	}

	public NewUserDetails(String email, UserRole role, String username, String licensePlate) {
		super();
		this.email = email;
		this.role = role;
		this.username = username;
		this.licensePlate = licensePlate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public void setUsename(String username) {
		this.username = username;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}
	
}