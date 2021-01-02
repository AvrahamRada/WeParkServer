package acs.element;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
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
import acs.boundaries.ElementIdBoundary;
import acs.boundaries.UserBoundary;
import acs.util.CreatedBy;
import acs.util.ElementId;
import acs.util.Location;
import acs.util.TestUtil;
import acs.util.UserId;
import acs.util.UserRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ElementPUTTests {
//	
//
//	private int port;
//	private String url;
//	private RestTemplate restTemplate;
//
//	
//	@LocalServerPort
//	public void setPort(int port) {
//		this.port = port;
//	}
//	
//	@PostConstruct
//	public void init() {
//		this.url = "http://localhost:" + port + "/acs/elements";	
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
//	public void testBindParentToChild() throws Exception{
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//		
//		// GIVEN the database contains a elementDomain 2020b.lior.trachtman with generated id
//		ElementBoundary elementParent = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "x"), "type", "name", true,
//						new Date(System.currentTimeMillis()),
//						new CreatedBy(new UserId("2020b.lior.trachtman", "sarel.micha@s.afeka.ac.il")),
//						new Location(40.730610, -73.935242), new HashMap<>()),
//				ElementBoundary.class,manager.getUserId().getDomain(), manager.getUserId().getEmail());
//		
//		
//		// GIVEN the database contains a elementDomain 2020b.lior.trachtman with generated id
//		ElementBoundary elementChild = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "y"), "type", "name", true,
//						new Date(System.currentTimeMillis()),
//						new CreatedBy(new UserId("2020b.lior.trachtman", "test.micha@s.afeka.ac.il")),
//						new Location(40.730610, 73.935242), new HashMap<>()),
//				ElementBoundary.class,manager.getUserId().getDomain(), manager.getUserId().getEmail());
//			
//		this.restTemplate.put(this.url + "/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}/children",
//				new ElementIdBoundary(elementChild.getElementId().getDomain(), elementChild.getElementId().getId()),
//				elementParent.getCreatedBy().getUserId().getDomain(),elementParent.getCreatedBy().getUserId().getEmail(),
//				elementParent.getElementId().getDomain(),elementParent.getElementId().getId());
//		
//		ElementBoundary[] ElementsArray = this.restTemplate.getForObject(this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}/parents?page={page}&size={size}",
//				ElementBoundary[].class, 
//				elementChild.getCreatedBy().getUserId().getDomain(), elementChild.getCreatedBy().getUserId().getEmail(),
//				elementChild.getElementId().getDomain(),elementChild.getElementId().getId(),0,1);
//		
//		assertThat(ElementsArray[0]).usingRecursiveComparison().isEqualTo(elementParent);
//	}
//	
//	@Test
//	public void testBindChildToParent() throws Exception{
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//		
//		// GIVEN the database contains a elementDomain 2020b.lior.trachtman with generated id
//		ElementBoundary elementParent = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "x"), "type", "name", true,
//						new Date(System.currentTimeMillis()),
//						new CreatedBy(new UserId("2020b.lior.trachtman", "sarel.micha@s.afeka.ac.il")),
//						new Location(40.730610, -73.935242), new HashMap<>()),
//				ElementBoundary.class,manager.getUserId().getDomain(), manager.getUserId().getEmail());
//		
//		
//		// GIVEN the database contains a elementDomain 2020b.lior.trachtman with generated id
//		ElementBoundary elementChild = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "y"), "type", "name", true,
//						new Date(System.currentTimeMillis()),
//						new CreatedBy(new UserId("2020b.lior.trachtman", "test.micha@s.afeka.ac.il")),
//						new Location(40.730610, 73.935242), new HashMap<>()),
//				ElementBoundary.class,manager.getUserId().getDomain(), manager.getUserId().getEmail());
//			
//		this.restTemplate.put(this.url + "/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}/children",
//				new ElementIdBoundary(elementChild.getElementId().getDomain(), elementChild.getElementId().getId()),
//				elementParent.getCreatedBy().getUserId().getDomain(),elementParent.getCreatedBy().getUserId().getEmail(),
//				elementParent.getElementId().getDomain(),elementParent.getElementId().getId());
//		
//		ElementBoundary[] ElementsArray = this.restTemplate.getForObject(this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}/children?page={page}&size={size}",
//				ElementBoundary[].class, 
//				elementParent.getCreatedBy().getUserId().getDomain(), elementParent.getCreatedBy().getUserId().getEmail(),
//				elementParent.getElementId().getDomain(),elementParent.getElementId().getId(),0,1);
//		
//		assertThat(ElementsArray[0]).usingRecursiveComparison().isEqualTo(elementChild);
//	}
//	
//	@Test
//	public void testPutSingleElementWithDatabaseAndUpdateTypeReturnsASpecificUpdatedElement() throws Exception {
//
//		// GIVEN the database contains a elementDomain 2020b.lior.trachtman with
//		// generated id
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//
//		ElementBoundary element = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "x"), "type", "name", true,
//						new Date(System.currentTimeMillis()),
//						new CreatedBy(new UserId("2020b.lior.trachtman", "sarel.micha@s.afeka.ac.il")),
//						new Location(40.730610, -73.935242), new HashMap<>()),
//				
//				ElementBoundary.class,manager.getUserId().getDomain(), manager.getUserId().getEmail());
//		
//		ElementBoundary newElement = new ElementBoundary(new ElementId("2020b.lior.trachtman", "anyId"), "type1", "name", true,
//				new Date(System.currentTimeMillis()),
//				new CreatedBy(new UserId("2020b.lior.trachtman", "sarel.micha@s.afeka.ac.il")),
//				new Location(40.730610, -73.935242), new HashMap<>());
//
//		// THEN the server returns a element boundary with elementDomain :
//		// 2020b.lior.trachtman AND id: x
//		this.restTemplate.put(this.url + "/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}", newElement, element.getCreatedBy().getUserId().getDomain(),
//				element.getCreatedBy().getUserId().getEmail(),element.getElementId().getDomain(),element.getElementId().getId());
//		
//		assertThat(this.restTemplate.getForObject(this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}?page={page}&size={size}", ElementBoundary.class,
//				element.getCreatedBy().getUserId().getDomain(),
//				element.getCreatedBy().getUserId().getEmail(),element.getElementId().getDomain(),element.getElementId().getId(),0,1).getType())
//		.isEqualTo(newElement.getType());
//		
//		}
//	
//	@Test
//	public void testPutSingleElementWithDatabaseAndUpdateNameReturnsASpecificUpdatedElement() throws Exception {
//
//		// GIVEN the database contains a elementDomain 2020b.lior.trachtman with
//		// generated id
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//
//		ElementBoundary element = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "x"), "type", "name", true,
//						new Date(System.currentTimeMillis()),
//						new CreatedBy(new UserId("2020b.lior.trachtman", "sarel.micha@s.afeka.ac.il")),
//						new Location(40.730610, -73.935242), new HashMap<>()),
//				
//				ElementBoundary.class,manager.getUserId().getDomain(), manager.getUserId().getEmail());
//		
//		ElementBoundary newElement = new ElementBoundary(new ElementId("2020b.lior.trachtman", "anyId"), "type", "anotherName", true,
//				new Date(System.currentTimeMillis()),
//				new CreatedBy(new UserId("2020b.lior.trachtman", "sarel.micha@s.afeka.ac.il")),
//				new Location(40.730610, -73.935242), new HashMap<>());
//	
//		this.restTemplate.put(this.url + "/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}", newElement, element.getCreatedBy().getUserId().getDomain(),
//				element.getCreatedBy().getUserId().getEmail(),element.getElementId().getDomain(),element.getElementId().getId());
//		
//		assertThat(this.restTemplate.getForObject(this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}?page={page}&size={size}", ElementBoundary.class,
//				element.getCreatedBy().getUserId().getDomain(),
//				element.getCreatedBy().getUserId().getEmail(),element.getElementId().getDomain(),element.getElementId().getId(),0,1).getName())
//		.isEqualTo(newElement.getName());
//		
//		}
//	
//	@Test
//	public void testPutSingleElementWithDatabaseAndUpdateLocationReturnsASpecificUpdatedElement() throws Exception {
//
//		// GIVEN the database contains a elementDomain 2020b.lior.trachtman with
//		// generated id
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//
//		ElementBoundary element = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "x"), "type", "name", true,
//						new Date(System.currentTimeMillis()),
//						new CreatedBy(new UserId("2020b.lior.trachtman", "sarel.micha@s.afeka.ac.il")),
//						new Location(40.730610, -73.935242), new HashMap<>()),
//				
//				ElementBoundary.class,manager.getUserId().getDomain(), manager.getUserId().getEmail());
//		
//		ElementBoundary newElement = new ElementBoundary(new ElementId("2020b.lior.trachtman", "anyId"), "type", "name", true,
//				new Date(System.currentTimeMillis()),
//				new CreatedBy(new UserId("2020b.lior.trachtman", "sarel.micha@s.afeka.ac.il")),
//				new Location(50.730610, -22.935242), new HashMap<>());
//	
//		this.restTemplate.put(this.url + "/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}", newElement, element.getCreatedBy().getUserId().getDomain(),
//				element.getCreatedBy().getUserId().getEmail(),element.getElementId().getDomain(),element.getElementId().getId());
//		
//		assertThat(this.restTemplate.getForObject(this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}?page={page}&size={size}", ElementBoundary.class,
//				element.getCreatedBy().getUserId().getDomain(),
//				element.getCreatedBy().getUserId().getEmail(),element.getElementId().getDomain(),element.getElementId().getId(),0,1).getLocation().getLat())
//		.isEqualTo(newElement.getLocation().getLat());
//		
//		assertThat(this.restTemplate.getForObject(this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}?page={page}&size={size}", ElementBoundary.class,
//				element.getCreatedBy().getUserId().getDomain(),
//				element.getCreatedBy().getUserId().getEmail(),element.getElementId().getDomain(),element.getElementId().getId(),0,1).getLocation().getLng())
//		.isEqualTo(newElement.getLocation().getLng());
//		
//		}
//
//	@Test
//	public void testPutSingleElementWithDatabaseAndUpdateActiveReturnsASpecificUpdatedElement() throws Exception {
//
//		// GIVEN the database contains a elementDomain 2020b.lior.trachtman with
//		// generated id
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//
//		ElementBoundary element = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "x"), "type", "name", true,
//						new Date(System.currentTimeMillis()),
//						new CreatedBy(new UserId("2020b.lior.trachtman", "sarel.micha@s.afeka.ac.il")),
//						new Location(40.730610, -73.935242), new HashMap<>()),
//				
//				ElementBoundary.class,manager.getUserId().getDomain(), manager.getUserId().getEmail());
//		
//		ElementBoundary newElement = new ElementBoundary(new ElementId("2020b.lior.trachtman", "anyId"), "type", "name", false,
//				new Date(System.currentTimeMillis()),
//				new CreatedBy(new UserId("2020b.lior.trachtman", "sarel.micha@s.afeka.ac.il")),
//				new Location(40.730610, -73.935242), new HashMap<>());
//		
//		this.restTemplate.put(this.url + "/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}", newElement, element.getCreatedBy().getUserId().getDomain(),
//				element.getCreatedBy().getUserId().getEmail(),element.getElementId().getDomain(),element.getElementId().getId());
//		
//		assertThat(this.restTemplate.getForObject(this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}?page={page}&size={size}", ElementBoundary.class,
//				element.getCreatedBy().getUserId().getDomain(),
//				element.getCreatedBy().getUserId().getEmail(),element.getElementId().getDomain(),element.getElementId().getId(),0,1).getActive())
//		.isEqualTo(newElement.getActive());
//		
//		}
//	
//	@Test
//	public void testPutSingleElementWithUserThatIsNotManager() throws Exception {
//
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//		// create Player and Admin
//		UserBoundary user = TestUtil.createNewUserByChoice(UserRole.PLAYER,port);
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
//
//		ElementBoundary element = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "x"), "type", "name", true,
//						new Date(System.currentTimeMillis()),
//						new CreatedBy(new UserId("2020b.lior.trachtman", "sarel.micha@s.afeka.ac.il")),
//						new Location(40.730610, -73.935242), new HashMap<>()),
//				
//				ElementBoundary.class,manager.getUserId().getDomain(), manager.getUserId().getEmail());
//		
//		ElementBoundary newElement = new ElementBoundary(new ElementId("2020b.lior.trachtman", "anyId"), "type1", "name", true,
//				new Date(System.currentTimeMillis()),
//				new CreatedBy(new UserId("2020b.lior.trachtman", "sarel.micha@s.afeka.ac.il")),
//				new Location(40.730610, -73.935242), new HashMap<>());
//
//		assertThrows(Exception.class, 
//				()-> this.restTemplate.put(this.url + "/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}", 
//						newElement,user.getUserId().getDomain(),user.getUserId().getEmail(),
//						element.getElementId().getDomain(),element.getElementId().getId()));
//		
//		assertThrows(Exception.class, 
//				()-> this.restTemplate.put(this.url + "/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}", 
//						newElement,admin.getUserId().getDomain(),admin.getUserId().getEmail(),
//						element.getElementId().getDomain(),element.getElementId().getId()));
//		
//		
//		}
//	
//	@Test
//	public void testBindParentToChildWithUserThatIsNotManager() throws Exception{
//		// create manager
//		UserBoundary manager = TestUtil.createNewUserByChoice(UserRole.MANAGER,port);
//		// create Player and Admin
//		UserBoundary user = TestUtil.createNewUserByChoice(UserRole.PLAYER,port);
//		UserBoundary admin = TestUtil.createNewUserByChoice(UserRole.ADMIN,port);
//		
//		// GIVEN the database contains a elementDomain 2020b.lior.trachtman with generated id
//		ElementBoundary elementParent = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "x"), "type", "name", true,
//						new Date(System.currentTimeMillis()),
//						new CreatedBy(new UserId("2020b.lior.trachtman", "sarel.micha@s.afeka.ac.il")),
//						new Location(40.730610, -73.935242), new HashMap<>()),
//				ElementBoundary.class,manager.getUserId().getDomain(), manager.getUserId().getEmail());
//		
//		
//		// GIVEN the database contains a elementDomain 2020b.lior.trachtman with generated id
//		ElementBoundary elementChild = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
//				new ElementBoundary(new ElementId("2020b.lior.trachtman", "y"), "type", "name", true,
//						new Date(System.currentTimeMillis()),
//						new CreatedBy(new UserId("2020b.lior.trachtman", "test.micha@s.afeka.ac.il")),
//						new Location(40.730610, 73.935242), new HashMap<>()),
//				ElementBoundary.class,manager.getUserId().getDomain(), manager.getUserId().getEmail());
//		
//		assertThrows(Exception.class, 
//				()-> this.restTemplate.put(this.url + "/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}/children", 
//						new ElementIdBoundary(elementChild.getElementId().getDomain(), elementChild.getElementId().getId()),
//						user.getUserId().getDomain(),user.getUserId().getEmail(),
//						elementParent.getElementId().getDomain(),elementParent.getElementId().getId()));
//		
//		assertThrows(Exception.class, 
//				()-> this.restTemplate.put(this.url + "/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}/children", 
//						new ElementIdBoundary(elementChild.getElementId().getDomain(), elementChild.getElementId().getId()),
//						admin.getUserId().getDomain(),admin.getUserId().getEmail(),
//						elementParent.getElementId().getDomain(),elementParent.getElementId().getId()));
//
//	}
//	
}
