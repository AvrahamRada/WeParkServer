package acs.util;

import java.util.Date;
import java.util.HashMap;
import java.util.stream.IntStream;

import org.springframework.web.client.RestTemplate;

import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;

public class TestUtil {
//	
//
//	public static void clearDB(int port) {
//
//		// User URL
//		String createUserUrl = "http://localhost:" + port + "/acs/users";
//
//		// Admin URL
//		String deleteElementsUrl = "http://localhost:" + port + "/acs/admin/elements/{adminDomain}/{adminEmail}";
//		String deleteUsersUrl = "http://localhost:" + port + "/acs/admin/users/{adminDomain}/{adminEmail}";
//		String deleteActionsUrl = "http://localhost:" + port + "/acs/admin/actions/{adminDomain}/{adminEmail}";
//
//		RestTemplate restTemplate = new RestTemplate();
//
//		// Create admin for clear DB
//		UserBoundary admin = restTemplate.postForObject(createUserUrl,
//				new NewUserDetails("adminspecial@gmail.com", UserRole.ADMIN, "Admin", "Avatar"), UserBoundary.class);
//		
//		// Delete all elements from DB
//		restTemplate.delete(deleteElementsUrl, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//
//		// Delete all actions from DB
//		restTemplate.delete(deleteActionsUrl, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//		
//		// Delete all users from DB
//		restTemplate.delete(deleteUsersUrl, admin.getUserId().getDomain(), admin.getUserId().getEmail());
//
//	}
//	
//	
//	// helpful methods
//		public static UserBoundary createNewUserByChoice(UserRole role,int port) {
//			String createUserUrl = "http://localhost:" + port + "/acs/users";
//			RestTemplate restTemplate = new RestTemplate();
//			UserBoundary user = null;
//			if (role == UserRole.ADMIN) {
//				user = restTemplate.postForObject(createUserUrl,
//						new NewUserDetails("admin@gmail.com", UserRole.ADMIN, "Admin", "Avatar"), UserBoundary.class);
//			} else if (role == UserRole.MANAGER) {
//				user = restTemplate.postForObject(createUserUrl,
//						new NewUserDetails("manager@gmail.com", UserRole.MANAGER, "manager", "Avatar"), UserBoundary.class);
//			} else {
//				user = restTemplate.postForObject(createUserUrl,
//						new NewUserDetails("player@gmail.com", UserRole.PLAYER, "player", "Avatar"), UserBoundary.class);
//			}
//			return user;
//		}
//
//		public static void createActionsByX(int x, UserBoundary userBoundary, ElementBoundary[] elementBoundary, int port) {
//			String invokeActionUrl = "http://localhost:" + port + "/acs/actions";
//			RestTemplate restTemplate = new RestTemplate();
//			IntStream.range(0, x).forEach(i -> restTemplate.postForObject(invokeActionUrl,
//					new ActionBoundary(null, "type", new Element(elementBoundary[i].getElementId()),
//							new Date(System.currentTimeMillis()), new InvokedBy(userBoundary.getUserId()), new HashMap<>()),
//					ActionBoundary.class, "2020b.lior.trachtman", "don't care"));
//		}
//
//		public static void createElementsByX(int x, UserBoundary userBoundary, int port) {
//			String createElementUrl = "http://localhost:" + port + "/acs/elements/{managerDomain}/{managerEmail}";
//			RestTemplate restTemplate = new RestTemplate();
//			IntStream.range(0, x)
//					.forEach(i -> restTemplate.postForObject(createElementUrl,
//							new ElementBoundary(null, "type", "mor", true, null, new CreatedBy(userBoundary.getUserId()),
//									new Location(32.11111, 33.11111), new HashMap<String, Object>()),
//							ElementBoundary.class, userBoundary.getUserId().getDomain(),
//							userBoundary.getUserId().getEmail()));
//
//		}
//
//		public static void createUsersByX(int x, UserRole role, int port) {
//			String createUserUrl = "http://localhost:" + port + "/acs/users";
//			RestTemplate restTemplate = new RestTemplate();
//			IntStream.range(0, x).forEach(i -> restTemplate.postForObject(createUserUrl,
//					new NewUserDetails("user" + i + "@gmail.com", role, "user", "(=)"), UserBoundary.class));
//		}

}
