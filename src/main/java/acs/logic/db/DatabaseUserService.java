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
import acs.data.UserEntity;
import acs.logic.UserService;
//import acs.dal.UserDao;
//import acs.data.UserRole;
//import acs.logic.EnhancedUserService;
//import acs.logic.util.UserConverter;

@Service
public class DatabaseUserService implements UserService {

	@Override
	public UserBoundary createUser(UserBoundary user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserBoundary login(String userDomain, String userEmail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAllUsers(String adminDomain, String adminEmail) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail, int size, int page) {
		// TODO Auto-generated method stub
		return null;
	}
	
//	private String projectName;
//	private UserConverter userConverter;
//	private UserDao userDao;
//
//	@Autowired
//	public DatabaseUserService(UserConverter userConverter, UserDao userDao) {
//		super();
//		this.userDao = userDao;
//		this.userConverter = userConverter;
//	}
//
//	@PostConstruct
//	public void init() {
//	}
//
//	// inject configuration value or inject default value
//	@Value("${spring.application.name:demo}")
//	public void setProjectName(String projectName) {
//		this.projectName = projectName;
//	}
//
//	@Override
//	@Transactional // (readOnly = false)
//	public UserBoundary createUser(UserBoundary user) {
//
//		user.validation();
//		user.getUserId().setDomain(projectName);
//
//		try {
//			getUserEntityFromDatabase(
//			userConverter.convertToEntityId(user.getUserId().getDomain(), user.getUserId().getEmail()),this.userDao);
//		} catch (RuntimeException re) {
//			UserEntity newUser = userConverter.toEntity(user);
//			this.userDao.save(newUser);
//			return user;
//		}
//		throw new RuntimeException("user is already exists in the system");
//	}
//
//	@Override
//	@Transactional (readOnly = true)
//	public UserBoundary login(String userDomain, String userEmail) {
//		// mockup reads data from list
//		UserEntity user = getUserEntityFromDatabase(userConverter.convertToEntityId(userDomain, userEmail),this.userDao);
//		return this.userConverter.fromEntity(user);
//	}
//
//	@Override
//	@Transactional // (readOnly = false)
//	public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {
//		UserEntity updateUser = getUserEntityFromDatabase(userConverter.convertToEntityId(userDomain, userEmail),this.userDao);
//		// ---Inside the setters there are null checks---
//		updateUser.setAvatar(update.getAvatar());
//		updateUser.setRole(update.getRole());
//		updateUser.setUsername(update.getUsername());
//		this.userDao.save(updateUser);
//		return this.userConverter.fromEntity(updateUser);
//	}
//
//	@Override
//	@Transactional (readOnly = true)
//	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {
//		checkRole(adminDomain, adminEmail,UserRole.ADMIN,this.userDao,this.userConverter);
//
//		return StreamSupport.stream(this.userDao.findAll().spliterator(), false) // Stream<UserEntity>
//				.map(this.userConverter::fromEntity) // Stream<UserBoundary>
//				.collect(Collectors.toList()); // List<UserBoundary>
//	}
//
//	@Override
//	@Transactional // (readOnly = false)
//	public void deleteAllUsers(String adminDomain, String adminEmail) {
//		checkRole(adminDomain, adminEmail,UserRole.ADMIN,this.userDao,this.userConverter);
//		this.userDao.deleteAll();
//	}
//
//	public static void checkRole(String domain, String email,UserRole role,UserDao userDao,UserConverter userConverter) {
//		UserEntity userEntity = getUserEntityFromDatabase(userConverter.convertToEntityId(domain, email),userDao);
//		if (userEntity.getRole() != role) {
//			throw new RuntimeException("User is not " + role);
//		}
//		
//
//	}
//
//	public static UserEntity getUserEntityFromDatabase(String userId,UserDao userDao) {
//		return userDao.findById(userId).
//				orElseThrow(() -> new RuntimeException("could not find user by userId"));
//		
//		/*
//		 * Optional<> rv = this.userDao
//		 * 						.findById(id);
//		 * if(rv.isPresent()){
//		 * 		return rv.get(); 
//		 * } else {
//		 * 		throw new RuntimeException("could not find user by userId");
//		 * }
//		 */
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail, int size, int page) {
//		checkRole(adminDomain, adminEmail,UserRole.ADMIN,this.userDao,this.userConverter);
//		
//		return this.userDao.findAll(PageRequest.of(page, size, Direction.ASC, "userId"))
//				.getContent().stream().map(this.userConverter::fromEntity).collect(Collectors.toList());
//	}
	
}
