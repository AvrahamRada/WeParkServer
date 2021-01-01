package acs.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import acs.boundaries.UserBoundary;
//import acs.logic.UserService;
import acs.util.NewUserDetails;
import acs.util.UserId;

@RestController
public class UserController {
//	private UserService userService;
//	
//	@Autowired
//	public UserController(UserService userService) {
//		this.userService = userService;
//	}
	
	/*--------------------- GET APIS ------------------- */

	//Login user get request
	@RequestMapping(path = "/acs/users/login/{userDomain}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary login(@PathVariable("userDomain") String userDomain, @PathVariable("userEmail") String userEmail) {
//		return userService.login(userDomain, userEmail);
		return new UserBoundary();
	}
	
	/*--------------------- POST APIS ------------------- */

	//Create a new user
	@RequestMapping(path = "/acs/users",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createNewUser(@RequestBody NewUserDetails input) {
//		return userService.createUser(new UserBoundary(new UserId(" ", input.getEmail()), input.getRole(), input.getUsername(), input.getAvatar()));
		return new UserBoundary();
	}
	
	/*--------------------- PUT APIS ------------------- */
	
	//Update user details
	@RequestMapping(path = "/acs/users/{userDomain}/{userEmail}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUserDetails(@PathVariable("userDomain") String userDomain, 
			@PathVariable("userEmail") String userEmail, @RequestBody UserBoundary update) {
//		userService.updateUser(userDomain, userEmail, update);
	}
	
}