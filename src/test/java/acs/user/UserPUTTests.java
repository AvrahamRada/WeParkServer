package acs.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.util.CreatedBy;
//import acs.util.Location;
import acs.util.NewUserDetails;
import acs.util.TestUtil;
import acs.util.UserId;
import acs.util.UserRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserPUTTests {
//
//	private int port;
//	private String createUserUrl;;
//	private RestTemplate restTemplate;
//	private String loginUrl;
//	private String updateUserUrl;
//
//	@LocalServerPort
//	public void setPort(int port) {
//		this.port = port;
//	}
//
//	@PostConstruct
//	public void init() {
//
//		this.createUserUrl = "http://localhost:" + port + "/acs/users";
//		this.updateUserUrl = "http://localhost:" + port + "/acs/users/{userDomain}/{userEmail}";
//		this.loginUrl = "http://localhost:" + port + "/acs/users/login/{userDomain}/{userEmail}";
//		this.restTemplate = new RestTemplate();
//	}
//
//	@BeforeEach
//	public void setup() {
//
//		TestUtil.clearDB(port);
//
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
//	public void testPutWithDatabaseContainigUserAndChangingItsRoleChangedTheResultRole() throws Exception {
//		// GIVEN the database contains a single user with PLAYER role
//		UserBoundary user = this.restTemplate.postForObject(this.createUserUrl,
//				new NewUserDetails("test@gmail.com", UserRole.PLAYER, "user", "Avatar"), UserBoundary.class);
//
//		// WHEN I PUT ADMIN role
//		UserBoundary changedUser = new UserBoundary(null, UserRole.ADMIN, "user", "Avatar");
//		this.restTemplate.put(this.updateUserUrl, changedUser, user.getUserId().getDomain(),
//				user.getUserId().getEmail());
//
//		// THEN the database is updated the user with the role value
//		assertThat(this.restTemplate.getForObject(this.loginUrl, UserBoundary.class, user.getUserId().getDomain(),
//				user.getUserId().getEmail()).getRole()).isEqualTo(changedUser.getRole());
//	}
//
//	@Test
//	public void testPutWithDatabaseContainigElementAndChangingItsUsernameChangedTheResultUsername() throws Exception {
//		// GIVEN the database contains a single user with user as name
//		UserBoundary user = this.restTemplate.postForObject(this.createUserUrl,
//				new NewUserDetails("test@gmail.com", UserRole.PLAYER, "user", "Avatar"), UserBoundary.class);
//
//		// WHEN I PUT new user name
//		UserBoundary changedUser = new UserBoundary(null, UserRole.ADMIN, "newUsername", "Avatar");
//		this.restTemplate.put(this.updateUserUrl, changedUser, user.getUserId().getDomain(),
//				user.getUserId().getEmail());
//
//		// THEN the database is updated the new user name
//		assertThat(this.restTemplate.getForObject(this.loginUrl, UserBoundary.class, user.getUserId().getDomain(),
//				user.getUserId().getEmail()).getUsername()).isEqualTo(changedUser.getUsername());
//	}
//
//	@Test
//	public void testPutWithDatabaseContainigUserAndChangingItsAvatarChangedTheResultAvatar() throws Exception {
//		// GIVEN the database contains a single user with avatar
//		UserBoundary user = this.restTemplate.postForObject(this.createUserUrl,
//				new NewUserDetails("test@gmail.com", UserRole.PLAYER, "user", "Avatar"), UserBoundary.class);
//
//		// WHEN I PUT new avatar
//		UserBoundary changedUser = new UserBoundary(null, UserRole.ADMIN, "user", "newAvatar");
//		this.restTemplate.put(this.updateUserUrl, changedUser, user.getUserId().getDomain(),
//				user.getUserId().getEmail());
//
//		// THEN the database is updated the user with the new avatar
//		assertThat(this.restTemplate.getForObject(this.loginUrl, UserBoundary.class, user.getUserId().getDomain(),
//				user.getUserId().getEmail()).getAvatar()).isEqualTo(changedUser.getAvatar());
//	}
//	
//	
//	@Test
//	public void testPutWithDatabaseContainigUserAndChangingItsUserIdDomainDoesNotChangedTheResultUserIdDomain()
//			throws Exception {
//		// GIVEN the database contains a single user
//		UserBoundary user = this.restTemplate.postForObject(this.createUserUrl,
//				new NewUserDetails("test@gmail.com", UserRole.PLAYER, "user", "Avatar"), UserBoundary.class);
//
//		// WHEN I PUT new domain
//		UserBoundary changedUser = new UserBoundary(new UserId("xxxx.xxxx.xxxx", user.getUserId().getEmail()),
//				UserRole.ADMIN, "user", "Avatar");
//		this.restTemplate.put(this.updateUserUrl, changedUser, user.getUserId().getDomain(),
//				user.getUserId().getEmail());
//
//		// THEN the database is not updated the user domain
//		assertThat(this.restTemplate.getForObject(this.loginUrl, UserBoundary.class, user.getUserId().getDomain(),
//				user.getUserId().getEmail()).getUserId().getDomain()).isEqualTo(user.getUserId().getDomain());
//	}
//
//	@Test
//	public void testPutWithDatabaseContainigUserAndChangingItsUserIdEmailDoesNotChangedTheResultUserIdEmail()
//			throws Exception {
//		// GIVEN the database contains a single user
//		UserBoundary user = this.restTemplate.postForObject(this.createUserUrl,
//				new NewUserDetails("test@gmail.com", UserRole.PLAYER, "user", "Avatar"), UserBoundary.class);
//
//		// WHEN I PUT new email
//		UserBoundary changedUser = new UserBoundary(new UserId(user.getUserId().getDomain(), "xxxx@xxxx.com"),
//				UserRole.ADMIN, "user", "Avatar");
//		this.restTemplate.put(this.updateUserUrl, changedUser, user.getUserId().getDomain(),
//				user.getUserId().getEmail());
//
//		// THEN the database is not updated the user email
//		assertThat(this.restTemplate.getForObject(this.loginUrl, UserBoundary.class, user.getUserId().getDomain(),
//				user.getUserId().getEmail()).getUserId().getEmail()).isEqualTo(user.getUserId().getEmail());
//	}
//	
//	@Test
//	public void testPutWithDatabaseContainigUserWuthNullAvatarDoesNotChangedTheResultUserIdEmail()
//			throws Exception {
//		// GIVEN the database contains a single user
//		UserBoundary user = this.restTemplate.postForObject(this.createUserUrl,
//				new NewUserDetails("test@gmail.com", UserRole.PLAYER, "user", "Avatar"), UserBoundary.class);
//
//		// WHEN I PUT new email
//		UserBoundary changedUser = new UserBoundary(new UserId(user.getUserId().getDomain(), "test@gmail.com"),
//				UserRole.ADMIN, "user", null);
//		this.restTemplate.put(this.updateUserUrl, changedUser, user.getUserId().getDomain(),
//				user.getUserId().getEmail());
//
//		// THEN the database is not updated the user email
//		assertThat(this.restTemplate.getForObject(this.loginUrl, UserBoundary.class, user.getUserId().getDomain(),
//				user.getUserId().getEmail()).getAvatar()).isEqualTo(user.getAvatar());
//	}
//	
//	@Test
//	public void testPutWithDatabaseContainigUserWuthEmptyStringAvatarDoesNotChangedTheResultUserIdEmail()
//			throws Exception {
//		// GIVEN the database contains a single user
//		UserBoundary user = this.restTemplate.postForObject(this.createUserUrl,
//				new NewUserDetails("test@gmail.com", UserRole.PLAYER, "user", "Avatar"), UserBoundary.class);
//
//		// WHEN I PUT new email
//		UserBoundary changedUser = new UserBoundary(new UserId(user.getUserId().getDomain(), "test@gmail.com"),
//				UserRole.ADMIN, "user", "");
//		this.restTemplate.put(this.updateUserUrl, changedUser, user.getUserId().getDomain(),
//				user.getUserId().getEmail());
//
//		// THEN the database is not updated the user email
//		assertThat(this.restTemplate.getForObject(this.loginUrl, UserBoundary.class, user.getUserId().getDomain(),
//				user.getUserId().getEmail()).getAvatar()).isEqualTo(user.getAvatar());
//	}
//
//
//	@Test
//	public void testPutWithEmptyDatabaseChangingAUserReturnsStatusDifferentThan2xx() throws Exception {
//		// GIVEN the database is empty
//		// do nothing
//
//		// WHEN I PUT /acs/users/{userDomain}/{userEmail} and send regular user boundary
//		// THEN the server responds with status != 2xx
//
//		assertThrows(Exception.class,
//				() -> this.restTemplate.put(this.updateUserUrl,
//						new UserBoundary(null, UserRole.PLAYER, "mor", "avatar"), UserBoundary.class,
//						"2020b.lior.trachtman", "test@gmail.com"));
//	}
//
//	@Test
//	public void testPutWith1UserInDatabaseChangingANonExistingUserReturnsStatusDifferentThan2xx() throws Exception {
//		// GIVEN the database is empty
//		// do nothing
//
//		// WHEN I PUT /acs/users/{userDomain}/{userEmail} and send regular user boundary
//		// THEN the server responds with status != 2xx
//
//		UserBoundary user = this.restTemplate.postForObject(this.createUserUrl,
//				new NewUserDetails("test@gmail.com", UserRole.PLAYER, "user", "Avatar"), UserBoundary.class);
//
//		assertThrows(Exception.class,
//				() -> this.restTemplate.put(this.updateUserUrl,
//						new UserBoundary(null, UserRole.PLAYER, "mor", "avatar"), UserBoundary.class,
//						"2020b.lior.trachtman", "morsof48@gmail.com"));
//	}
}
