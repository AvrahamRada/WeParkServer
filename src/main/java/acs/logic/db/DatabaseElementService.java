package acs.logic.db;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import acs.logic.util.AwsS3;
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
	private AwsS3 amazonAWS;	// AWS - Amazon
	private QueueingTheory queueingTheory;	// QueueingTheory
	private HashMap<String, Double> dataToSave;

	@Autowired
	public DatabaseElementService(ElementConverter elementConverter, ElementDao elementDao, UserConverter userConverter,
			UserDao userDao) {

		super();
		this.elementConverter = elementConverter;
		this.elementDao = elementDao;
		this.userConverter = userConverter;
		this.userDao = userDao;
		
		dataToSave = new HashMap<>();
		
		amazonAWS = new AwsS3();																			// AWS - Amazon
		this.amazonAWS.downloadCSV("Ichilov_Hospital.csv");													// Download from data-set
		this.amazonAWS.saveCSVToData("Ichilov_Hospital.csv");												// Save on the server
		this.dataToSave.put("Ichilov_Hospital" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.dataToSave.put("Ichilov_Hospital" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.dataToSave.put("Ichilov_Hospital" + "Wq", this.amazonAWS.getDataToSave().get("Wq"));
		this.dataToSave.put("Ichilov_Hospital" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));
		
		this.amazonAWS.downloadCSV("Neve_Tzedek_Neighborhood.csv");		// Download from data-set
		this.amazonAWS.saveCSVToData("Neve_Tzedek_Neighborhood.csv");	// Save on the server
		this.dataToSave.put("Neve_Tzedek_Neighborhood" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.dataToSave.put("Neve_Tzedek_Neighborhood" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.dataToSave.put("Neve_Tzedek_Neighborhood" + "Wq", this.amazonAWS.getDataToSave().get("Wq"));
		this.dataToSave.put("Neve_Tzedek_Neighborhood" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));
		
		this.amazonAWS.downloadCSV("Beach.csv");		// Download from data-set
		this.amazonAWS.saveCSVToData("Beach.csv");	// Save on the server
		this.dataToSave.put("Beach" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.dataToSave.put("Beach" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.dataToSave.put("Beach" + "Wq", this.amazonAWS.getDataToSave().get("Wq"));
		this.dataToSave.put("Beach" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));
		
		this.amazonAWS.downloadCSV("Dizengoff_Center_Shopping_Mall.csv");		// Download from data-set
		this.amazonAWS.saveCSVToData("Dizengoff_Center_Shopping_Mall.csv");	// Save on the server
		this.dataToSave.put("Dizengoff_Center_Shopping_Mall" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.dataToSave.put("Dizengoff_Center_Shopping_Mall" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.dataToSave.put("Dizengoff_Center_Shopping_Mall" + "Wq", this.amazonAWS.getDataToSave().get("Wq"));
		this.dataToSave.put("Dizengoff_Center_Shopping_Mall" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));
		
		this.amazonAWS.downloadCSV("Afeka.csv");		// Download from data-set
		this.amazonAWS.saveCSVToData("Afeka.csv");	// Save on the server
		this.dataToSave.put("Afeka" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.dataToSave.put("Afeka" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.dataToSave.put("Afeka" + "Wq", this.amazonAWS.getDataToSave().get("Wq"));
		this.dataToSave.put("Afeka" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));
		
		this.amazonAWS.downloadCSV("Allenby.csv");		// Download from data-set
		this.amazonAWS.saveCSVToData("Allenby.csv");	// Save on the server
		this.dataToSave.put("Allenby" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.dataToSave.put("Allenby" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.dataToSave.put("Allenby" + "Wq", this.amazonAWS.getDataToSave().get("Wq"));
		this.dataToSave.put("Allenby" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));
		
		this.amazonAWS.downloadCSV("School_area.csv");		// Download from data-set
		this.amazonAWS.saveCSVToData("School_area.csv");	// Save on the server
		this.dataToSave.put("School_area" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.dataToSave.put("School_area" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.dataToSave.put("School_area" + "Wq", this.amazonAWS.getDataToSave().get("Wq"));
		this.dataToSave.put("School_area" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));
		
		this.amazonAWS.downloadCSV("Food_area.csv");		// Download from data-set
		this.amazonAWS.saveCSVToData("Food_area.csv");	// Save on the server
		this.dataToSave.put("Food_area" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.dataToSave.put("Food_area" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.dataToSave.put("Food_area" + "Wq", this.amazonAWS.getDataToSave().get("Wq"));
		this.dataToSave.put("Food_area" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));
		
		this.amazonAWS.downloadCSV("Work_area.csv");		// Download from data-set
		this.amazonAWS.saveCSVToData("Work_area.csv");	// Save on the server
		this.dataToSave.put("Work_area" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.dataToSave.put("Work_area" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.dataToSave.put("Work_area" + "Wq", this.amazonAWS.getDataToSave().get("Wq"));
		this.dataToSave.put("Work_area" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));
		
	}

	@PostConstruct
	public void init() {
		
		// Ichilov_Hospital
		this.queueingTheory = new QueueingTheory(this.dataToSave.get("Ichilov_Hospital" + "Lambda"), 
				this.dataToSave.get("Ichilov_Hospital" + "W"), 
				this.dataToSave.get("Ichilov_Hospital" + "Wq"), 
				this.dataToSave.get("Ichilov_Hospital" + "Servers"));
		
		create(this.projectName, 
				"avraham@gmail.com", 
				new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString())
						, "squre"
						, "squre01",
						true,
						new Date(System.currentTimeMillis()),
						new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
						new HashMap<String, Object>() {{
							put("top",32.11795); 
							put("left",34.81906); 
							put("bottom",32.11614);
							put("right",34.82120);}}));

		
		// Neve_Tzedek_Neighborhood
		this.queueingTheory = new QueueingTheory(this.dataToSave.get("Neve_Tzedek_Neighborhood" + "Lambda"), 
				this.dataToSave.get("Neve_Tzedek_Neighborhood" + "W"), 
				this.dataToSave.get("Neve_Tzedek_Neighborhood" + "Wq"), 
				this.dataToSave.get("Neve_Tzedek_Neighborhood" + "Servers"));
		
		create(this.projectName, 
				"avraham@gmail.com", 
				new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString())
						, "squre"
						, "squre02",
						true,
						new Date(System.currentTimeMillis()),
						new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
						new HashMap<String, Object>() {{
							put("top",32.11795); 
							put("left",34.690); 
							put("bottom",32.11614);
							put("right",34.81906);}}));
		
		// Beach
		this.queueingTheory = new QueueingTheory(this.dataToSave.get("Beach" + "Lambda"), 
				this.dataToSave.get("Beach" + "W"), 
				this.dataToSave.get("Beach" + "Wq"), 
				this.dataToSave.get("Beach" + "Servers"));
		
		create(this.projectName, 
				"avraham@gmail.com", 
				new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString())
						, "squre"
						, "squre03",
						true,
						new Date(System.currentTimeMillis()),
						new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
						new HashMap<String, Object>() {{
							put("top",32.11795); 
							put("left",34.81474); 
							put("bottom",32.11614);
							put("right",34.8169);}}));
		
		// Dizengoff_Center_Shopping_Mall
		this.queueingTheory = new QueueingTheory(this.dataToSave.get("Dizengoff_Center_Shopping_Mall" + "Lambda"), 
				this.dataToSave.get("Dizengoff_Center_Shopping_Mall" + "W"), 
				this.dataToSave.get("Dizengoff_Center_Shopping_Mall" + "Wq"), 
				this.dataToSave.get("Dizengoff_Center_Shopping_Mall" + "Servers"));
		
		create(this.projectName, 
				"avraham@gmail.com", 
				new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString())
						, "squre"
						, "squre04",
						true,
						new Date(System.currentTimeMillis()),
						new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
						new HashMap<String, Object>() {{
							put("top",32.11614); 
							put("left",34.81906); 
							put("bottom",32.11433);
							put("right",34.8212);}}));
		
		// Afeka
		this.queueingTheory = new QueueingTheory(this.dataToSave.get("Afeka" + "Lambda"), 
				this.dataToSave.get("Afeka" + "W"), 
				this.dataToSave.get("Afeka" + "Wq"), 
				this.dataToSave.get("Afeka" + "Servers"));
		
		create(this.projectName, 
				"avraham@gmail.com", 
				new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString())
						, "squre"
						, "squre05",
						true,
						new Date(System.currentTimeMillis()),
						new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
						new HashMap<String, Object>() {{
							put("top",32.11614); 
							put("left",34.8169); 
							put("bottom",32.11433);
							put("right",34.81906);}}));
		
		// Allenby
		this.queueingTheory = new QueueingTheory(this.dataToSave.get("Allenby" + "Lambda"), 
				this.dataToSave.get("Allenby" + "W"), 
				this.dataToSave.get("Allenby" + "Wq"), 
				this.dataToSave.get("Allenby" + "Servers"));
		
		create(this.projectName, 
				"avraham@gmail.com", 
				new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString())
						, "squre"
						, "squre06",
						true,
						new Date(System.currentTimeMillis()),
						new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
						new HashMap<String, Object>() {{
							put("top",32.11614); 
							put("left",34.81474); 
							put("bottom",32.11433);
							put("right",34.8169);}}));
		
		// School_area
		this.queueingTheory = new QueueingTheory(this.dataToSave.get("School_area" + "Lambda"), 
				this.dataToSave.get("School_area" + "W"), 
				this.dataToSave.get("School_area" + "Wq"), 
				this.dataToSave.get("School_area" + "Servers"));
		
		create(this.projectName, 
				"avraham@gmail.com", 
				new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString())
						, "squre"
						, "squre07",
						true,
						new Date(System.currentTimeMillis()),
						new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
						new HashMap<String, Object>() {{
							put("top",32.11614); 
							put("left",34.81474); 
							put("bottom",32.11433);
							put("right",34.8169);}}));
		
		// Food_area
		this.queueingTheory = new QueueingTheory(this.dataToSave.get("Food_area" + "Lambda"), 
				this.dataToSave.get("Food_area" + "W"), 
				this.dataToSave.get("Food_area" + "Wq"), 
				this.dataToSave.get("Food_area" + "Servers"));
		
		create(this.projectName, 
				"avraham@gmail.com", 
				new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString())
						, "squre"
						, "squre08",
						true,
						new Date(System.currentTimeMillis()),
						new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
						new HashMap<String, Object>() {{
							put("top",32.11614); 
							put("left",34.81474); 
							put("bottom",32.11433);
							put("right",34.8169);}}));
		
		// Work_area
		this.queueingTheory = new QueueingTheory(this.dataToSave.get("Work_area" + "Lambda"), 
				this.dataToSave.get("Work_area" + "W"), 
				this.dataToSave.get("Work_area" + "Wq"), 
				this.dataToSave.get("Work_area" + "Servers"));
		
		create(this.projectName, 
				"avraham@gmail.com", 
				new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString())
						, "squre"
						, "squre09",
						true,
						new Date(System.currentTimeMillis()),
						new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
						new HashMap<String, Object>() {{
							put("top",32.11614); 
							put("left",34.81474); 
							put("bottom",32.11433);
							put("right",34.8169);}}));
		
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
