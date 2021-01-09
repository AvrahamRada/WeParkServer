package acs.logic.db;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.dal.ActionDao;
import acs.dal.ElementDao;
import acs.dal.UserDao;
import acs.data.ActionEntity;
import acs.data.ElementEntity;
import acs.data.UserEntity;
import acs.logic.ActionService;
import acs.logic.ElementNotFoundException;
import acs.logic.util.ActionConverter;
import acs.logic.util.ElementConverter;
import acs.logic.util.UserConverter;
import acs.util.ActionId;
import acs.util.CreatedBy;
import acs.util.Element;
import acs.util.ElementId;
import acs.util.Location;
import acs.util.NewUserDetails;
import acs.util.UserId;
import acs.util.UserRole;

//import acs.logic.EnhancedActionService;
//import acs.logic.util.ActionConverter;
//import acs.logic.util.ElementConverter;
//import acs.logic.util.UserConverter;
//import acs.data.UserRole;
//import acs.dal.ActionDao;
//import acs.dal.ElementDao;
//import acs.dal.UserDao;
//import acs.action.ActionId;

@Service
public class DatabaseActionService implements ActionService {
	
	// finals
	private final String PARK = "park"; 													// Action #1 - "park"
	private final String UNPARK = "unpark";													// Action #2 - "unpark"
	private final String IS_TAKEN = "isTaken";
	private final String CREATE_USER_MANAGER_BY_USERNAME = "createUserManagerByUsername";	// Action #3 - "createUserManagerByUsername"
	private final String NEW_USER_DETAILS = "newUserDetails";
	private final String CITY = "city";
	private final String IS_USER_PARKED = "isUserParked";

	private String projectName;
	private ActionConverter actionConverter;
	private ActionDao actionDao;
	private UserConverter userConverter;
	private UserDao userDao;
	private ElementDao elementDao;
	private ElementConverter elementConverter;

	@Autowired
	public DatabaseActionService(ActionConverter actionConverter, ActionDao actionDao, UserConverter userConverter,
			UserDao userDao, ElementDao elementDao, ElementConverter elementConverter) {
		super();
		this.actionConverter = actionConverter;
		this.actionDao = actionDao;
		this.userConverter = userConverter;
		this.userDao = userDao;
		this.elementDao = elementDao;
		this.elementConverter = elementConverter;
	}

	@PostConstruct
	public void init() {

	}

	// inject configuration value or inject default value
	@Value("${spring.application.name:demo}")
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public Object invokeAction(ActionBoundary action) {
		action.validation(); // if one of the important value is null, it will throw an exception
		action.setActionId(new ActionId(this.projectName, UUID.randomUUID().toString()));
		action.setCreatedTimestamp(new Date(System.currentTimeMillis()));

		Object obj = operateAction(action);

		// Save action to DB
		actionDao.save(this.actionConverter.toEntity(action));

		return obj;
	}
	
	
	private Object operateAction(ActionBoundary action) {

		ElementEntity element = null;

		switch (action.getType()) {
			case PARK:
				// Checking if the user that invoke this action is a player: YES - continue, NO - runtime Exception
				DatabaseUserService.checkRole(action.getInvokedBy().getUserId().getDomain(),
						action.getInvokedBy().getUserId().getEmail(), UserRole.PLAYER, userDao, userConverter);
				
				// Find and get the element the we want to perform this Action about 
				element = DatabaseElementService.findActiveElement(elementDao, this.elementConverter.convertToEntityId(
						action.getElement().getElementId().getDomain(), action.getElement().getElementId().getId()));
				
				// update element attribute of isTaken to TRUE
				element.getElementAttributes().put(IS_TAKEN, true);
				// update the element in the DB.
				elementDao.save(element);
				return action;
	
			case UNPARK:
				// Checking if the user that invoke this action is a player: YES - continue, NO - runtime Exception
				DatabaseUserService.checkRole(action.getInvokedBy().getUserId().getDomain(),
						action.getInvokedBy().getUserId().getEmail(), UserRole.PLAYER, userDao, userConverter);
				
				// Find all actions that the same person that invoke this action, invoked, and of type: PARK, and get the last one sorted by createdTimestamp
				List<ActionEntity> lastParkAction = actionDao.findOneByInvokedByAndTypeLike(
						actionConverter.convertToEntityId(action.getInvokedBy().getUserId().getDomain(),
								action.getInvokedBy().getUserId().getEmail()),
						PARK, PageRequest.of(0, 1, Direction.DESC, "createdTimestamp"));
				if (lastParkAction.size() == 0) {
					throw new RuntimeException("Player did not parked yet.");
				}
				// Get the element(domain+id) of the player that already parked
				String elementId = lastParkAction.get(0).getElement();
				// Get the hole element( ElementEntity)
				element = elementDao.findById(elementId)
						.orElseThrow(() -> new ElementNotFoundException("could not find element"));
				// update element attribute of isTaken to TRUE
				element.getElementAttributes().put(IS_TAKEN, false);
				// update the element in the DB.
				elementDao.save(element);
				return action;
	
				// This case is performed when we create a City = create user MANAGER
			case CREATE_USER_MANAGER_BY_USERNAME:
				// Get NewUserDetails from actionAttributes inside action
				NewUserDetails newUserDetails = new NewUserDetails(
						action.getActionAttributes().get("email").toString(),
						UserRole.valueOf(action.getActionAttributes().get("role").toString()),
						action.getActionAttributes().get("username").toString(),
						action.getActionAttributes().get("avatar").toString());
				
				// Find all users by username or userId (Find the city in our DB)
				List<UserEntity> users = userDao.findAllByUsernameOrUserId(newUserDetails.getUsername(),
						this.userConverter.convertToEntityId(this.projectName, newUserDetails.getEmail()),
						PageRequest.of(0, 1, Direction.DESC, "username"));
				if (!users.isEmpty()) {
					throw new RuntimeException("Manager already exists.");
				}
				// Create UserBoundary
				UserBoundary userBoudary = new UserBoundary(new UserId(this.projectName, newUserDetails.getEmail()),
						newUserDetails.getRole(), newUserDetails.getUsername()/*, newUserDetails.getAvatar()*/);
				// Create UserEntity
				UserEntity newUser = userConverter.toEntity(userBoudary);
				// Create user in the DB
				this.userDao.save(newUser);
				// Create ElementBoundary
				ElementBoundary elementBoundary = new ElementBoundary(
						new ElementId(this.projectName, UUID.randomUUID().toString()), CITY, newUser.getUsername(), true,
						new Date(System.currentTimeMillis()),
						new CreatedBy(new UserId(userBoudary.getUserId().getDomain(), userBoudary.getUserId().getEmail())),
						new Location(99999.0, 99999.0), new HashMap<>());
				// Create ELEMENT city in the DB
				this.elementDao.save(this.elementConverter.toEntity(elementBoundary));
				action.setElement(new Element(elementBoundary.getElementId()));
				return userBoudary;
	
			case IS_USER_PARKED:
				// First of all check if you are PLAYER: YES - continue, NO - runtime exception
				DatabaseUserService.checkRole(action.getInvokedBy().getUserId().getDomain(),
						action.getInvokedBy().getUserId().getEmail(), UserRole.PLAYER, userDao, userConverter);
				
				// All actions that this UserId invoked & type = "PARK"
				List<ActionEntity> myActionPark = actionDao.findOneByInvokedByAndTypeLike(
						actionConverter.convertToEntityId(action.getInvokedBy().getUserId().getDomain(),
								action.getInvokedBy().getUserId().getEmail()),
						PARK, PageRequest.of(0, 1, Direction.DESC, "createdTimestamp"));
				
				// All actions that this UserId invoked & type = "UNPARK"
				List<ActionEntity> myActionUnpark = actionDao.findOneByInvokedByAndTypeLike(
						actionConverter.convertToEntityId(action.getInvokedBy().getUserId().getDomain(),
								action.getInvokedBy().getUserId().getEmail()),
						UNPARK, PageRequest.of(0, 1, Direction.DESC, "createdTimestamp"));
				
				// If no actions where found - PLAYER still didn't parked
				if (myActionPark.size() == 0 && myActionUnpark.size() == 0) {
					throw new RuntimeException("Player did not parked yet.");
				} else if (myActionPark.size() == 0) {
					return false;
				} else if (myActionUnpark.size() == 0) {
					return true;
				} else {
					if (myActionPark.get(0).getCreatedTimestamp()
							.compareTo(myActionUnpark.get(0).getCreatedTimestamp()) > 0) {
						return true;
					}
					return false;
				}
	
			default:
				return action;
		}
	}

	@Override
	public List<ActionBoundary> getAllActions(String adminDomain, String adminEmail) {
		DatabaseUserService.checkRole(adminDomain, adminEmail, UserRole.MANAGER, userDao, userConverter);

		return StreamSupport.stream(this.actionDao.findAll().spliterator(), false) // Stream<ElementEntity>
				.map(this.actionConverter::fromEntity) // Stream<ElementBoundary>
				.collect(Collectors.toList());
	}

	@Override
	public void deleteAllActions(String adminDomain, String adminEmail) {
		DatabaseUserService.checkRole(adminDomain, adminEmail, UserRole.MANAGER, userDao, userConverter);
		actionDao.deleteAll();
	}

	@Override
	public List<ActionBoundary> getAllActions(String adminDomain, String adminEmail, int size, int page) {
		DatabaseUserService.checkRole(adminDomain, adminEmail, UserRole.MANAGER, this.userDao, this.userConverter);
		return this.actionDao.findAll(PageRequest.of(page, size, Direction.ASC, "actionId")).getContent().stream()
				.map(this.actionConverter::fromEntity).collect(Collectors.toList());
	}

//	// finals
//	private final String PARK = "park";
//	private final String UNPARK = "unpark";
//	private final String IS_TAKEN = "isTaken";
//	private final String CREATE_USER_MANAGER_BY_USERNAME = "createUserManagerByUsername";
//	private final String NEW_USER_DETAILS = "newUserDetails";
//	private final String CITY = "city";
//	private final String IS_USER_PARKED = "isUserParked";
//
//	private String projectName;
//	private ActionConverter actionConverter;
//	private ActionDao actionDao;
//	private UserConverter userConverter;
//	private UserDao userDao;
//	private ElementDao elementDao;
//	private ElementConverter elementConverter;
//
//	@Autowired
//	public DatabaseActionService(ActionConverter actionConverter, ActionDao actionDao, UserConverter userConverter,
//			UserDao userDao, ElementDao elementDao, ElementConverter elementConverter) {
//		super();
//		this.actionConverter = actionConverter;
//		this.actionDao = actionDao;
//		this.userConverter = userConverter;
//		this.userDao = userDao;
//		this.elementDao = elementDao;
//		this.elementConverter = elementConverter;
//	}
//
//	@PostConstruct
//	public void init() {
//
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
//	public Object invokeAction(ActionBoundary action) {
//
//		action.validation(); // if one of the important value is null, it will throw an exception
//		action.setActionId(new ActionId(this.projectName, UUID.randomUUID().toString()));
//		action.setCreatedTimestamp(new Date(System.currentTimeMillis()));
//
//		Object obj = operateAction(action);
//
//		// Save action to DB
//		actionDao.save(this.actionConverter.toEntity(action));
//
//		return obj;
//	}
//
//	private Object operateAction(ActionBoundary action) {
//
//		ElementEntity element = null;
//
//		switch (action.getType()) {
//
//		case PARK:
//			DatabaseUserService.checkRole(action.getInvokedBy().getUserId().getDomain(),
//					action.getInvokedBy().getUserId().getEmail(), UserRole.PLAYER, userDao, userConverter);
//
//			element = DatabaseElementService.findActiveElement(elementDao, this.elementConverter.convertToEntityId(
//					action.getElement().getElementId().getDomain(), action.getElement().getElementId().getId()));
//
//			// update element attribute of isTaken to TRUE
//			element.getElementAttributes().put(IS_TAKEN, true);
//
//			// update the element in the DB.
//			elementDao.save(element);
//
//			return action;
//
//		case UNPARK:
//			DatabaseUserService.checkRole(action.getInvokedBy().getUserId().getDomain(),
//					action.getInvokedBy().getUserId().getEmail(), UserRole.PLAYER, userDao, userConverter);
//
//			List<ActionEntity> lastParkAction = actionDao.findOneByInvokedByAndTypeLike(
//					actionConverter.convertToEntityId(action.getInvokedBy().getUserId().getDomain(),
//							action.getInvokedBy().getUserId().getEmail()),
//					PARK, PageRequest.of(0, 1, Direction.DESC, "createdTimestamp"));
//
//			if (lastParkAction.size() == 0) {
//				throw new RuntimeException("Player did not parked yet.");
//			}
//			
//			String elementId = lastParkAction.get(0).getElement();
//			element = elementDao.findById(elementId)
//					.orElseThrow(() -> new ElementNotFoundException("could not find element"));
//
//			// update element attribute of isTaken to TRUE
//			element.getElementAttributes().put(IS_TAKEN, false);
//
//			// update the element in the DB.
//			elementDao.save(element);
//			return action;
//
//		case CREATE_USER_MANAGER_BY_USERNAME:
//			NewUserDetails newUserDetails = new NewUserDetails(action.getActionAttributes().get("email").toString(),
//					UserRole.valueOf(action.getActionAttributes().get("role").toString()),
//					action.getActionAttributes().get("username").toString(),
//					action.getActionAttributes().get("avatar").toString());
//
//			List<UserEntity> users = userDao.findAllByUsernameOrUserId(newUserDetails.getUsername(),
//					this.userConverter.convertToEntityId(this.projectName, newUserDetails.getEmail()),
//					PageRequest.of(0, 1, Direction.DESC, "username"));
//			if (!users.isEmpty()) {
//				throw new RuntimeException("Manager already exists.");
//			}
//
//			UserBoundary userBoudary = new UserBoundary(new UserId(this.projectName, newUserDetails.getEmail()),
//					newUserDetails.getRole(), newUserDetails.getUsername(), newUserDetails.getAvatar());
//
//			UserEntity newUser = userConverter.toEntity(userBoudary);
//			this.userDao.save(newUser);
//
//			ElementBoundary elementBoundary = new ElementBoundary(
//					new ElementId(this.projectName, UUID.randomUUID().toString()), CITY, newUser.getUsername(), true,
//					new Date(System.currentTimeMillis()),
//					new CreatedBy(new UserId(userBoudary.getUserId().getDomain(), userBoudary.getUserId().getEmail())),
//					new Location(99999.0, 99999.0), new HashMap<>());
//
//			this.elementDao.save(this.elementConverter.toEntity(elementBoundary));
//			action.setElement(new Element(elementBoundary.getElementId()));
//
//			return userBoudary;
//
//		case IS_USER_PARKED:
//			DatabaseUserService.checkRole(action.getInvokedBy().getUserId().getDomain(),
//					action.getInvokedBy().getUserId().getEmail(), UserRole.PLAYER, userDao, userConverter);
//
//			// Hopefully this query works
//			List<ActionEntity> myActionPark = actionDao.findOneByInvokedByAndTypeLike(
//					actionConverter.convertToEntityId(action.getInvokedBy().getUserId().getDomain(),
//							action.getInvokedBy().getUserId().getEmail()),
//					PARK, PageRequest.of(0, 1, Direction.DESC, "createdTimestamp"));
//
//			List<ActionEntity> myActionUnpark = actionDao.findOneByInvokedByAndTypeLike(
//					actionConverter.convertToEntityId(action.getInvokedBy().getUserId().getDomain(),
//							action.getInvokedBy().getUserId().getEmail()),
//					UNPARK, PageRequest.of(0, 1, Direction.DESC, "createdTimestamp"));
//
//			if (myActionPark.size() == 0 && myActionUnpark.size() == 0) {
//				throw new RuntimeException("Player did not parked yet.");
//			} else if (myActionPark.size() == 0) {
//				return false;
//			} else if (myActionUnpark.size() == 0) {
//				return true;
//			} else {
//				if (myActionPark.get(0).getCreatedTimestamp()
//						.compareTo(myActionUnpark.get(0).getCreatedTimestamp()) > 0) {
//					return true;
//				}
//				return false;
//			}
//
//		default:
//
//			return action;
//
//		}
//
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	public List<ActionBoundary> getAllActions(String adminDomain, String adminEmail) {
//
//		DatabaseUserService.checkRole(adminDomain, adminEmail, UserRole.ADMIN, userDao, userConverter);
//
//		return StreamSupport.stream(this.actionDao.findAll().spliterator(), false) // Stream<ElementEntity>
//				.map(this.actionConverter::fromEntity) // Stream<ElementBoundary>
//				.collect(Collectors.toList());
//	}
//
//	@Override
//	@Transactional // (readOnly = false)
//	public void deleteAllActions(String adminDomain, String adminEmail) {
//		DatabaseUserService.checkRole(adminDomain, adminEmail, UserRole.ADMIN, userDao, userConverter);
//		actionDao.deleteAll();
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	public List<ActionBoundary> getAllActions(String adminDomain, String adminEmail, int size, int page) {
//		DatabaseUserService.checkRole(adminDomain, adminEmail, UserRole.ADMIN, this.userDao, this.userConverter);
//		return this.actionDao.findAll(PageRequest.of(page, size, Direction.ASC, "actionId")).getContent().stream()
//				.map(this.actionConverter::fromEntity).collect(Collectors.toList());
//	}

}
