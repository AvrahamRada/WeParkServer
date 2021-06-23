package acs.logic.util;

import org.springframework.stereotype.Component;

import acs.boundaries.UserBoundary;
import acs.data.UserEntity;

@Component
public class UserConverter extends Converter {
	
	// UserEntity --> UserBoundary
	public UserBoundary fromEntity(UserEntity entity) {
		UserBoundary rv = new UserBoundary();
		rv.setUserId(convertToUserId(entity.getUserId()));
		rv.setLicensePlate(entity.getLicensePlate());
		rv.setUsername(entity.getUsername());
		rv.setRole(entity.getRole());
		return rv;
	}

	// UserBoundary --> UserEntity
	public UserEntity toEntity(UserBoundary boundary) {
		UserEntity rv = new UserEntity();
		rv.setUserId(convertToEntityId(boundary.getUserId().getDomain(), boundary.getUserId().getEmail()));
		rv.setLicensePlate(boundary.getLicensePlate());
		rv.setUsername(boundary.getUsername());
		rv.setRole(boundary.getRole());
		return rv;
	}
}
