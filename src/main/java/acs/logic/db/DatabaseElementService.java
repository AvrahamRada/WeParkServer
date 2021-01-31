package acs.logic.db;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import acs.boundaries.ElementBoundary;
import acs.boundaries.ElementIdBoundary;
import acs.dal.ElementDao;
import acs.dal.UserDao;
import acs.data.ElementEntity;
import acs.data.UserEntity;
import acs.logic.ElementNotFoundException;
import acs.logic.ElementService;
import acs.logic.UserNotFoundException;
import acs.logic.util.ElementConverter;
import acs.logic.util.QueueingTheory;
import acs.logic.util.UserConverter;
import acs.util.CreatedBy;
import acs.util.ElementId;
import acs.util.Location;
import acs.util.UserId;
import acs.util.UserRole;
//import acs.dal.ElementDao;
//import acs.dal.UserDao;
//import acs.logic.util.ElementConverter;
//import acs.logic.util.UserConverter;
//import acs.logic.EnhancedElementService;
//import acs.data.UserRole;

@Service
public class DatabaseElementService implements ElementService {

	// finals
	private final double ARRIVAL_RATE = 60;
	private final double TOTAL_TIME_IN_SYSTEM = 200;
	private final double AVERAGE_WAITING_TIME_G = 20;
	private final int SERVERS = 216;

	private String projectName;
	private ElementConverter elementConverter;
	private UserConverter userConverter;
	private ElementDao elementDao;
	private UserDao userDao;
	private QueueingTheory queueingTheory;;

	@Autowired
	public DatabaseElementService(ElementConverter elementConverter, ElementDao elementDao, UserConverter userConverter,
			UserDao userDao) {

		super();
		this.elementConverter = elementConverter;
		this.elementDao = elementDao;
		this.userConverter = userConverter;
		this.userDao = userDao;
		this.queueingTheory = new QueueingTheory(ARRIVAL_RATE, TOTAL_TIME_IN_SYSTEM, AVERAGE_WAITING_TIME_G, SERVERS);
	}

	@PostConstruct
	public void init() {
	}

	// inject configuration value or inject default value
	@Value("${spring.application.name:demo}")
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectName() {
		return projectName;
	}

	@Override
	public ElementBoundary create(String managerDomain, String managerEmail, ElementBoundary elementBoundary) {
		DatabaseUserService.checkRole(managerDomain, managerEmail, UserRole.MANAGER, userDao, userConverter);
		// Validate that the important element boundary fields are not null;

		elementBoundary.validation();

		// Set the element's domain to the project name and create the unique id for the
		// element.
		elementBoundary.setElementId(new ElementId(getProjectName(), UUID.randomUUID().toString()));

		// Set the element's creation date.
		elementBoundary.setCreatedTimestamp(new Date(System.currentTimeMillis()));

		// Set element's manager details.
		elementBoundary.setCreatedBy(new CreatedBy(new UserId(managerDomain, managerEmail)));
		// Default values
		elementBoundary.getElementAttributes().put("arrivalRate", queueingTheory.getArrivalRate());
		elementBoundary.getElementAttributes().put("totalTimeInSystem", queueingTheory.getTotalTimeInSystem());
		elementBoundary.getElementAttributes().put("averageWaitingTime_q", queueingTheory.getAverageWaitingTime_q());
		elementBoundary.getElementAttributes().put("servers", queueingTheory.getServers());

		// Generated values
		elementBoundary.getElementAttributes().put("generalQuantity", this.queueingTheory.getGeneralQuantity());
		elementBoundary.getElementAttributes().put("averageQueueQuantity_q",
				this.queueingTheory.getAverageQueueQuantity_q());
		elementBoundary.getElementAttributes().put("serviceRate", this.queueingTheory.getServiceRate());
		elementBoundary.getElementAttributes().put("averageServiceDuration",
				this.queueingTheory.getAverageServiceDuration());
		elementBoundary.getElementAttributes().put("overload", this.queueingTheory.getOverload());
		elementBoundary.getElementAttributes().put("getServiceImmediately",
				this.queueingTheory.getGetServiceImmediately());
		elementBoundary.getElementAttributes().put("r", this.queueingTheory.getR());
		elementBoundary.getElementAttributes().put("w_t", this.queueingTheory.getW_t());

		// Convert the element boundary to element entity
		ElementEntity elementEntity = elementConverter.toEntity(elementBoundary);

		// Add to database
		this.elementDao.save(elementEntity);

		return elementBoundary;
	}

	@Override
	public ElementBoundary update(String managerDomain, String managerEmail, String elementDomain, String elementId,
			ElementBoundary update) {
		DatabaseUserService.checkRole(managerDomain, managerEmail, UserRole.MANAGER, userDao, userConverter);
		// Fetching the specific element from DB.
		ElementEntity foundedElement = this.elementDao
				.findById(this.elementConverter.convertToEntityId(elementDomain, elementId))
				.orElseThrow(() -> new ElementNotFoundException("could not find element"));

		// Convert the input to entity before update the values in element entity that
		// is in the DB.
		ElementEntity updateEntity = elementConverter.toEntity(update);

		// Update the element's values.
		updateElementValues(foundedElement, updateEntity);

		// save updated element to the database
		this.elementDao.save(foundedElement);

		// Convert the update entity to boundary and returns it.
		return elementConverter.fromEntity(foundedElement);
	}

	private void updateElementValues(ElementEntity toBeUpdatedEntity, ElementEntity inputEntity) {

		// Copy the important values from update entity to toBeUpdateEntity only if they
		// are not null
		toBeUpdatedEntity.setActive(inputEntity.getActive());
		toBeUpdatedEntity.setElementAttributes(inputEntity.getElementAttributes());
//		toBeUpdatedEntity.setLocation(inputEntity.getLocation());
		toBeUpdatedEntity.setName(inputEntity.getName());
		toBeUpdatedEntity.setType(inputEntity.getType());

	}

	@Override
	public ElementBoundary getSpecificElement(String userDomain, String userEmail, String elementDomain,
			String elementId) {
		ElementEntity foundedElement;
		UserEntity userEntity = DatabaseUserService
				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);

		foundedElement = getSpecificElementWithPermission(
				this.elementConverter.convertToEntityId(elementDomain, elementId), userEntity.getRole());

		return elementConverter.fromEntity(foundedElement);
	}

	private ElementEntity getSpecificElementWithPermission(String elementId, UserRole role) {
		if (role == UserRole.MANAGER) {
			// Fetching the specific element from DB.
			return findActiveOrInActiveElement(elementDao, elementId);
		} else if (role == UserRole.PLAYER) {
			return findActiveElement(elementDao, elementId);

		} else { // Role is ADMIN
			throw new UserNotFoundException("Not valid operation for ADMIN user");
		}

	}

	public static ElementEntity findActiveElement(ElementDao elementDao, String elementId) {
		List<ElementEntity> elements = elementDao.findOneByElementIdAndActive(elementId, true,
				PageRequest.of(0, 1, Direction.ASC, "elementId"));
		if (elements.size() == 0) {
			throw new ElementNotFoundException("could not find element");
		}
		return elements.get(0);
	}

//	public static ElementEntity findActiveElement(ElementDao elementDao, String elementId) {
//		List<ElementEntity> elements = elementDao.findOneByElementIdAndActive(elementId,true,
//				PageRequest.of(0, 1, Direction.ASC, "elementId"));
//		if(elements.size() == 0 ) {
//			throw new ElementNotFoundException("could not find element");
//		}
//		return elements.get(0);	
//	}

	public static ElementEntity findActiveOrInActiveElement(ElementDao elementDao, String elementId) {
		return elementDao.findById(elementId).orElseThrow(() -> new ElementNotFoundException("could not find element"));
	}

	@Override
	public List<ElementBoundary> getAll(String userDomain, String userEmail) {
		UserEntity userEntity = DatabaseUserService
				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);
		if (userEntity.getRole() == UserRole.MANAGER) {
			return StreamSupport.stream(this.elementDao.findAll().spliterator(), false) // Stream<ElementEntity>
					.map(this.elementConverter::fromEntity) // Stream<ElementBoundary>
					.collect(Collectors.toList());
			// For now MANAGER & MANAGER will get the same info without permissions
		} else if (userEntity.getRole() == UserRole.MANAGER) {
			return StreamSupport.stream(this.elementDao.findAll().spliterator(), false) // Stream<ElementEntity>
//					.filter(user -> user.getActive())
					.map(this.elementConverter::fromEntity) // Stream<ElementBoundary>
					.collect(Collectors.toList());
		} else { // Role is ADMIN
			throw new UserNotFoundException("Not valid operation for ADMIN user");
		}
//		return null;
	}

	@Override
	public void deleteAllElements(String adminDomain, String adminEmail) {
		DatabaseUserService.checkRole(adminDomain, adminEmail, UserRole.MANAGER, userDao, userConverter);
		// Clear all elements from DB.
		this.elementDao.deleteAll();
	}

//	@Override
//	public void bindParentElementToChildElement(String managerDomain, String managerEmail, String elementDomain,
//			String elementId, ElementIdBoundary input) {
//		// TODO Auto-generated method stub
//		
//	}

//	@Override
//	public Collection<ElementBoundary> getAllChildrenElements(String userDomain, String userEmail, String elementDomain,
//			String elementId, int size, int page) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public Collection<ElementBoundary> getAllOriginsElements(String userDomain, String userEmail, String elementDomain,
//			String elementId, int size, int page) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public List<ElementBoundary> getAll(String userDomain, String userEmail, int size, int page) {
		UserEntity userEntity = DatabaseUserService
				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);
		List<ElementEntity> entities;

		if (userEntity.getRole() == UserRole.MANAGER) {
			entities = this.elementDao.findAll(PageRequest.of(page, size, Direction.ASC, "elementId")).getContent();
		} else if (userEntity.getRole() == UserRole.PLAYER) {
			entities = this.elementDao.findAllByActive(true, PageRequest.of(page, size, Direction.ASC, "elementId"));
		} else {
			throw new UserNotFoundException("Not valid operation for ADMIN user");
		}

		return entities.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
	}

	@Override
	public List<ElementBoundary> getAllElementsByName(String userDomain, String userEmail, String name, int size,
			int page) {
		UserEntity userEntity = DatabaseUserService
				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);

		List<ElementEntity> entities;

		if (userEntity.getRole() == UserRole.MANAGER) {
			entities = this.elementDao.findAllByNameLike(name, PageRequest.of(page, size, Direction.ASC, "elementId"));
		} else if (userEntity.getRole() == UserRole.PLAYER) {
			entities = this.elementDao.findAllByNameLikeAndActive(name, true,
					PageRequest.of(page, size, Direction.ASC, "elementId"));
		} else {
			throw new UserNotFoundException("Not valid operation for ADMIN user");
		}

		return entities.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
	}

	@Override
	public List<ElementBoundary> getAllElementsByType(String userDomain, String userEmail, String type, int size,
			int page) {

		List<ElementEntity> entities;
		UserEntity userEntity = DatabaseUserService
				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);

		if (userEntity.getRole() == UserRole.MANAGER) {
			entities = this.elementDao.findAllByTypeLike(type, PageRequest.of(page, size, Direction.ASC, "elementId"));
		} else if (userEntity.getRole() == UserRole.PLAYER) {
			entities = this.elementDao.findAllByTypeLikeAndActive(type, true,
					PageRequest.of(page, size, Direction.ASC, "elementId"));
		} else {
			throw new UserNotFoundException("Not valid operation for ADMIN user");
		}

		return entities.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
	}

//	@Override
//	public List<ElementBoundary> getAllElementsByLocation(String userDomain, String userEmail, String lat, String lng,
//			String distance, int size, int page) {
//		
//		double distanceNum = Double.parseDouble(distance);
//		double latNum = Double.parseDouble(lat);
//		double lngNum = Double.parseDouble(lng);
//		
//		if(distanceNum < 0) {
//			throw new RuntimeException("Distance can not be negative.");
//		}
//
//		List<ElementEntity> entities;
//		Double minLat, maxLat, minLng, maxLng;
//		UserEntity userEntity = DatabaseUserService
//				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);
//
//		minLat = latNum - distanceNum;
//		maxLat = latNum + distanceNum;
//		minLng = lngNum - distanceNum;
//		maxLng = lngNum + distanceNum;
//
//		if (userEntity.getRole() == UserRole.MANAGER) {
//			entities = this.elementDao.findAllByLocation_LatBetweenAndLocation_LngBetween(minLat, maxLat, minLng,
//					maxLng, PageRequest.of(page, size, Direction.ASC, "elementId"));
//		} else if (userEntity.getRole() == UserRole.PLAYER) {
//			entities = this.elementDao.findAllByLocation_LatBetweenAndLocation_LngBetweenAndActive(minLat, maxLat,
//					minLng, maxLng, true, PageRequest.of(page, size, Direction.ASC, "elementId"));
//		} else {
//			throw new UserNotFoundException("Not valid operation for ADMIN user");
//		}
//
//		return entities.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
//	}

//	private String projectName;
//	private ElementConverter elementConverter;
//	private UserConverter userConverter;
//	private ElementDao elementDao;
//	private UserDao userDao;
//
//	@Autowired
//	public DatabaseElementService(ElementConverter elementConverter, ElementDao elementDao, UserConverter userConverter,
//			UserDao userDao) {
//
//		super();
//		this.elementConverter = elementConverter;
//		this.elementDao = elementDao;
//		this.userConverter = userConverter;
//		this.userDao = userDao;
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
//	public String getProjectName() {
//		return projectName;
//	}
//
//	@Override
//	@Transactional // (readOnly = false)
//	public ElementBoundary create(String managerDomain, String managerEmail, ElementBoundary elementBoundary) {
//
//		DatabaseUserService.checkRole(managerDomain, managerEmail, UserRole.MANAGER, userDao, userConverter);
//		// Validate that the important element boundary fields are not null;
//
//		elementBoundary.validation();
//
//		// Set the element's domain to the project name and create the unique id for the
//		// element.
//		elementBoundary.setElementId(new ElementId(getProjectName(), UUID.randomUUID().toString()));
//
//		// Set the element's creation date.
//		elementBoundary.setCreatedTimestamp(new Date(System.currentTimeMillis()));
//
//		// Set element's manager details.
//		elementBoundary.setCreatedBy(new CreatedBy(new UserId(managerDomain, managerEmail)));
//
//		// Convert the element boundary to element entity
//		ElementEntity elementEntity = elementConverter.toEntity(elementBoundary);
//
//		// Add to database
//		this.elementDao.save(elementEntity);
//
//		return elementBoundary;
//
//	}
//
//	@Override
//	@Transactional // (readOnly = false)
//	public ElementBoundary update(String managerDomain, String managerEmail, String elementDomain, String elementId,
//			ElementBoundary update) {
//
//		DatabaseUserService.checkRole(managerDomain, managerEmail, UserRole.MANAGER, userDao, userConverter);
//		// Fetching the specific element from DB.
//		ElementEntity foundedElement = this.elementDao
//				.findById(this.elementConverter.convertToEntityId(elementDomain, elementId))
//				.orElseThrow(() -> new ElementNotFoundException("could not find element"));
//
//		// Convert the input to entity before update the values in element entity that
//		// is in the DB.
//		ElementEntity updateEntity = elementConverter.toEntity(update);
//
//		// Update the element's values.
//		updateElementValues(foundedElement, updateEntity);
//
//		// save updated element to the database
//		this.elementDao.save(foundedElement);
//
//		// Convert the update entity to boundary and returns it.
//		return elementConverter.fromEntity(foundedElement);
//
//	}
//
//	// Old version
//	@Override
//	@Transactional(readOnly = true)
//	public List<ElementBoundary> getAll(String userDomain, String userEmail) {
////		UserEntity userEntity = DatabaseUserService
////				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);
////		if (userEntity.getRole() == UserRole.MANAGER) {
////		
////			return StreamSupport.stream(this.elementDao.findAll().spliterator(), false) // Stream<ElementEntity>
////					.map(this.elementConverter::fromEntity) // Stream<ElementBoundary>
////					.collect(Collectors.toList());
////		} else if (userEntity.getRole() == UserRole.PLAYER) {
////			
////			return StreamSupport.stream(this.elementDao.findAll().spliterator(), false) // Stream<ElementEntity>
////					.filter(user -> user.getActive()).map(this.elementConverter::fromEntity) // Stream<ElementBoundary>
////					.collect(Collectors.toList());
////		} else { // Role is ADMIN
////			
////			throw new UserNotFoundException("Not valid operation for ADMIN user");
////		}
//		return null;
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	public ElementBoundary getSpecificElement(String userDomain, String userEmail, String elementDomain,
//			String elementId) {
//		ElementEntity foundedElement;
//		UserEntity userEntity = DatabaseUserService
//				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);
//
//		foundedElement = getSpecificElementWithPermission(
//				this.elementConverter.convertToEntityId(elementDomain, elementId), userEntity.getRole());
//
//		return elementConverter.fromEntity(foundedElement);
//
//	}
//
//	
//	@Override
//	@Transactional // (readOnly = false)
//	public void deleteAllElements(String adminDomain, String adminEmail) {
//		DatabaseUserService.checkRole(adminDomain, adminEmail, UserRole.ADMIN, userDao, userConverter);
//		// Clear all elements from DB.
//		this.elementDao.deleteAll();
//
//	}
//
//	private void updateElementValues(ElementEntity toBeUpdatedEntity, ElementEntity inputEntity) {
//
//		// Copy the important values from update entity to toBeUpdateEntity only if they
//		// are not null
//		toBeUpdatedEntity.setActive(inputEntity.getActive());
//		toBeUpdatedEntity.setElementAttributes(inputEntity.getElementAttributes());
//		toBeUpdatedEntity.setLocation(inputEntity.getLocation());
//		toBeUpdatedEntity.setName(inputEntity.getName());
//		toBeUpdatedEntity.setType(inputEntity.getType());
//
//	}
//
//	@Override
//	@Transactional // (readOnly = false)
//	public void bindParentElementToChildElement(String managerDomain, String managerEmail, String elementDomain,
//			String elementId, ElementIdBoundary elementIdBoundary) {
//
//		DatabaseUserService.checkRole(managerDomain, managerEmail, UserRole.MANAGER, userDao, userConverter);
//
//		ElementEntity originElement = this.elementDao
//				.findById(this.elementConverter.convertToEntityId(elementDomain, elementId))
//				.orElseThrow(() -> new ElementNotFoundException("could not find origin by id: " + elementId));
//
//		ElementEntity childElement = this.elementDao
//				.findById(this.elementConverter.convertToEntityId(elementIdBoundary.getDomain(),
//						elementIdBoundary.getId()))
//				.orElseThrow(
//						() -> new ElementNotFoundException("could not find child by id: " + elementIdBoundary.getId()));
//
//		originElement.addChildElement(childElement);
//		this.elementDao.save(originElement);
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	public Collection<ElementBoundary> getAllChildrenElements(String userDomain, String userEmail, String elementDomain,
//			String elementId, int size, int page) {
//
//		UserEntity userEntity = DatabaseUserService
//				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);
//		Set<ElementEntity> entities;
//		ElementEntity originElement;
//		if (userEntity.getRole() == UserRole.MANAGER) {
//			originElement = findActiveOrInActiveElement(elementDao, this.elementConverter.convertToEntityId(elementDomain, elementId));
//			entities = elementDao.findAllByOrigin(originElement, PageRequest.of(page, size, Direction.ASC, "elementId")).stream().collect(Collectors.toSet());
//		} else if (userEntity.getRole() == UserRole.PLAYER) {
//			originElement = findActiveElement(elementDao, this.elementConverter.convertToEntityId(elementDomain, elementId));
//			entities = elementDao.findAllByOriginAndActive(originElement,true, PageRequest.of(page, size, Direction.ASC, "elementId")).stream().collect(Collectors.toSet());
//		} else {
//			throw new UserNotFoundException("Not valid operation for ADMIN user");
//		}
//		return entities.stream().map(elementConverter::fromEntity).collect(Collectors.toList());
//
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	public Collection<ElementBoundary> getAllOriginsElements(String userDomain, String userEmail, String elementDomain,
//			String elementId, int size, int page) {
//		ElementEntity reply;
//		UserEntity userEntity = DatabaseUserService
//				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);
//
//		reply = getSpecificElementWithPermission(this.elementConverter.convertToEntityId(elementDomain, elementId),
//				userEntity.getRole());
//
//		ElementEntity origin = reply.getOrigin();
//		Set<ElementBoundary> rv = new HashSet<>();
//
//		if (origin != null) {
//			if(userEntity.getRole() == UserRole.MANAGER) {
//				rv.add(this.elementConverter.fromEntity(origin));
//			}else if(userEntity.getRole() == UserRole.PLAYER) {
//				if(origin.getActive()) {
//					rv.add(this.elementConverter.fromEntity(origin));
//				}
//			}
//		}
//
//		return rv;
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	public List<ElementBoundary> getAll(String userDomain, String userEmail, int size, int page) {
//
//		UserEntity userEntity = DatabaseUserService
//				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);
//
//		List<ElementEntity> entities;
//
//		if (userEntity.getRole() == UserRole.MANAGER) {
//			entities = this.elementDao.findAll(PageRequest.of(page, size, Direction.ASC, "elementId")).getContent();
//		} else if (userEntity.getRole() == UserRole.PLAYER) {
//			entities = this.elementDao.findAllByActive(true, PageRequest.of(page, size, Direction.ASC, "elementId"));
//		} else {
//			throw new UserNotFoundException("Not valid operation for ADMIN user");
//		}
//
//		return entities.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
//
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	public List<ElementBoundary> getAllElementsByName(String userDomain, String userEmail, String name, int size,
//			int page) {
//
//		UserEntity userEntity = DatabaseUserService
//				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);
//
//		List<ElementEntity> entities;
//
//		if (userEntity.getRole() == UserRole.MANAGER) {
//			entities = this.elementDao.findAllByNameLike(name, PageRequest.of(page, size, Direction.ASC, "elementId"));
//		} else if (userEntity.getRole() == UserRole.PLAYER) {
//			entities = this.elementDao.findAllByNameLikeAndActive(name, true,
//					PageRequest.of(page, size, Direction.ASC, "elementId"));
//		} else {
//			throw new UserNotFoundException("Not valid operation for ADMIN user");
//		}
//
//		return entities.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
//
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	public List<ElementBoundary> getAllElementsByType(String userDomain, String userEmail, String type, int size,
//			int page) {
//
//		List<ElementEntity> entities;
//		UserEntity userEntity = DatabaseUserService
//				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);
//
//		if (userEntity.getRole() == UserRole.MANAGER) {
//			entities = this.elementDao.findAllByTypeLike(type, PageRequest.of(page, size, Direction.ASC, "elementId"));
//		} else if (userEntity.getRole() == UserRole.PLAYER) {
//			entities = this.elementDao.findAllByTypeLikeAndActive(type, true,
//					PageRequest.of(page, size, Direction.ASC, "elementId"));
//		} else {
//			throw new UserNotFoundException("Not valid operation for ADMIN user");
//		}
//
//		return entities.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
//
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	public List<ElementBoundary> getAllElementsByLocation(String userDomain, String userEmail, String lat, String lng,
//			String distance, int size, int page) {
//		
//		double distanceNum = Double.parseDouble(distance);
//		double latNum = Double.parseDouble(lat);
//		double lngNum = Double.parseDouble(lng);
//		
//		if(distanceNum < 0) {
//			throw new RuntimeException("Distance can not be negative.");
//		}
//
//		List<ElementEntity> entities;
//		Double minLat, maxLat, minLng, maxLng;
//		UserEntity userEntity = DatabaseUserService
//				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);
//
//		minLat = latNum - distanceNum;
//		maxLat = latNum + distanceNum;
//		minLng = lngNum - distanceNum;
//		maxLng = lngNum + distanceNum;
//
//		if (userEntity.getRole() == UserRole.MANAGER) {
//			entities = this.elementDao.findAllByLocation_LatBetweenAndLocation_LngBetween(minLat, maxLat, minLng,
//					maxLng, PageRequest.of(page, size, Direction.ASC, "elementId"));
//		} else if (userEntity.getRole() == UserRole.PLAYER) {
//			entities = this.elementDao.findAllByLocation_LatBetweenAndLocation_LngBetweenAndActive(minLat, maxLat,
//					minLng, maxLng, true, PageRequest.of(page, size, Direction.ASC, "elementId"));
//		} else {
//			throw new UserNotFoundException("Not valid operation for ADMIN user");
//		}
//
//		return entities.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
//
//	}
//
//
//	private ElementEntity getSpecificElementWithPermission(String elementId, UserRole role) {
//		if (role == UserRole.MANAGER) {
//			// Fetching the specific element from DB.
//			return findActiveOrInActiveElement(elementDao, elementId);
//		} else if (role == UserRole.PLAYER) {
//			return findActiveElement(elementDao, elementId);
//
//		} else { // Role is ADMIN
//			throw new UserNotFoundException("Not valid operation for ADMIN user");
//		}
//
//	}
//
//	
//	public static ElementEntity findActiveElement(ElementDao elementDao, String elementId) {
//		List<ElementEntity> elements = elementDao.findOneByElementIdAndActive(elementId,true,
//				PageRequest.of(0, 1, Direction.ASC, "elementId"));
//		if(elements.size() == 0 ) {
//			throw new ElementNotFoundException("could not find element");
//		}
//		return elements.get(0);	
//	}
//
//	public static ElementEntity findActiveOrInActiveElement(ElementDao elementDao, String elementId) {
//		return elementDao.findById(elementId).orElseThrow(() -> new ElementNotFoundException("could not find element"));
//	}
//
////	public static List<ElementBoundary> findActiveElements(Collection<ElementEntity> entities, ElementDao elementDao,
////			ElementConverter elementConverter) {
////		return entities.stream().filter(elementEntity -> elementEntity.getActive()).map(elementConverter::fromEntity)
////				.collect(Collectors.toList());
////	}
////
////	public static List<ElementBoundary> findActiveAndInActiveElements(Collection<ElementEntity> entities,
////			ElementDao elementDao, ElementConverter elementConverter) {
////		return entities.stream().map(elementConverter::fromEntity).collect(Collectors.toList());
////	}

}
