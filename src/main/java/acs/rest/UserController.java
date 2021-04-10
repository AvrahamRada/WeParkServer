package acs.rest;


import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import acs.boundaries.UserBoundary;
import acs.logic.UserService;
import acs.logic.util.AwsS3;
//import acs.logic.UserService;
import acs.util.NewUserDetails;
import acs.util.UserId;

@RestController
public class UserController {
	private UserService userService;
//	private AwsS3 amazonAWS = new AwsS3();
	
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostConstruct
	public void init() {
		
//		this.amazonAWS.deleteBucket("testavrahamweparkbucket");
//		this.amazonAWS.listOfBuckets();
//		this.amazonAWS.uploadFile("avrahamwepark", "tavrahamrada.csv", "D:\\test.csv");
		
//		this.amazonAWS.downloadCSV("Afeka.csv");
//		this.amazonAWS.downloadCSV("Beach.csv");
//		this.amazonAWS.downloadCSV("Evening_entertainment.csv");
//		this.amazonAWS.downloadCSV("Food_area.csv");
//		this.amazonAWS.downloadCSV("Ichilov_Hospital.csv");
//		this.amazonAWS.downloadCSV("Neighborhood.csv");
//		this.amazonAWS.downloadCSV("School_area.csv");
//		this.amazonAWS.downloadCSV("Shopping_complex.csv");
//		this.amazonAWS.downloadCSV("Work_area.csv");
		
		
		
//		this.amazonAWS.saveCSVToData("Afeka.csv");
//		this.amazonAWS.saveCSVToData("Beach.csv");
//		this.amazonAWS.saveCSVToData("Evening_entertainment.csv");
//		this.amazonAWS.saveCSVToData("Food_area.csv");
//		this.amazonAWS.saveCSVToData("Ichilov_Hospital.csv");
//		this.amazonAWS.saveCSVToData("Neighborhood.csv");
//		this.amazonAWS.saveCSVToData("School_area.csv");
//		this.amazonAWS.saveCSVToData("Shopping_complex.csv");
//		this.amazonAWS.saveCSVToData("Work_area.csv");
	}
	
	/*--------------------- GET APIS ------------------- */

	// Login user get request
	@RequestMapping(path = "/acs/users/login/{userDomain}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary login(@PathVariable("userDomain") String userDomain, @PathVariable("userEmail") String userEmail) {
		return userService.login(userDomain, userEmail);
	}
	
	/*--------------------- POST APIS ------------------- */

	// Create a new user
	@RequestMapping(path = "/acs/users",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createNewUser(@RequestBody NewUserDetails input) {
		return userService.createUser(new UserBoundary(new UserId("", input.getEmail()), input.getRole(), input.getUsername(),input.getLicensePlate()));

	}
	
	/*--------------------- PUT APIS ------------------- */
	
	//Update user details
	@RequestMapping(path = "/acs/users/{userDomain}/{userEmail}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUserDetails(@PathVariable("userDomain") String userDomain, 
			@PathVariable("userEmail") String userEmail, @RequestBody UserBoundary update) {
		userService.updateUser(userDomain, userEmail, update);
	}
}