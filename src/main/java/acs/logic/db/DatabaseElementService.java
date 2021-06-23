package acs.logic.db;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
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
import acs.util.UserId;
import acs.util.UserRole;

@Service
public class DatabaseElementService implements ElementService {

	private static final int NUM_OF_ELEMENTS = 9;

	private static final String ICHILOV_ELEMENT = "Ichilov Hospital";
	private static final String NEVE_TZEDEK_ELEMENT = "Neve Tzedek";
	private static final String BEACH_ELEMENT = "Frishman Beach";
	private static final String SHARONA_MARKET_ELEMENT = "Sharona Market";
	private static final String AFEKA_ELEMENT = "Afeka";
	private static final String ALLENBY_ELEMENT = "Allenby";
	private static final String SCHOOL_ELEMENT = "School Area";
	private static final String FOOD_ELEMENT = "Food area";
	private static final String WORK_ELEMENT = "Work";

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

	private AwsS3 amazonAWS; // AWS - Amazon
	private HashMap<String, Double> awsDataMap; // Collection to save all information from AWS
	private List<ElementBoundary> elements;

	private QueueingTheory queueingTheoryAfekaElement;
	private QueueingTheory queueingTheoryAllenbyElement;
	private QueueingTheory queueingTheoryFoodAreaElement;
	private QueueingTheory queueingTheoryFrishmanBeachElement;
	private QueueingTheory queueingTheoryIchilovHospitalElement;
	private QueueingTheory queueingTheoryNeveTzedekElement;
	private QueueingTheory queueingTheorySchoolElement;
	private QueueingTheory queueingTheorySharonaMarketElement;
	private QueueingTheory queueingTheoryWorkElement;

	@Autowired
	public DatabaseElementService(ElementConverter elementConverter, ElementDao elementDao, UserConverter userConverter,
			UserDao userDao) {

		super();
		

		this.elementConverter = elementConverter;
		this.elementDao = elementDao;
		this.userConverter = userConverter;
		this.userDao = userDao;

		amazonAWS = new AwsS3(); // AWS - Amazon
		awsDataMap = new HashMap<>();

		deleteFilesFromServer(); // Delete files from server

		downloadFilesFromAmazonCloud(); // Download files to server


		readAllFilesToOurMap(); // read
		

	}

	private void deleteFilesFromServer() {
		try {
			Files.deleteIfExists(Paths.get("Ichilov_Hospital.csv"));
			Files.deleteIfExists(Paths.get("Neve_Tzedek.csv"));
			Files.deleteIfExists(Paths.get("Frishman_Beach.csv"));
			Files.deleteIfExists(Paths.get("Sharona_Market.csv"));
			Files.deleteIfExists(Paths.get("Afeka.csv"));
			Files.deleteIfExists(Paths.get("Allenby.csv"));
			Files.deleteIfExists(Paths.get("School.csv"));
			Files.deleteIfExists(Paths.get("Food_area.csv"));
			Files.deleteIfExists(Paths.get("Work.csv"));
		} catch (NoSuchFileException e) {
			System.out.println("** ERROR: No such file exists! **");
		} catch (DirectoryNotEmptyException e) {
			System.out.println("** ERROR: Directory isn't empty! **");
		} catch (IOException e) {
			System.out.println("** ERROR: Invalid permissions");
		}
		System.out.println("Deletion of all files successfully.");
	}

	private void downloadFilesFromAmazonCloud() {

		// Download from data-set
		this.amazonAWS.downloadCSV("Ichilov_Hospital.csv");
		this.amazonAWS.downloadCSV("Neve_Tzedek.csv");
		this.amazonAWS.downloadCSV("Frishman_Beach.csv");
		this.amazonAWS.downloadCSV("Sharona_Market.csv");
		this.amazonAWS.downloadCSV("Allenby.csv");
		this.amazonAWS.downloadCSV("School.csv");
		this.amazonAWS.downloadCSV("Food_area.csv");
		this.amazonAWS.downloadCSV("Work.csv");
		this.amazonAWS.downloadCSV("Afeka.csv");
	}

	private void readAllFilesToOurMap() {
		this.amazonAWS.readCSVFileToOurMap("Ichilov_Hospital.csv");

		this.awsDataMap.put("Ichilov_Hospital" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.awsDataMap.put("Ichilov_Hospital" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.awsDataMap.put("Ichilov_Hospital" + "Wq", this.amazonAWS.getDataToSave().get("Q"));
		this.awsDataMap.put("Ichilov_Hospital" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));
		


		this.amazonAWS.readCSVFileToOurMap("Neve_Tzedek.csv"); // Save on the server
		this.awsDataMap.put("Neve_Tzedek" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.awsDataMap.put("Neve_Tzedek" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.awsDataMap.put("Neve_Tzedek" + "Wq", this.amazonAWS.getDataToSave().get("Q"));
		this.awsDataMap.put("Neve_Tzedek" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));

		this.amazonAWS.readCSVFileToOurMap("Frishman_Beach.csv"); // Save on the server
		this.awsDataMap.put("Frishman_Beach" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.awsDataMap.put("Frishman_Beach" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.awsDataMap.put("Frishman_Beach" + "Wq", this.amazonAWS.getDataToSave().get("Q"));
		this.awsDataMap.put("Frishman_Beach" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));

		this.amazonAWS.readCSVFileToOurMap("Sharona_Market.csv"); // Save on the server
		this.awsDataMap.put("Sharona_Market" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.awsDataMap.put("Sharona_Market" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.awsDataMap.put("Sharona_Market" + "Wq", this.amazonAWS.getDataToSave().get("Q"));
		this.awsDataMap.put("Sharona_Market" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));

		this.amazonAWS.readCSVFileToOurMap("Afeka.csv"); // Save on the server
		this.awsDataMap.put("Afeka" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.awsDataMap.put("Afeka" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.awsDataMap.put("Afeka" + "Wq", this.amazonAWS.getDataToSave().get("Q"));
		this.awsDataMap.put("Afeka" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));

		this.amazonAWS.readCSVFileToOurMap("Allenby.csv"); // Save on the server
		this.awsDataMap.put("Allenby" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.awsDataMap.put("Allenby" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.awsDataMap.put("Allenby" + "Wq", this.amazonAWS.getDataToSave().get("Q"));
		this.awsDataMap.put("Allenby" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));

		this.amazonAWS.readCSVFileToOurMap("School.csv"); // Save on the server
		this.awsDataMap.put("School" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.awsDataMap.put("School" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.awsDataMap.put("School" + "Wq", this.amazonAWS.getDataToSave().get("Q"));
		this.awsDataMap.put("School" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));

		this.amazonAWS.readCSVFileToOurMap("Food_area.csv"); // Save on the server
		this.awsDataMap.put("Food_area" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.awsDataMap.put("Food_area" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.awsDataMap.put("Food_area" + "Wq", this.amazonAWS.getDataToSave().get("Q"));
		this.awsDataMap.put("Food_area" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));

		this.amazonAWS.readCSVFileToOurMap("Work.csv"); // Save on the server
		this.awsDataMap.put("Work" + "Lambda", this.amazonAWS.getDataToSave().get("Lambda"));
		this.awsDataMap.put("Work" + "W", this.amazonAWS.getDataToSave().get("W"));
		this.awsDataMap.put("Work" + "Wq", this.amazonAWS.getDataToSave().get("Q"));
		this.awsDataMap.put("Work" + "Servers", this.amazonAWS.getDataToSave().get("Servers"));

	}

	@PostConstruct
	public void init() {

		// Get all elements
		elements = StreamSupport.stream(this.elementDao.findAll().spliterator(), false) // Stream<ElementEntity>
				.map(this.elementConverter::fromEntity) // Stream<ElementBoundary>
				.collect(Collectors.toList());

		if (elements != null && elements.size() == NUM_OF_ELEMENTS) { // Checking if we have exactly 9 elements

			for (ElementBoundary elementBoundary : elements) { // if YES - we will UPDATE each element
				ElementBoundary tempElement;

				switch (elementBoundary.getName()) {

				case ICHILOV_ELEMENT:

					// Ichilov_Hospital
					this.queueingTheoryIchilovHospitalElement = new QueueingTheory(
							this.awsDataMap.get("Ichilov_Hospital" + "Lambda"),
							this.awsDataMap.get("Ichilov_Hospital" + "W"),
							this.awsDataMap.get("Ichilov_Hospital" + "Wq"),
							this.awsDataMap.get("Ichilov_Hospital" + "Servers"));

					// Create temporary element
					tempElement = createWithoutSaving(this.projectName, "avraham@gmail.com",
							new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
									"Ichilov Hospital", true, new Date(System.currentTimeMillis()),
									new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
									new HashMap<String, Object>() {
										{
											put("top", 32.11795);
											put("left", 34.81906);
											put("bottom", 32.11614);
											put("right", 34.82120);
										}
									}),
							this.queueingTheoryIchilovHospitalElement);

					// Update the element
					update(this.projectName, "avraham@gmail.com", elementBoundary.getElementId().getDomain(),
							elementBoundary.getElementId().getId(), tempElement);

					break;

				case NEVE_TZEDEK_ELEMENT:

					// Neve_Tzedek_Neighborhood
					this.queueingTheoryNeveTzedekElement = new QueueingTheory(
							this.awsDataMap.get("Neve_Tzedek" + "Lambda"), 
							this.awsDataMap.get("Neve_Tzedek" + "W"),
							this.awsDataMap.get("Neve_Tzedek" + "Wq"), 
							this.awsDataMap.get("Neve_Tzedek" + "Servers"));

					// Create temporary element
					tempElement = createWithoutSaving(this.projectName, "avraham@gmail.com",
							new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
									"Neve Tzedek", true, new Date(System.currentTimeMillis()),
									new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
									new HashMap<String, Object>() {
										{
											put("top", 32.11795);
											put("left", 34.690);
											put("bottom", 32.11614);
											put("right", 34.81906);
										}
									}),
							this.queueingTheoryNeveTzedekElement);
					
					// Update the element
					update(this.projectName, "avraham@gmail.com", elementBoundary.getElementId().getDomain(),
							elementBoundary.getElementId().getId(), tempElement);

					break;

				case BEACH_ELEMENT:

					// Frishman Beach
					this.queueingTheoryFrishmanBeachElement = new QueueingTheory(
							this.awsDataMap.get("Frishman_Beach" + "Lambda"),
							this.awsDataMap.get("Frishman_Beach" + "W"), 
							this.awsDataMap.get("Frishman_Beach" + "Wq"),
							this.awsDataMap.get("Frishman_Beach" + "Servers"));

					// Create temporary element
					tempElement = createWithoutSaving(this.projectName, "avraham@gmail.com",
							new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
									"Frishman Beach", true, new Date(System.currentTimeMillis()),
									new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
									new HashMap<String, Object>() {
										{
											put("top", 32.11795);
											put("left", 34.81474);
											put("bottom", 32.11614);
											put("right", 34.8169);
										}
									}),
							this.queueingTheoryFrishmanBeachElement);

					// Update the element
					update(this.projectName, "avraham@gmail.com", elementBoundary.getElementId().getDomain(),
							elementBoundary.getElementId().getId(), tempElement);

					break;

				case SHARONA_MARKET_ELEMENT:

					// Sharona_Market
					this.queueingTheorySharonaMarketElement = new QueueingTheory(
							this.awsDataMap.get("Sharona_Market" + "Lambda"),
							this.awsDataMap.get("Sharona_Market" + "W"), 
							this.awsDataMap.get("Sharona_Market" + "Wq"),
							this.awsDataMap.get("Sharona_Market" + "Servers"));

					// Create temporary element
					tempElement = createWithoutSaving(this.projectName, "avraham@gmail.com",
							new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
									"Sharona Market", true, new Date(System.currentTimeMillis()),
									new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
									new HashMap<String, Object>() {
										{
											put("top", 32.11614);
											put("left", 34.81906);
											put("bottom", 32.11433);
											put("right", 34.8212);
										}
									}),
							this.queueingTheorySharonaMarketElement);

					// Update the element
					update(this.projectName, "avraham@gmail.com", elementBoundary.getElementId().getDomain(),
							elementBoundary.getElementId().getId(), tempElement);

					break;

				case AFEKA_ELEMENT:

					this.queueingTheoryAfekaElement = new QueueingTheory(
							this.awsDataMap.get("Afeka" + "Lambda"),
							this.awsDataMap.get("Afeka" + "W"), 
							this.awsDataMap.get("Afeka" + "Wq"),
							this.awsDataMap.get("Afeka" + "Servers"));

					// Create temporary element
					tempElement = createWithoutSaving(this.projectName, "avraham@gmail.com",
							new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
									"Afeka", true, new Date(System.currentTimeMillis()),
									new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
									new HashMap<String, Object>() {
										{
											put("top", 32.11614);
											put("left", 34.8169);
											put("bottom", 32.11433);
											put("right", 34.81906);
										}
									}),
							this.queueingTheoryAfekaElement);

					// Update the element
					update(this.projectName, "avraham@gmail.com", elementBoundary.getElementId().getDomain(),
							elementBoundary.getElementId().getId(), tempElement);

					break;

				case ALLENBY_ELEMENT:

					this.queueingTheoryAllenbyElement = new QueueingTheory(
							this.awsDataMap.get("Allenby" + "Lambda"),
							this.awsDataMap.get("Allenby" + "W"), 
							this.awsDataMap.get("Allenby" + "Wq"),
							this.awsDataMap.get("Allenby" + "Servers"));

					// Create temporary element
					tempElement = createWithoutSaving(this.projectName, "avraham@gmail.com",
							new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
									"Allenby", true, new Date(System.currentTimeMillis()),
									new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
									new HashMap<String, Object>() {
										{
											put("top", 32.11614);
											put("left", 34.81474);
											put("bottom", 32.11433);
											put("right", 34.8169);
										}
									}),
							this.queueingTheoryAllenbyElement);

					// Update the element
					update(this.projectName, "avraham@gmail.com", elementBoundary.getElementId().getDomain(),
							elementBoundary.getElementId().getId(), tempElement);

					break;

				case SCHOOL_ELEMENT:

					this.queueingTheorySchoolElement = new QueueingTheory(
							this.awsDataMap.get("School" + "Lambda"),
							this.awsDataMap.get("School" + "W"), 
							this.awsDataMap.get("School" + "Wq"),
							this.awsDataMap.get("School" + "Servers"));

					// Create temporary element
					tempElement = createWithoutSaving(this.projectName, "avraham@gmail.com",
							new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
									"School Area", true, new Date(System.currentTimeMillis()),
									new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
									new HashMap<String, Object>() {
										{
											put("top", 32.11614);
											put("left", 34.81474);
											put("bottom", 32.11433);
											put("right", 34.8169);
										}
									}),
							this.queueingTheorySchoolElement);

					// Update the element
					update(this.projectName, "avraham@gmail.com", elementBoundary.getElementId().getDomain(),
							elementBoundary.getElementId().getId(), tempElement);

					break;

				case FOOD_ELEMENT:

					// Food_area
					this.queueingTheoryFoodAreaElement = new QueueingTheory(
							this.awsDataMap.get("Food_area" + "Lambda"),
							this.awsDataMap.get("Food_area" + "W"), 
							this.awsDataMap.get("Food_area" + "Wq"),
							this.awsDataMap.get("Food_area" + "Servers"));

					// Create temporary element
					tempElement = createWithoutSaving(this.projectName, "avraham@gmail.com",
							new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
									"Food area", true, new Date(System.currentTimeMillis()),
									new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
									new HashMap<String, Object>() {
										{
											put("top", 32.11614);
											put("left", 34.81474);
											put("bottom", 32.11433);
											put("right", 34.8169);
										}
									}),
							this.queueingTheoryFoodAreaElement);
					
					// Update the element
					update(this.projectName, "avraham@gmail.com", elementBoundary.getElementId().getDomain(),
							elementBoundary.getElementId().getId(), tempElement);

					break;

				case WORK_ELEMENT:

					// Work
					this.queueingTheoryWorkElement = new QueueingTheory(
							this.awsDataMap.get("Work" + "Lambda"),
							this.awsDataMap.get("Work" + "W"), 
							this.awsDataMap.get("Work" + "Wq"),
							this.awsDataMap.get("Work" + "Servers"));

					// Create temporary element
					tempElement = createWithoutSaving(this.projectName, "avraham@gmail.com",
							new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
									"Work", true, new Date(System.currentTimeMillis()),
									new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
									new HashMap<String, Object>() {
										{
											put("top", 32.11614);
											put("left", 34.81474);
											put("bottom", 32.11433);
											put("right", 34.8169);
										}
									}),
							this.queueingTheoryWorkElement);
					
					// Update the element
					update(this.projectName, "avraham@gmail.com", elementBoundary.getElementId().getDomain(),
							elementBoundary.getElementId().getId(), tempElement);

					break;

				default:
					break;
				}
			} // foreach
		} // if

		else {

			// First we will delete all elements
			deleteAllElements("WePark", "avraham@gmail.com");

			// Ichilov_Hospital
			this.queueingTheoryIchilovHospitalElement = new QueueingTheory(
					this.awsDataMap.get("Ichilov_Hospital" + "Lambda"), this.awsDataMap.get("Ichilov_Hospital" + "W"),
					this.awsDataMap.get("Ichilov_Hospital" + "Wq"),
					this.awsDataMap.get("Ichilov_Hospital" + "Servers"));

			create(this.projectName, "avraham@gmail.com",
					new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
							"Ichilov Hospital", true, new Date(System.currentTimeMillis()),
							new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
							new HashMap<String, Object>() {
								{
									put("top", 32.11795);
									put("left", 34.81906);
									put("bottom", 32.11614);
									put("right", 34.82120);
								}
							}),
					this.queueingTheoryIchilovHospitalElement);

			// Neve_Tzedek_Neighborhood
			this.queueingTheoryNeveTzedekElement = new QueueingTheory(this.awsDataMap.get("Neve_Tzedek" + "Lambda"),
					this.awsDataMap.get("Neve_Tzedek" + "W"), this.awsDataMap.get("Neve_Tzedek" + "Wq"),
					this.awsDataMap.get("Neve_Tzedek" + "Servers"));

			create(this.projectName, "avraham@gmail.com",
					new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
							"Neve Tzedek", true, new Date(System.currentTimeMillis()),
							new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
							new HashMap<String, Object>() {
								{
									put("top", 32.11795);
									put("left", 34.690);
									put("bottom", 32.11614);
									put("right", 34.81906);
								}
							}),
					this.queueingTheoryNeveTzedekElement);

			// Frishman Beach
			this.queueingTheoryFrishmanBeachElement = new QueueingTheory(
					this.awsDataMap.get("Frishman_Beach" + "Lambda"), this.awsDataMap.get("Frishman_Beach" + "W"),
					this.awsDataMap.get("Frishman_Beach" + "Wq"), this.awsDataMap.get("Frishman_Beach" + "Servers"));

			create(this.projectName, "avraham@gmail.com",
					new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
							"Frishman Beach", true, new Date(System.currentTimeMillis()),
							new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
							new HashMap<String, Object>() {
								{
									put("top", 32.11795);
									put("left", 34.81474);
									put("bottom", 32.11614);
									put("right", 34.8169);
								}
							}),
					this.queueingTheoryFrishmanBeachElement);

			// Sharona_Market
			this.queueingTheorySharonaMarketElement = new QueueingTheory(
					this.awsDataMap.get("Sharona_Market" + "Lambda"), this.awsDataMap.get("Sharona_Market" + "W"),
					this.awsDataMap.get("Sharona_Market" + "Wq"), this.awsDataMap.get("Sharona_Market" + "Servers"));

			create(this.projectName, "avraham@gmail.com",
					new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
							"Sharona Market", true, new Date(System.currentTimeMillis()),
							new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
							new HashMap<String, Object>() {
								{
									put("top", 32.11614);
									put("left", 34.81906);
									put("bottom", 32.11433);
									put("right", 34.8212);
								}
							}),
					this.queueingTheorySharonaMarketElement);

			// Afeka
			this.queueingTheoryAfekaElement = new QueueingTheory(this.awsDataMap.get("Afeka" + "Lambda"),
					this.awsDataMap.get("Afeka" + "W"), this.awsDataMap.get("Afeka" + "Wq"),
					this.awsDataMap.get("Afeka" + "Servers"));

			create(this.projectName, "avraham@gmail.com",
					new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre", "Afeka",
							true, new Date(System.currentTimeMillis()),
							new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
							new HashMap<String, Object>() {
								{
									put("top", 32.11614);
									put("left", 34.8169);
									put("bottom", 32.11433);
									put("right", 34.81906);
								}
							}),
					this.queueingTheoryAfekaElement);

			// Allenby
			this.queueingTheoryAllenbyElement = new QueueingTheory(this.awsDataMap.get("Allenby" + "Lambda"),
					this.awsDataMap.get("Allenby" + "W"), this.awsDataMap.get("Allenby" + "Wq"),
					this.awsDataMap.get("Allenby" + "Servers"));

			create(this.projectName, "avraham@gmail.com",
					new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
							"Allenby", true, new Date(System.currentTimeMillis()),
							new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
							new HashMap<String, Object>() {
								{
									put("top", 32.11614);
									put("left", 34.81474);
									put("bottom", 32.11433);
									put("right", 34.8169);
								}
							}),
					this.queueingTheoryAllenbyElement);

			// School
			this.queueingTheorySchoolElement = new QueueingTheory(this.awsDataMap.get("School" + "Lambda"),
					this.awsDataMap.get("School" + "W"), this.awsDataMap.get("School" + "Wq"),
					this.awsDataMap.get("School" + "Servers"));

			create(this.projectName, "avraham@gmail.com",
					new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
							"School Area", true, new Date(System.currentTimeMillis()),
							new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
							new HashMap<String, Object>() {
								{
									put("top", 32.11614);
									put("left", 34.81474);
									put("bottom", 32.11433);
									put("right", 34.8169);
								}
							}),
					this.queueingTheorySchoolElement);

			// Food_area
			this.queueingTheoryFoodAreaElement = new QueueingTheory(this.awsDataMap.get("Food_area" + "Lambda"),
					this.awsDataMap.get("Food_area" + "W"), this.awsDataMap.get("Food_area" + "Wq"),
					this.awsDataMap.get("Food_area" + "Servers"));

			create(this.projectName, "avraham@gmail.com",
					new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre",
							"Food area", true, new Date(System.currentTimeMillis()),
							new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
							new HashMap<String, Object>() {
								{
									put("top", 32.11614);
									put("left", 34.81474);
									put("bottom", 32.11433);
									put("right", 34.8169);
								}
							}),
					this.queueingTheoryFoodAreaElement);

			// Work
			this.queueingTheoryWorkElement = new QueueingTheory(this.awsDataMap.get("Work" + "Lambda"),
					this.awsDataMap.get("Work" + "W"), this.awsDataMap.get("Work" + "Wq"),
					this.awsDataMap.get("Work" + "Servers"));

			create(this.projectName, "avraham@gmail.com",
					new ElementBoundary(new ElementId(getProjectName(), UUID.randomUUID().toString()), "squre", "Work",
							true, new Date(System.currentTimeMillis()),
							new CreatedBy(new UserId(this.projectName, "avraham@gmail.com")),
							new HashMap<String, Object>() {
								{
									put("top", 32.11614);
									put("left", 34.81474);
									put("bottom", 32.11433);
									put("right", 34.8169);
								}
							}),
					this.queueingTheoryWorkElement);
		} // end of else
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
	public ElementBoundary create(String managerDomain, String managerEmail, ElementBoundary elementBoundary,
			QueueingTheory queueingTheory) {
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
		elementBoundary.getElementAttributes().put("generalQuantity", queueingTheory.getGeneralQuantity());
		elementBoundary.getElementAttributes().put("averageQueueQuantity_q",
				queueingTheory.getAverageQueueQuantity_q());
		elementBoundary.getElementAttributes().put("serviceRate", queueingTheory.getServiceRate());
		elementBoundary.getElementAttributes().put("averageServiceDuration",
				queueingTheory.getAverageServiceDuration());
		elementBoundary.getElementAttributes().put("overload", queueingTheory.getOverload());
		elementBoundary.getElementAttributes().put("getServiceImmediately", queueingTheory.getGetServiceImmediately());
		elementBoundary.getElementAttributes().put("r", queueingTheory.getR());
		elementBoundary.getElementAttributes().put("w_t", queueingTheory.getW_t());

		// Convert the element boundary to element entity
		ElementEntity elementEntity = elementConverter.toEntity(elementBoundary);

		// Add to database
		this.elementDao.save(elementEntity);

		return elementBoundary;
	}

	@Override
	public ElementBoundary createWithoutSaving(String managerDomain, String managerEmail,
			ElementBoundary elementBoundary, QueueingTheory queueingTheory) {
//		DatabaseUserService.checkRole(managerDomain, managerEmail, UserRole.MANAGER, userDao, userConverter);
//		// Validate that the important element boundary fields are not null;
//
//		elementBoundary.validation();

		// Set the element's domain to the project name and create the unique id for the
		// element.
//		elementBoundary.setElementId(new ElementId(getProjectName(), UUID.randomUUID().toString()));

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
		elementBoundary.getElementAttributes().put("generalQuantity", queueingTheory.getGeneralQuantity());
		elementBoundary.getElementAttributes().put("averageQueueQuantity_q",
				queueingTheory.getAverageQueueQuantity_q());
		elementBoundary.getElementAttributes().put("serviceRate", queueingTheory.getServiceRate());
		elementBoundary.getElementAttributes().put("averageServiceDuration",
				queueingTheory.getAverageServiceDuration());
		elementBoundary.getElementAttributes().put("overload", queueingTheory.getOverload());
		elementBoundary.getElementAttributes().put("getServiceImmediately", queueingTheory.getGetServiceImmediately());
		elementBoundary.getElementAttributes().put("r", queueingTheory.getR());
		elementBoundary.getElementAttributes().put("w_t", queueingTheory.getW_t());

//		// Convert the element boundary to element entity
//		ElementEntity elementEntity = elementConverter.toEntity(elementBoundary);

//		// Add to database
//		this.elementDao.save(elementEntity);

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
//		System.err.println("******foundedElement*******************************************************************************");

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
		} else if (role == UserRole.ACTOR) {
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
	}

	@Override
	public void deleteAllElements(String adminDomain, String adminEmail) {
		DatabaseUserService.checkRole(adminDomain, adminEmail, UserRole.MANAGER, userDao, userConverter);
		// Clear all elements from DB.
		this.elementDao.deleteAll();
	}

	@Override
	public List<ElementBoundary> getAll(String userDomain, String userEmail, int size, int page) {
		UserEntity userEntity = DatabaseUserService
				.getUserEntityFromDatabase(this.userConverter.convertToEntityId(userDomain, userEmail), userDao);
		List<ElementEntity> entities;

		if (userEntity.getRole() == UserRole.MANAGER) {
			entities = this.elementDao.findAll(PageRequest.of(page, size, Direction.ASC, "elementId")).getContent();
		} else if (userEntity.getRole() == UserRole.ACTOR) {
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
		} else if (userEntity.getRole() == UserRole.ACTOR) {
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
		} else if (userEntity.getRole() == UserRole.ACTOR) {
			entities = this.elementDao.findAllByTypeLikeAndActive(type, true,
					PageRequest.of(page, size, Direction.ASC, "elementId"));
		} else {
			throw new UserNotFoundException("Not valid operation for ADMIN user");
		}

		return entities.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
	}

}
