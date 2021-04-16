package acs.util;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;

public class UserId {

	private String domain; // DOMAIN VARCHAR(255)
	private String email;  // EMAIL VARCHAR(255)

	public UserId() {
	}

	public UserId(String domain, String email) {
		super();
		this.domain = domain;
		this.email = email;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		if (domain != null) {
			this.domain = domain;
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if(email != null) {
			this.email = email;
		}
	}

	public void validation() {

		if (this.domain == null) {
			throw new RuntimeException("\n** ERROR ** \nUserId.domain is NULL.\n");
		}
		
		if (this.email == null) {
			throw new RuntimeException("\n** ERROR ** \nUserId.email is NULL.\n");
		}
		
		if (!new EmailValidator().isValid(email, null) || email.isEmpty() || email.contains("#")) {
            throw new RuntimeException("\n** ERROR ** \nThe email is not in a valid format");
        }

	}

}
