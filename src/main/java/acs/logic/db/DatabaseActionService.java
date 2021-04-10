package acs.logic.db;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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


@Service
public class DatabaseActionService implements ActionService {
	
	// finals
	private final String PARK = "park"; 													// Action #1 - "park"
	private final String UNPARK = "unpark";													// Action #2 - "unpark"
	private final String FIND = "find";														// Action #3 - "find"
	private final String IS_TAKEN = "isTaken";
	private final String CREATE_USER_MANAGER_BY_USERNAME = "createUserManagerByUsername";
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
		List<ElementBoundary> allElements;
		int index;

		switch (action.getType()) {
			case PARK:
				// Checking if the user that invoke this action is a player: YES - continue, NO - runtime Exception
				DatabaseUserService.checkRole(action.getInvokedBy().getUserId().getDomain(),
						action.getInvokedBy().getUserId().getEmail(), UserRole.PLAYER, userDao, userConverter);
				
				// Get the exact element whom we want to perform PARK on
				element = elementDao.findById(this.elementConverter.convertToEntityId(
						action.getElement().getElementId().getDomain(), action.getElement().getElementId().getId()))
						.orElseThrow(() -> new ElementNotFoundException("** ERROR ** || could not find element"));
				
				// Update number of servers
				element.getElementAttributes().put("servers",((int)(element.getElementAttributes().get("servers")) + 1));
				
				// update the element in the DB.
				elementDao.save(element);
				return action;
	
			case UNPARK:
				// Checking if the user that invoke this action is a player: YES - continue, NO - runtime Exception
				DatabaseUserService.checkRole(action.getInvokedBy().getUserId().getDomain(),
						action.getInvokedBy().getUserId().getEmail(), UserRole.PLAYER, userDao, userConverter);
				
				// Get the exact element whom we want to perform PARK on
				element = elementDao.findById(this.elementConverter.convertToEntityId(
						action.getElement().getElementId().getDomain(), action.getElement().getElementId().getId()))
						.orElseThrow(() -> new ElementNotFoundException("** ERROR ** || could not find element"));
				
				// Update number of servers
				element.getElementAttributes().put("servers",((int)(element.getElementAttributes().get("servers")) - 1));
				
				// update the element in the DB.
				elementDao.save(element);
				return action;			
				
			case FIND:
				// Checking if the user that invoke this action is a player: YES - continue, NO - runtime Exception
				DatabaseUserService.checkRole(action.getInvokedBy().getUserId().getDomain(),
						action.getInvokedBy().getUserId().getEmail(), UserRole.PLAYER, userDao, userConverter);
				
				// Get all elements
				allElements = StreamSupport.stream(this.elementDao.findAll().spliterator(), false) // Stream<ElementEntity>
																.map(this.elementConverter::fromEntity) // Stream<ElementBoundary>
																.collect(Collectors.toList());
				index = -1;
				// Find specific element that we need
				for (int i = 0; i < allElements.size(); i++) {
					if(isInside((double)(action.getActionAttributes().get("lat")),
								(double)(action.getActionAttributes().get("lng")),
								allElements.get(i))) {
						index = i;
						break;
					}
				}
				
				if(index>=0 && index <=8)
					return allElements.get(index);
				throw new RuntimeException("** ERROR ** || user's location is not valid (out of range)");
	
			default:
				return action;
		}
	}

	private boolean isInside(double lat, double lng, ElementBoundary elementBoundary) {
		return isBounded((double)elementBoundary.getElementAttributes().get("top"),
						(double)elementBoundary.getElementAttributes().get("left"),
						(double)elementBoundary.getElementAttributes().get("bottom"),
						(double)elementBoundary.getElementAttributes().get("right"),
						lat,lng);
	}
	
	/*
	* top: north latitude of bounding box.
	* left: left longitude of bounding box (western bound). 
	* bottom: south latitude of the bounding box.
	* right: right longitude of bounding box (eastern bound).
	* latitude: latitude of the point to check.
	* longitude: longitude of the point to check.
	*/
	private boolean isBounded(double leftUpLat, double leftUpLng, double rightDownLat, double rightDownLng, 
	                  double latitude, double longitude){
	        /* Check latitude bounds first. */
	        if(leftUpLat >= latitude && latitude >= rightDownLat){
	                /* If your bounding box doesn't wrap 
	                   the date line the value
	                   must be between the bounds.
	                   If your bounding box does wrap the 
	                   date line it only needs to be  
	                   higher than the left bound or 
	                   lower than the right bound. */
	        	
	        	 if(leftUpLng <= longitude && longitude <= rightDownLng) {
	        		 return true;
	        	 }
	        	 else {
	        		 return false;
	        	 }
//	            if(leftUpLng <= longitude && leftUpLng <= longitude && longitude <= rightDownLng){
//	                return true;
//	            } else if(leftUpLng > rightDownLng && (leftUpLng <= longitude || longitude <= rightDownLng)) {
//	                return true;
//	            }
	        }
	        return false;
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

}
