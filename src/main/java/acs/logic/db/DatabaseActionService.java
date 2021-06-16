package acs.logic.db;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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
import acs.logic.ElementService;
import acs.logic.util.ActionConverter;
import acs.logic.util.AwsS3;
import acs.logic.util.ElementConverter;
import acs.logic.util.QueueingTheory;
import acs.logic.util.UserConverter;
import acs.util.ActionId;
import acs.util.CreatedBy;
import acs.util.Element;
import acs.util.ElementId;
import acs.util.NewUserDetails;
import acs.util.UserId;
import acs.util.UserRole;

@Configuration
@EnableScheduling
@Service
public class DatabaseActionService implements ActionService {
	private int counter = 0;

	// finals
	private final String PARK = "park"; 	// Action #1 - "park"
	private final String UNPARK = "unpark"; // Action #2 - "unpark"
	private final String FIND = "find"; 	// Action #3 - "find"

	private String projectName;
	private ActionConverter actionConverter;
	private ActionDao actionDao;
	private UserConverter userConverter;
	private UserDao userDao;
	private ElementDao elementDao;
	private ElementConverter elementConverter;
	private ElementService elementService;
	private AwsS3 amazonAWS; // AWS - Amazon

	@Autowired
	public DatabaseActionService(ActionConverter actionConverter, ActionDao actionDao, UserConverter userConverter,
			UserDao userDao, ElementDao elementDao, ElementConverter elementConverter, ElementService elementService) {
		super();
		this.actionConverter = actionConverter;
		this.actionDao = actionDao;
		this.userConverter = userConverter;
		this.userDao = userDao;
		this.elementDao = elementDao;
		this.elementConverter = elementConverter;
		this.elementService = elementService;

		amazonAWS = new AwsS3(); // AWS - Amazon
	}

	@PostConstruct
	public void init() {
//		test();
//		quartzScheduler.scheduleJob(
//			    myJob, newTrigger().withIdentity("myJob", "group")
//			                       .withSchedule(cronSchedule("0 * * * * ?")).build());

//		Calendar calendar = Calendar.getInstance();
//		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//		scheduler.scheduleAtFixedRate(new MyTask(), millisToNextHour(calendar), 60*60*1000, TimeUnit.MILLISECONDS);

	}

	/*
	 * Fields by order: [second] [minute] [hour] [day of month] [month] [day(s) of
	 * week]
	 */
	@Scheduled(cron = "0 0 * * * ?")
	public void test() {
		counter++;
		System.err.println("Counter = " + counter);
	}

//	private static long millisToNextHour(Calendar calendar) {
//	    int minutes = calendar.get(Calendar.MINUTE);
//	    int seconds = calendar.get(Calendar.SECOND);
//	    int millis = calendar.get(Calendar.MILLISECOND);
//	    int minutesToNextHour = 60 - minutes;
//	    int secondsToNextHour = 60 - seconds;
//	    int millisToNextHour = 1000 - millis;
//	    return minutesToNextHour*60*1000 + secondsToNextHour*1000 + millisToNextHour;
//	}

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

		ElementEntity elementEntity = null;
		ElementBoundary elementBoundary = null;
		ElementBoundary newElementBoundary = null;
		QueueingTheory queueingTheory;
		
		List<ElementBoundary> allElements;
		String fileName;

		switch (action.getType()) {
		case PARK:
			// Checking if the user that invoke this action is a player: YES - continue, NO
			// - runtime Exception
			DatabaseUserService.checkRole(action.getInvokedBy().getUserId().getDomain(),
					action.getInvokedBy().getUserId().getEmail(), UserRole.ACTOR, userDao, userConverter);

			// Get Wq from the driver
			double Wq = (double) action.getActionAttributes().get("averageWaitingTime_q");

			// Get the exact element whom we want to perform PARK on
			elementEntity = elementDao
					.findById(this.elementConverter.convertToEntityId(action.getElement().getElementId().getDomain(),
							action.getElement().getElementId().getId()))
					.orElseThrow(() -> new ElementNotFoundException("** ERROR ** || could not find element"));

			fileName = getFileNameByElementName(elementEntity.getName());

			amazonAWS.writeDataToCsvFile(fileName, new String[] { "0", Wq + "", "0", "0" });

			amazonAWS.deleteFile(fileName);

			amazonAWS.uploadFile(fileName, fileName);
			
			this.amazonAWS.readCSVFileToOurMap(fileName);
						
			queueingTheory = new QueueingTheory(
					this.amazonAWS.getDataToSave().get("Lambda"),
					this.amazonAWS.getDataToSave().get("W"),
					this.amazonAWS.getDataToSave().get("Q"),
					this.amazonAWS.getDataToSave().get("Servers"));
			
			elementBoundary = this.elementConverter.fromEntity(elementEntity);
			
			newElementBoundary = this.elementService.createWithoutSaving(this.projectName, "avraham@gmail.com",elementBoundary,
					queueingTheory);
			
			// Update the element
			this.elementService.update(this.projectName, "avraham@gmail.com", elementBoundary.getElementId().getDomain(),
					elementBoundary.getElementId().getId(), newElementBoundary);

			return action;

		case UNPARK:

			// Checking if the user that invoke this action is a player: YES - continue, NO
			// - runtime Exception
			DatabaseUserService.checkRole(action.getInvokedBy().getUserId().getDomain(),
					action.getInvokedBy().getUserId().getEmail(), UserRole.ACTOR, userDao, userConverter);

			// Get W from the driver
			double W = (double) action.getActionAttributes().get("totalTimeInSystem");

			// Get the exact element whom we want to perform PARK on
			elementEntity = elementDao
					.findById(this.elementConverter.convertToEntityId(action.getElement().getElementId().getDomain(),
							action.getElement().getElementId().getId()))
					.orElseThrow(() -> new ElementNotFoundException("** ERROR ** || could not find element"));

			fileName = getFileNameByElementName(elementEntity.getName());

			amazonAWS.writeDataToCsvFile(fileName, new String[] { W + "", "0", "0", "0" });

			amazonAWS.deleteFile(fileName);

			amazonAWS.uploadFile(fileName, fileName);
			
			queueingTheory = new QueueingTheory(
					this.amazonAWS.getDataToSave().get("Lambda"),
					this.amazonAWS.getDataToSave().get("W"),
					this.amazonAWS.getDataToSave().get("Q"),
					this.amazonAWS.getDataToSave().get("Servers"));
			
			elementBoundary = this.elementConverter.fromEntity(elementEntity);
			
			newElementBoundary = this.elementService.createWithoutSaving(this.projectName, "avraham@gmail.com",elementBoundary,
					queueingTheory);
			
			// Update the element
			this.elementService.update(this.projectName, "avraham@gmail.com", elementBoundary.getElementId().getDomain(),
					elementBoundary.getElementId().getId(), newElementBoundary);

			return action;		

		case FIND:
			// Checking if the user that invoke this action is a actor: YES - continue, NO -
			// runtime Exception
			DatabaseUserService.checkRole(action.getInvokedBy().getUserId().getDomain(),
					action.getInvokedBy().getUserId().getEmail(), UserRole.ACTOR, userDao, userConverter);

			// Get all elements
			allElements = StreamSupport.stream(this.elementDao.findAll().spliterator(), false) // Stream<ElementEntity>
					.map(this.elementConverter::fromEntity) // Stream<ElementBoundary>
					.collect(Collectors.toList());

			return allElements;
//				index = -1;
//				// Find specific element that we need
//				for (int i = 0; i < allElements.size(); i++) {
//					if(isInside((double)(action.getActionAttributes().get("lat")),
//								(double)(action.getActionAttributes().get("lng")),
//								allElements.get(i))) {
//						index = i;
//						break;
//					}
//				}
//				
//				if(index>=0 && index <=8)
//					return allElements.get(index);
//				throw new RuntimeException("** ERROR ** || user's location is not valid (out of range)");

		default:
			return action;
		}
	}	

	private String getFileNameByElementName(String name) {
		if (name.equals("Ichilov Hospital")) {
			return "Ichilov_Hospital.csv";
		} else if (name.equals("Neve Tzedek")) {
			return "Neve_Tzedek.csv";
		} else if (name.equals("Frishman Beach")) {
			return "Frishman_Beach.csv";
		} else if (name.equals("Sharona Market")) {
			return "Sharona_Market.csv";
		} else if (name.equals("Afeka")) {
			return "Afeka.csv";
		} else if (name.equals("Allenby")) {
			return "Allenby.csv";
		} else if (name.equals("School Area")) {
			return "School.csv";
		} else if (name.equals("Food area")) {
			return "Food_area.csv";
		} else if (name.equals("Work")) {
			return "Work.csv";
		} else {
			return null;
		}
	}

	private boolean isInside(double lat, double lng, ElementBoundary elementBoundary) {
		return isBounded((double) elementBoundary.getElementAttributes().get("top"),
				(double) elementBoundary.getElementAttributes().get("left"),
				(double) elementBoundary.getElementAttributes().get("bottom"),
				(double) elementBoundary.getElementAttributes().get("right"), lat, lng);
	}

	/*
	 * top: north latitude of bounding box. left: left longitude of bounding box
	 * (western bound). bottom: south latitude of the bounding box. right: right
	 * longitude of bounding box (eastern bound). latitude: latitude of the point to
	 * check. longitude: longitude of the point to check.
	 */
	private boolean isBounded(double leftUpLat, double leftUpLng, double rightDownLat, double rightDownLng,
			double latitude, double longitude) {
		/* Check latitude bounds first. */
		if (leftUpLat >= latitude && latitude >= rightDownLat) {
			/*
			 * If your bounding box doesn't wrap the date line the value must be between the
			 * bounds. If your bounding box does wrap the date line it only needs to be
			 * higher than the left bound or lower than the right bound.
			 */

			if (leftUpLng <= longitude && longitude <= rightDownLng) {
				return true;
			} else {
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
