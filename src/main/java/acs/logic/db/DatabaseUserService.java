package acs.logic.db;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
//import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import acs.boundaries.UserBoundary;
import acs.dal.UserDao;
import acs.data.UserEntity;
import acs.logic.UserService;
//import acs.dal.UserDao;
//import acs.data.UserRole;
//import acs.logic.EnhancedUserService;
//import acs.logic.util.UserConverter;
import acs.logic.util.UserConverter;
import acs.util.UserRole;

@Service
public class DatabaseUserService implements UserService {
	private String projectName;
	private UserConverter userConverter;
	private UserDao userDao;
	
	@Autowired
	public DatabaseUserService(UserConverter userConverter, UserDao userDao) {
		super();
		this.userDao = userDao;
		this.userConverter = userConverter;
	}
	
	@PostConstruct
	public void init() {
	}

	// inject configuration value or inject default value
	@Value("${spring.application.name:defaulValue}")
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	@Override
	public UserBoundary createUser(UserBoundary user) {
		user.validation();
		user.getUserId().setDomain(projectName);
		
		try {
			getUserEntityFromDatabase(userConverter.convertToEntityId(user.getUserId().getDomain(), user.getUserId().getEmail()), 
									  this.userDao);
		} catch (RuntimeException re) { // If user is not exist we will create a new one.
			UserEntity newUser = userConverter.toEntity(user);
			this.userDao.save(newUser);
			return user;
		}
		throw new RuntimeException("User is already exists in the system");

//      ---------------------- Avraham ----------------------
//		UserEntity ue = getUserEntityFromDatabase(userConverter.convertToEntityId(user.getUserId().getDomain(), user.getUserId().getEmail()), 
//									  this.userDao);
//		if (ue == null) {
//			UserEntity newUser = userConverter.toEntity(user);
//			this.userDao.save(newUser);
//			return user;
//		}
//		else {
//			return userConverter.fromEntity(ue);
//		}
//		---------------------- END ----------------------
	}
	
	public static UserEntity getUserEntityFromDatabase(String userId,UserDao userDao) {
		
//		 If (Found)
//			we find user in DB we will return the user as UserEntity
//		 Else
//			throw RuntimeException
		
		return userDao.findById(userId).
				orElseThrow(() -> new RuntimeException("could not find user by userId"));	
		
		/*
		 * Optional<> rv = this.userDao
		 * 						.findById(id);
		 * if(rv.isPresent()){
		 * 		return rv.get(); 
		 * } else {
		 * 		throw new RuntimeException("could not find user by userId");
		 * }
		 */
		
//		UserEntity ue = userDao.findByUserId(userId);
//		return ue;	
	}

	@Override
	public UserBoundary login(String userDomain, String userEmail) {
//        ---------------------- Avraham ----------------------
//		UserEntity user = getUserEntityFromDatabase(userConverter.convertToEntityId(userDomain, userEmail),this.userDao);
//		if (user == null) {
//			System.out.println("user is not created yet! - Login Failed!");
//			return null;
//		}
//		else {
//			return this.userConverter.fromEntity(user);
//		} 
//		---------------------- END ----------------------
		UserEntity user = getUserEntityFromDatabase(userConverter.convertToEntityId(userDomain, userEmail),this.userDao);
		return this.userConverter.fromEntity(user);
	}

	@Override
	public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {
//      ---------------------- Avraham ----------------------
//		UserEntity updateUser = getUserEntityFromDatabase(userConverter.convertToEntityId(userDomain, userEmail),this.userDao);
//		// ---Inside the setters there are null checks---

//		if (updateUser == null) {
//			System.out.println("user is not created yet! - Login Failed!");
//			return null;
//		}
//		else {
//			updateUser.setRole(update.getRole());
//			updateUser.setUsername(update.getUsername());
//			this.userDao.save(updateUser);
//			return this.userConverter.fromEntity(updateUser);
//		} 
//		---------------------- END ----------------------
		
		
		UserEntity updateUser = getUserEntityFromDatabase(userConverter.convertToEntityId(userDomain, userEmail),this.userDao);
		// ---Inside the setters there are null checks---
//		updateUser.setAvatar(update.getAvatar());
		updateUser.setRole(update.getRole());
		updateUser.setUsername(update.getUsername());
		updateUser.setLicensePlate(update.getLicensePlate());
		this.userDao.save(updateUser);
		return this.userConverter.fromEntity(updateUser);

	}

	@Override
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {
		checkRole(adminDomain, adminEmail,UserRole.MANAGER,this.userDao,this.userConverter);

		return StreamSupport.stream(this.userDao.findAll().spliterator(), false) // Stream<UserEntity>
				.map(this.userConverter::fromEntity) // Stream<UserBoundary>
				.collect(Collectors.toList()); // List<UserBoundary>
	}
	
	public static void checkRole(String domain, String email,UserRole role,UserDao userDao,UserConverter userConverter) {
		UserEntity userEntity = getUserEntityFromDatabase(userConverter.convertToEntityId(domain, email),userDao);
		if (userEntity.getRole() != role) {
			System.out.println("User is not " + role);
			throw new RuntimeException("User is not " + role);
		}
	}	

	@Override
	public void deleteAllUsers(String adminDomain, String adminEmail) {
		checkRole(adminDomain, adminEmail,UserRole.MANAGER,this.userDao,this.userConverter);
		this.userDao.deleteAll();
		
	}

	@Override
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail, int size, int page) {
		checkRole(adminDomain, adminEmail,UserRole.MANAGER,this.userDao,this.userConverter);
		System.out.println("test");
		return StreamSupport.stream(this.userDao.findAll().spliterator(), false) // Stream<UserEntity>
				.map(this.userConverter::fromEntity) // Stream<UserBoundary>
				.collect(Collectors.toList()); // List<UserBoundary>
	}	
}
