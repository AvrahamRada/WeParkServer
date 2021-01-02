package acs.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.util.ActionId;
import acs.util.CreatedBy;
import acs.util.Element;
import acs.util.ElementId;
import acs.util.InvokedBy;
import acs.util.Location;
import acs.util.NewUserDetails;
import acs.util.TestUtil;
import acs.util.UserId;
import acs.util.UserRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActionPOSTTests {
//
//	private int port;
//	private String url;
//	private String createUserUrl;
//	private String getAllActionsUrl;
//	private RestTemplate restTemplate;
//	private String createElementUrl;
//	private String allElementsUrl;
//
//	@LocalServerPort
//	public void setPort(int port) {
//		this.port = port;
//	}
//
//	@PostConstruct
//	public void init() {
//		this.url = "http://localhost:" + port + "/acs/actions";
//		this.getAllActionsUrl = "http://localhost:" + port + "/acs/admin/actions/{adminDomain}/{adminEmail}";
//		this.createUserUrl = "http://localhost:" + port + "/acs/users";
//		this.createElementUrl = "http://localhost:" + port + "/acs/elements/{managerDomain}/{managerEmail}";
//		// for elements
//		this.allElementsUrl = "http://localhost:" + port + "/acs/elements/{userDomain}/{userEmail}";
//		this.restTemplate = new RestTemplate();
//	}
//
//	@BeforeEach
//	public void setup() {
//
//		TestUtil.clearDB(port);
//	}
//
//	@AfterEach
//	public void teardown() {
//
//		TestUtil.clearDB(port);
//	}
//
//	@Test
//	public void testContext() {
//
//	}
//
//	@Test
//	public void testPostSingleActionWithNoActionIdServerSaveToDBNewActionEntityWithGeneratedID() throws Exception {
//
//		// create admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN, port);
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER, port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER, port);
//		TestUtil.createElementsByX(1, manager, port);
//
//		// create element with manager
//		ElementBoundary newElement = this.restTemplate.postForObject(this.createElementUrl,
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "x"), "type", "name", true,
//						new Date(System.currentTimeMillis()), new CreatedBy(manager.getUserId()),
//						new Location(40.730610, -73.935242), new HashMap<>()),
//				ElementBoundary.class, manager.getUserId().getDomain(), manager.getUserId().getEmail());
//
//		ActionBoundary newAction = this.restTemplate.postForObject(this.url,
//				new ActionBoundary(null, "type", new Element(newElement.getElementId()),
//						new Date(System.currentTimeMillis()), new InvokedBy(player.getUserId()),
//						new HashMap<String, Object>()),
//				ActionBoundary.class);
//
//		ActionBoundary[] actualActionsArray = this.restTemplate.getForObject(this.getAllActionsUrl,
//				ActionBoundary[].class, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		assertThat(actualActionsArray).usingRecursiveFieldByFieldElementComparator().contains(newAction);
//
//	}
//
//	@Test
//	public void testPostSingleActionServerSaveToDBNewActionEntityWithGeneratedID() throws Exception {
//
//		// create admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN, port);
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER, port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER, port);
////		TestUtil.createElementsByX(1, manager, port);
//
//		// create element with manager
//		ElementBoundary newElement = this.restTemplate.postForObject(this.createElementUrl,
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "x"), "type", "name", true,
//						new Date(System.currentTimeMillis()), new CreatedBy(manager.getUserId()),
//						new Location(40.730610, -73.935242), new HashMap<>()),
//				ElementBoundary.class, manager.getUserId().getDomain(), manager.getUserId().getEmail());
//
//		ActionBoundary newAction = this.restTemplate.postForObject(this.url,
//				new ActionBoundary(new ActionId("2020b.lior.trachtman", "dont care"), "type",
//						new Element(newElement.getElementId()), new Date(System.currentTimeMillis()),
//						new InvokedBy(player.getUserId()), new HashMap<String, Object>()),
//				ActionBoundary.class);
//
//		ActionBoundary[] actualActionsArray = this.restTemplate.getForObject(this.getAllActionsUrl,
//				ActionBoundary[].class, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		assertThat(actualActionsArray).usingRecursiveFieldByFieldElementComparator().contains(newAction);
//
//	}
//
//	@Test
//	public void testPostSingleActionWithNoElementDatabaseReturnStatusDifferenceFrom2xx() throws Exception {
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER, port);
//
//		// GIVEN the server is up
//		// do nothing
//
//		// WHEN I POST /acs/actions with Action Boundary with no element
//
//		// THEN the server returns status != 2xx
//		assertThrows(Exception.class,
//				() -> this.restTemplate.postForObject(this.url,
//						new ActionBoundary(null, "type", null, new Date(System.currentTimeMillis()),
//								new InvokedBy(player.getUserId()), new HashMap<String, Object>()),
//						ActionBoundary.class));
//
//	}
//
//	@Test
//	public void testPostSingleActionWithNoElementIdDatabaseReturnStatusDifferenceFrom2xx() throws Exception {
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER, port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER, port);
//		// GIVEN the server is up
//		// do nothing
//
//		// WHEN I POST /acs/actions with Action Boundary with no element
//
//		// THEN the server returns status != 2xx
//		assertThrows(Exception.class,
//				() -> this.restTemplate.postForObject(this.url,
//						new ActionBoundary(null, "type", new Element(null), new Date(System.currentTimeMillis()),
//								new InvokedBy(player.getUserId()), new HashMap<String, Object>()),
//						ActionBoundary.class));
//
//	}
//
//	@Test
//	public void testPostSingleActionWithNoTypeDatabaseReturnStatusDifferenceFrom2xx() throws Exception {
//
//		// GIVEN the server is up
//		// do nothing
//
//		// WHEN I POST /acs/actions with Action Boundary with no type
//
//		// THEN the server returns status != 2xx
//		assertThrows(Exception.class, () -> this.restTemplate.postForObject(this.url,
//				new ActionBoundary(null, null, new Element(new ElementId("2020b.lior.trachtman", "don't care")),
//						new Date(System.currentTimeMillis()),
//						new InvokedBy(new UserId("2020b.lior.trachtman", "don't care")), new HashMap<String, Object>()),
//				ActionBoundary.class));
//
//	}
//
//	@Test
//	public void testPostSingleActionWithNoCreatedTimestampDatabaseStoreActionEntityWithGenereatedTimestamp()
//			throws Exception {
//
//		// create admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN, port);
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER, port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER, port);
////		TestUtil.createElementsByX(1, manager, port);
//
//		// create element with manager
//		ElementBoundary newElement = this.restTemplate.postForObject(this.createElementUrl,
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "x"), "type", "name", true,
//						new Date(System.currentTimeMillis()), new CreatedBy(manager.getUserId()),
//						new Location(40.730610, -73.935242), new HashMap<>()),
//				ElementBoundary.class, manager.getUserId().getDomain(), manager.getUserId().getEmail());
//
//		ActionBoundary newAction = this.restTemplate.postForObject(this.url,
//				new ActionBoundary(new ActionId("2020b.lior.trachtman", "dont care"), "type",
//						new Element(newElement.getElementId()), null, new InvokedBy(player.getUserId()),
//						new HashMap<String, Object>()),
//				ActionBoundary.class);
//
//		ActionBoundary[] actualActionsArray = this.restTemplate.getForObject(this.getAllActionsUrl,
//				ActionBoundary[].class, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		assertThat(actualActionsArray).usingRecursiveFieldByFieldElementComparator().contains(newAction);
//
//	}
//
//	@Test
//	public void testPostSingleActionWithNoInvokedByDatabaseReturnStatusDifferenceFrom2xx() throws Exception {
//
//		// GIVEN the server is up
//		// do nothing
//
//		// WHEN I POST /acs/actions with Action Boundary with no invoked by
//
//		// THEN the server returns status != 2xx
//		assertThrows(Exception.class, () -> this.restTemplate.postForObject(this.url,
//				new ActionBoundary(null, "type", new Element(new ElementId("2020b.lior.trachtman", "don't care")),
//						new Date(System.currentTimeMillis()), null, new HashMap<String, Object>()),
//				ActionBoundary.class));
//
//	}
//
//	@Test
//	public void testPostSingleActionWithNoActionAttributesByDatabaseReturnStatusDifferenceFrom2xx() throws Exception {
//
//		// GIVEN the server is up
//		// do nothing
//
//		// WHEN I POST /acs/actions with Action Boundary with no invoked by
//
//		// THEN the server returns status != 2xx
//		assertThrows(Exception.class, () -> this.restTemplate.postForObject(this.url,
//				new ActionBoundary(null, "type", new Element(new ElementId("2020b.lior.trachtman", "don't care")),
//						new Date(System.currentTimeMillis()),
//						new InvokedBy(new UserId("2020b.lior.trachtman", "don't care")), null),
//				ActionBoundary.class));
//
//	}
//
//	@Test
//	public void testPost10ValidActionServerSaveToDBAllEntitesWithGeneratedID() throws Exception {
//
//		final int X = 10;
//
//		// create admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN, port);
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER, port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER, port);
//		// create 100 elements
//		TestUtil.createElementsByX(X, manager, port);
//
//		ElementBoundary[] boundaries = this.restTemplate.getForObject(this.allElementsUrl + "?page={page}&size={size}",
//				ElementBoundary[].class, manager.getUserId().getDomain(), manager.getUserId().getEmail(), 0, X);
//
//		List<ActionBoundary> storedActions = new ArrayList<>();
//
//		for (int i = 0; i < X; i++) {
//			storedActions.add(this.restTemplate.postForObject(this.url,
//					new ActionBoundary(null, "type", new Element(boundaries[i].getElementId()),
//							new Date(System.currentTimeMillis()), new InvokedBy(player.getUserId()),
//							new HashMap<String, Object>()),
//					ActionBoundary.class));
//		}
//
//		// WHEN I POST X action boundaries to the server
//		ActionBoundary[] actualActionsArray = this.restTemplate.getForObject(
//				this.getAllActionsUrl + "?page={page}&size={size}", ActionBoundary[].class,
//				admin.getUserId().getDomain(), admin.getUserId().getEmail(), 0, X);
//
//		// THEN the server returns the same X actions in the database (which mean DB
//		// saved the action entites
//		assertThat(actualActionsArray).usingRecursiveFieldByFieldElementComparator()
//				.containsExactlyInAnyOrderElementsOf(storedActions);
//
//	}
//
//	@Test
//	public void testPost100ValidActionServerSaveToDBAllEntitesWithGeneratedID() throws Exception {
//
//		final int X = 100;
//		// create admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN, port);
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER, port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER, port);
//		// create 100 elements
//		TestUtil.createElementsByX(X, manager, port);
//
//		ElementBoundary[] boundaries = this.restTemplate.getForObject(this.allElementsUrl + "?page={page}&size={size}",
//				ElementBoundary[].class, manager.getUserId().getDomain(), manager.getUserId().getEmail(), 0, X);
//
//		List<ActionBoundary> storedActions = new ArrayList<>();
//
//		for (int i = 0; i < X; i++) {
//			storedActions.add(this.restTemplate.postForObject(this.url,
//					new ActionBoundary(null, "type", new Element(boundaries[i].getElementId()),
//							new Date(System.currentTimeMillis()), new InvokedBy(player.getUserId()),
//							new HashMap<String, Object>()),
//					ActionBoundary.class));
//		}
//
//		// WHEN I POST X action boundaries to the server
//		ActionBoundary[] actualActionsArray = this.restTemplate.getForObject(
//				this.getAllActionsUrl + "?page={page}&size={size}", ActionBoundary[].class,
//				admin.getUserId().getDomain(), admin.getUserId().getEmail(), 0, X);
//
//		// THEN the server returns the same X actions in the database (which mean DB
//		// saved the action entites
//		assertThat(actualActionsArray).usingRecursiveFieldByFieldElementComparator()
//				.containsExactlyInAnyOrderElementsOf(storedActions);
//
//	}
//
//	@Test
//	public void testPost1000ValidActionServerSaveToDBAllEntitesWithGeneratedID() throws Exception {
//
//		final int X = 1000;
//		// create admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN, port);
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER, port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER, port);
//		// create 100 elements
//		TestUtil.createElementsByX(X, manager, port);
//
//		ElementBoundary[] boundaries = this.restTemplate.getForObject(this.allElementsUrl + "?page={page}&size={size}",
//				ElementBoundary[].class, manager.getUserId().getDomain(), manager.getUserId().getEmail(), 0, X);
//
//		List<ActionBoundary> storedActions = new ArrayList<>();
//
//		for (int i = 0; i < X; i++) {
//			storedActions.add(this.restTemplate.postForObject(this.url,
//					new ActionBoundary(null, "type", new Element(boundaries[i].getElementId()),
//							new Date(System.currentTimeMillis()), new InvokedBy(player.getUserId()),
//							new HashMap<String, Object>()),
//					ActionBoundary.class));
//		}
//
//		// WHEN I POST X action boundaries to the server
//		ActionBoundary[] actualActionsArray = this.restTemplate.getForObject(
//				this.getAllActionsUrl + "?page={page}&size={size}", ActionBoundary[].class,
//				admin.getUserId().getDomain(), admin.getUserId().getEmail(), 0, X);
//
//		// THEN the server returns the same X actions in the database (which mean DB
//		// saved the action entites
//		assertThat(actualActionsArray).usingRecursiveFieldByFieldElementComparator()
//				.containsExactlyInAnyOrderElementsOf(storedActions);
//
//	}

}
