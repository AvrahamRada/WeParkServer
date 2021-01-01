package acs.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

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
public class AdminGETTests {

	private int port;
	private String allUsersUrl;
	private String createUserUrl;
	private String allActionsUrl;
	private String allElementsUrl;
	private String invokeActionUrl;
	private String createElementUrl;
	private RestTemplate restTemplate;
	
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.createUserUrl = "http://localhost:" + port + "/acs/users";
		this.invokeActionUrl = "http://localhost:" + port + "/acs/actions";
		this.allUsersUrl = "http://localhost:" + port + "/acs/admin/users/{adminDomain}/{adminEmail}";
		this.allActionsUrl = "http://localhost:" + port + "/acs/admin/actions/{adminDomain}/{adminEmail}";
		this.createElementUrl = "http://localhost:" + port + "/acs/elements/{managerDomain}/{managerEmail}";
		this.allElementsUrl = "http://localhost:" + port + "/acs/elements/{userDomain}/{userEmail}";
		this.restTemplate = new RestTemplate();
	}

	@BeforeEach
	public void setup() {

		TestUtil.clearDB(port);

	}

	@AfterEach
	public void teardown() {

		TestUtil.clearDB(port);

	}

	@Test
	public void testContext() {

	}

	

	@Test
	public void testGetAllActionsWith10ActionsReturnsDatabaseWith10Actions() throws Exception {
		// create admin
		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
		// create player
		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER,port);
		// create manager
		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
		//create elements
		TestUtil.createElementsByX(10, manager,port);
		ElementBoundary[] elementBoundaries = this.restTemplate.getForObject(this.allElementsUrl
				, ElementBoundary[].class, manager.getUserId().getDomain(),manager.getUserId().getEmail());
		// create X actions
		final int X = 10;
		TestUtil.createActionsByX(X, player, elementBoundaries,port);

		// get all actions
		ActionBoundary[] rv = this.restTemplate.getForObject(this.allActionsUrl, ActionBoundary[].class,
				admin.getUserId().getDomain(), admin.getUserId().getEmail());

		// the server returns array of X actions
		assertThat(rv).hasSize(X);
	}

	@Test
	public void testGetAllActionsWith100ActionsReturnDatabaseWith100Actions() throws Exception {
		final int X = 100;
		// create admin for get all actions
		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
		// create manager
		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
		// create player for invoke 100 actions
		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER,port);

		TestUtil.createElementsByX(X, manager,port);
		ElementBoundary[] elementBoundaries = this.restTemplate.getForObject(
				this.allElementsUrl + "?page={page}&size={size}"
				, ElementBoundary[].class, manager.getUserId().getDomain(),manager.getUserId().getEmail(),0,X);
		// create X actions
		
		TestUtil.createActionsByX(X, player, elementBoundaries,port);
		

		// get all actions
		ActionBoundary[] rv = this.restTemplate.getForObject(this.allActionsUrl + "?page={page}&size={size}",
				ActionBoundary[].class, admin.getUserId().getDomain(), admin.getUserId().getEmail(), 0, X);

		// the server returns array of X actions
		assertThat(rv).hasSize(X);

	}

	@Test
	public void testGetAllUsersWith10UsersAndAdmin() throws Exception {
		// create admin
		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);

		// create X users
		final int X = 10;
		TestUtil.createUsersByX(X, UserRole.PLAYER,port);
		

		// get all users
		UserBoundary[] rv = this.restTemplate.getForObject(this.allUsersUrl+ "?page={page}&size={size}", UserBoundary[].class,
				admin.getUserId().getDomain(), admin.getUserId().getEmail(),0,X+1);

		// the server returns array of 10 users and 1 admin = 11 users
		assertThat(rv).hasSize(X + 1);

	}

	@Test
	public void testGetAllUsersWith100UsersAndAdmin() throws Exception {
		// create admin
				UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);

				// create X users
				final int X = 100;
				TestUtil.createUsersByX(X, UserRole.PLAYER,port);
				

				// get all users
				UserBoundary[] rv = this.restTemplate.getForObject(this.allUsersUrl+ "?page={page}&size={size}", UserBoundary[].class,
						admin.getUserId().getDomain(), admin.getUserId().getEmail(),0,X+1);

				// the server returns array of 100 users and 1 admin = 101 users
				assertThat(rv).hasSize(X + 1);

	}

	@Test
	public void testGetAllActionsWithActionsAndNotAdminReturnsStatusDiffrenceFrom2xx() throws Exception {
		final int X = 10;
		// create manager
		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
		// create player for invoke 100 actions
		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER,port);

		TestUtil.createElementsByX(X, manager,port);
		ElementBoundary[] elementBoundaries = this.restTemplate.getForObject(
				this.allElementsUrl
				, ElementBoundary[].class, manager.getUserId().getDomain(),manager.getUserId().getEmail());
		// create X actions
		
		TestUtil.createActionsByX(X, player, elementBoundaries,port);
		
		assertThrows(Exception.class, ()->this.restTemplate.getForObject(this.allActionsUrl,
				ActionBoundary[].class, player.getUserId().getDomain(), player.getUserId().getEmail()));
		
	}

	@Test
	public void testGetAllUsersWithUsersAndNotAdmin() {
		// create A NOT admin user
		UserBoundary notAdmin = TestUtil.createNewUserByChoice(UserRole.PLAYER,port);

		// create X users
		final int X = 10;
		TestUtil.createUsersByX(X, UserRole.PLAYER,port);
		

		// delete all users from DB
		assertThrows(Exception.class, () -> this.restTemplate.getForObject(this.allUsersUrl, UserBoundary[].class,
				notAdmin.getUserId().getDomain(), notAdmin.getUserId().getEmail()));
	}

	@Test
	public void testGet10UsersAndAdminInDatabaseReturnAllUsersStoredInDatabase() throws Exception {
		int x = 10;
		List<UserBoundary> storedUsers = new ArrayList<>();
		for (int i = 0; i < x; i++) {
			storedUsers.add(this.restTemplate.postForObject(this.createUserUrl,
					new NewUserDetails("user" + i + "@gmail.com", UserRole.PLAYER, "user", ":)"), UserBoundary.class));
		}

		// create and login admin
		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);

		storedUsers.add(admin);

		// WHEN
		UserBoundary[] usersArray = this.restTemplate.getForObject(this.allUsersUrl + "?page={page}&size={size}" , UserBoundary[].class,
				admin.getUserId().getDomain(), admin.getUserId().getEmail(),0,x+1);

		// THEN the server returns the same 10 users and 1 admin = 11 users in the
		// database
		assertThat(usersArray).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedUsers);
	}

}
