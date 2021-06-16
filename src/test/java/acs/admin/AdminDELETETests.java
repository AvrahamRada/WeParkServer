package acs.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.HashMap;
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
//import acs.util.Location;
import acs.util.NewUserDetails;
import acs.util.TestUtil;
import acs.util.UserId;
import acs.util.UserRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdminDELETETests {
//
//	private int port;
//	// User
//	private String allUsersUrl;
//	// Element
//	private String allElementsUrl;
//	// Action
//	private String allActionsUrl;
//	// Admin
//	private String deleteActionsUrl;
//	private String deleteElementsUrl;
//	private String deleteUsersUrl;
//
//	private RestTemplate restTemplate;
//
//	@LocalServerPort
//	public void setPort(int port) {
//		this.port = port;
//	}
//
//	@PostConstruct
//	public void init() {
//		// for user
//		this.allUsersUrl = "http://localhost:" + port + "/acs/admin/users/{adminDomain}/{adminEmail}";
//		// for elements
//		this.allElementsUrl = "http://localhost:" + port + "/acs/elements/{userDomain}/{userEmail}";
//		// for invoke action
//		this.allActionsUrl = "http://localhost:" + port + "/acs/admin/actions/{adminDomain}/{adminEmail}";
//		// for DELETE of admin
//		this.deleteActionsUrl = "http://localhost:" + port + "/acs/admin/actions/{adminDomain}/{adminEmail}";
//		this.deleteElementsUrl = "http://localhost:" + port + "/acs/admin/elements/{adminDomain}/{adminEmail}";
//		this.deleteUsersUrl = "http://localhost:" + port + "/acs/admin/users/{adminDomain}/{adminEmail}";
//		this.restTemplate = new RestTemplate();
//	}
//
//	@BeforeEach
//	public void setup() {
//		// TestUtil.clearDB(port);
//	}
//
//	@AfterEach
//	public void teardown() {
//		TestUtil.clearDB(port);
//	}
//
//	@Test
//	public void testContext() {
//
//	}
//
//
//
//	// Test with Admin
//	@Test
//	public void testDeleteAllActionsWith10ActionsAndAdmin() throws Exception {
//		// create admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER,port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//		// create elements
//		TestUtil.createElementsByX(10, manager,port);
//		ElementBoundary[] elementBoundaries = this.restTemplate.getForObject(this.allElementsUrl,
//				ElementBoundary[].class, manager.getUserId().getDomain(), manager.getUserId().getEmail());
//		// create X actions
//		final int X = 10;
//		TestUtil.createActionsByX(X, player, elementBoundaries,port);
//
//		// delete all actions from DB
//		this.restTemplate.delete(this.deleteActionsUrl, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		// get all actions after delete
//		ActionBoundary[] rv = this.restTemplate.getForObject(this.allActionsUrl, ActionBoundary[].class,
//				admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		// the server returns an empty array
//		assertThat(rv).isEmpty();
//	}
//
//	@Test
//	public void testDeleteAllActionsWith100ActionsAndAdmin() throws Exception {
//
//		// create X actions
//		final int X = 100;
//		// create admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER,port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//		// create elements
//		TestUtil.createElementsByX(X, manager,port);
//		ElementBoundary[] elementBoundaries = this.restTemplate.getForObject(
//				this.allElementsUrl + "?page={page}&size={size}", ElementBoundary[].class,
//				manager.getUserId().getDomain(), manager.getUserId().getEmail(), 0, X);
//		// create X actions
//		TestUtil.createActionsByX(X, player, elementBoundaries,port);
//
//		// delete all actions from DB
//		this.restTemplate.delete(this.deleteActionsUrl, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		// get all actions after delete
//		ActionBoundary[] rv = this.restTemplate.getForObject(this.allActionsUrl + "?page={page}&size={size}",
//				ActionBoundary[].class, admin.getUserId().getDomain(), admin.getUserId().getEmail(), 0, X);
//
//		// the server returns an empty array
//		assertThat(rv).isEmpty();
//	}
//
//	@Test
//	public void testDeleteAllElementsWith10ElementsAndAdmin() throws Exception {
//		final int X = 10;
//		// create admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//		// create elements
//		TestUtil.createElementsByX(X, manager,port);
//
//		// delete all elements from DB
//		this.restTemplate.delete(this.deleteElementsUrl, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		// get all elements after delete
//		ElementBoundary[] rv = this.restTemplate.getForObject(this.allElementsUrl, ElementBoundary[].class,
//				manager.getUserId().getDomain(), manager.getUserId().getEmail());
//
//		// the server returns an empty array
//		assertThat(rv).isEmpty();
//	}
//
//	@Test
//	public void testDeleteAllElementsWith100ElementsAndAdmin() throws Exception {
//		final int X = 100;
//		// create admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//		// create elements
//		TestUtil.createElementsByX(X, manager,port);
//
//		// delete all elements from DB
//		this.restTemplate.delete(this.deleteElementsUrl, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		// get all elements after delete
//		ElementBoundary[] rv = this.restTemplate.getForObject(this.allElementsUrl, ElementBoundary[].class,
//				manager.getUserId().getDomain(), manager.getUserId().getEmail());
//
//		// the server returns an empty array
//		assertThat(rv).isEmpty();
//	}
//
//	@Test
//	public void testDeleteAllUsersWith10UsersAndAdmin() throws Exception {
//		final int X = 10;
//		// create admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
//
//		// create X users
//		TestUtil.createUsersByX(X, UserRole.PLAYER,port);
//
//		// get all users
//		UserBoundary[] rv = this.restTemplate.getForObject(this.allUsersUrl + "?page={page}&size={size}",
//				UserBoundary[].class, admin.getUserId().getDomain(), admin.getUserId().getEmail(), 0, X + 1);
//
//		// the server returns array of 10 users and 1 admin = 11 users
//		assertThat(rv).hasSize(X + 1);
//
//		// delete all users from DB - include admin
//		this.restTemplate.delete(this.deleteUsersUrl, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		// Recreate admin
//		admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
//
//		// get all users - only admin
//		rv = this.restTemplate.getForObject(this.allUsersUrl, UserBoundary[].class, admin.getUserId().getDomain(),
//				admin.getUserId().getEmail());
//
//		// the server returns array of 1 admin = 1 users
//		assertThat(rv).hasSize(1);
//	}
//
//	@Test
//	public void testDeleteAllUsersWith100UsersAndAdmin() throws Exception {
//		final int X = 100;
//		// create admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
//
//		// create X users
//		TestUtil.createUsersByX(X, UserRole.PLAYER,port);
//
//		// get all users
//		UserBoundary[] rv = this.restTemplate.getForObject(this.allUsersUrl + "?page={page}&size={size}",
//				UserBoundary[].class, admin.getUserId().getDomain(), admin.getUserId().getEmail(), 0, X + 1);
//
//		// the server returns array of 100 users and 1 admin = 101 users
//		assertThat(rv).hasSize(X + 1);
//
//		// delete all users from DB - include admin
//		this.restTemplate.delete(this.deleteUsersUrl, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		// Recreate admin
//		admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
//
//		// get all users - only admin
//		rv = this.restTemplate.getForObject(this.allUsersUrl, UserBoundary[].class, admin.getUserId().getDomain(),
//				admin.getUserId().getEmail());
//
//		// the server returns array of 1 admin = 1 users
//		assertThat(rv).hasSize(1);
//	}
//
//	// Test with Not Admin - player
//	@Test
//	public void testDeleteAllActionsWithActionsAndPlayerReturnsStatusDiffrenceFrom2xx() throws Exception {
//
//		// create X actions
//		final int X = 10;
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER,port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//		// create elements
//		TestUtil.createElementsByX(X, manager,port);
//		ElementBoundary[] elementBoundaries = this.restTemplate.getForObject(
//				this.allElementsUrl + "?page={page}&size={size}", ElementBoundary[].class,
//				manager.getUserId().getDomain(), manager.getUserId().getEmail(), 0, X);
//		// create X actions
//		TestUtil.createActionsByX(X, player, elementBoundaries,port);
//
//		// delete all actions from DB with player - who is not admin
//		assertThrows(Exception.class, () -> this.restTemplate.delete(this.deleteActionsUrl,
//				player.getUserId().getDomain(), player.getUserId().getEmail()));
//
//	}
//
//	// Test with Not Admin - player
//	@Test
//	public void testDeleteAllActionsWithActionsAndManagerReturnsStatusDiffrenceFrom2xx() throws Exception {
//
//		// create X actions
//		final int X = 10;
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER,port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//		// create elements
//		TestUtil.createElementsByX(X, manager,port);
//		ElementBoundary[] elementBoundaries = this.restTemplate.getForObject(
//				this.allElementsUrl + "?page={page}&size={size}", ElementBoundary[].class,
//				manager.getUserId().getDomain(), manager.getUserId().getEmail(), 0, X);
//		// create X actions
//		TestUtil.createActionsByX(X, player, elementBoundaries,port);
//
//		// delete all actions from DB with player - who is not admin
//		assertThrows(Exception.class, () -> this.restTemplate.delete(this.deleteActionsUrl,
//				manager.getUserId().getDomain(), manager.getUserId().getEmail()));
//
//	}
//
//	@Test
//	public void testDeleteAllElementsWithElementsAndPlayer() throws Exception {
//
//		// create X elements
//		final int X = 10;
//		// create A NOT admin user
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER,port);
//		// create elements
//		TestUtil.createElementsByX(X, manager,port);
//
//		// delete all actions from DB with player - who is not admin
//		assertThrows(Exception.class, () -> this.restTemplate.delete(this.deleteActionsUrl,
//				player.getUserId().getDomain(), player.getUserId().getEmail()));
//
//	}
//
//	@Test
//	public void testDeleteAllElementsWithElementsAndManager() throws Exception {
//
//		// create X elements
//		final int X = 10;
//		// create A NOT admin user
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//		// create elements
//		TestUtil.createElementsByX(X, manager,port);
//
//		// delete all actions from DB with player - who is not admin
//		assertThrows(Exception.class, () -> this.restTemplate.delete(this.deleteActionsUrl,
//				manager.getUserId().getDomain(), manager.getUserId().getEmail()));
//
//	}
//
//	@Test
//	public void testDeleteAllUsersWithUsersAndPlayer() {
//		// create A NOT admin user
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER,port);
//
//		// create X users
//		final int X = 10;
//		TestUtil.createUsersByX(X, UserRole.PLAYER,port);
//
//		// delete all users from DB
//		assertThrows(Exception.class, () -> this.restTemplate.delete(this.deleteUsersUrl,
//				player.getUserId().getDomain(), player.getUserId().getEmail()));
//	}
//
//	@Test
//	public void testDeleteAllUsersWithUsersAndManager() {
//		// create A NOT admin user
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//
//		// create X users
//		final int X = 10;
//		TestUtil.createUsersByX(X, UserRole.PLAYER,port);
//
//		// delete all users from DB
//		assertThrows(Exception.class, () -> this.restTemplate.delete(this.deleteUsersUrl,
//				manager.getUserId().getDomain(), manager.getUserId().getEmail()));
//	}
//
//	// Test with wrong path
//	@Test
//	public void testDeleteAllActionsWithWrongPath() throws Exception {
//
//		// create X actions
//		final int X = 100;
//		// create admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
//		// create player
//		UserBoundary player = TestUtil.createNewUserByChoice(UserRole.PLAYER,port);
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//		// create elements
//		TestUtil.createElementsByX(X, manager,port);
//		ElementBoundary[] elementBoundaries = this.restTemplate.getForObject(
//				this.allElementsUrl + "?page={page}&size={size}", ElementBoundary[].class,
//				manager.getUserId().getDomain(), manager.getUserId().getEmail(), 0, X);
//		// create X actions
//		TestUtil.createActionsByX(X, player, elementBoundaries,port);
//
//		// not delete all elements from DB
//		this.restTemplate.delete(this.deleteElementsUrl, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		// get all actions after delete elements
//		ActionBoundary[] rv = this.restTemplate.getForObject(this.allActionsUrl, ActionBoundary[].class,
//				admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		// the server returns an isn't empty array
//		assertThat(rv).isNotEmpty();
//	}
//
//	@Test
//	public void testDeleteAllElementsWithWrongPath() throws Exception {
//		// create and login admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//
//		// create X elements
//		final int X = 10;
//		TestUtil.createElementsByX(X, manager,port);
//
//		// not delete all elements from DB
//		this.restTemplate.delete(this.deleteActionsUrl, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		// get all elements after delete
//		ElementBoundary[] rv = this.restTemplate.getForObject(this.allElementsUrl, ElementBoundary[].class,
//				manager.getUserId().getDomain(), manager.getUserId().getEmail());
//
//		// the server returns an empty array
//		assertThat(rv).isNotEmpty();
//	}
//
//	@Test
//	public void testDeleteAllUsersWithWrongPath() throws Exception {
//		// create and login admin
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
//
//		// create X users
//		final int X = 10;
//		TestUtil.createUsersByX(X, UserRole.PLAYER,port);
//
//
//		// not delete all users from DB - include admin
//		this.restTemplate.delete(this.deleteActionsUrl, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//		// get all users - only admin
//		UserBoundary[] rv = this.restTemplate.getForObject(this.allUsersUrl, UserBoundary[].class, admin.getUserId().getDomain(),
//				admin.getUserId().getEmail());
//
//		// the server returns array of 10 users and 1 admin = 11 users
//		assertThat(rv).isNotEmpty();
//	}

}
