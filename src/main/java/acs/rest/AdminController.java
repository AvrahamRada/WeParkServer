package acs.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import acs.boundaries.ActionBoundary;
import acs.boundaries.UserBoundary;
//import acs.logic.ActionService;
//import acs.logic.ElementService;
//import acs.logic.EnhancedActionService;
//import acs.logic.EnhancedElementService;
//import acs.logic.EnhancedUserService;
//import acs.logic.UserService;

@RestController
public class AdminController {

//	private EnhancedUserService enhancedUserService;
//	private EnhancedElementService enhancedElementService;
//	private EnhancedActionService  enhancedActionService;
//
//	@Autowired
//	public AdminController(EnhancedUserService userService, EnhancedElementService elementService, EnhancedActionService actionService) {
//		this.enhancedUserService = userService;
//		this.enhancedElementService = elementService;
//		this.enhancedActionService = actionService;
//
//	}

	/*--------------------- GET all users APIS ------------------- */

	@RequestMapping(path = "/acs/admin/users/{adminDomain}/{adminEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] getAllUsers(@PathVariable("adminDomain") String adminDomain,
			@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
//		return this.enhancedUserService.getAllUsers(adminDomain, adminEmail, size, page).toArray(new UserBoundary[0]);
		return new UserBoundary[] {};
	}

	/*--------------------- GET all actions APIS ------------------- */

	@RequestMapping(path = "/acs/admin/actions/{adminDomain}/{adminEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary[] getAllActions(@PathVariable("adminDomain") String adminDomain,
			@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
//		return this.enhancedActionService.getAllActions(adminDomain, adminEmail, size, page).toArray(new ActionBoundary[0]);
		return new ActionBoundary[] {};

	}

	/*--------------------- DELETE APIS ------------------- */

	@RequestMapping(path = "/acs/admin/actions/{adminDomain}/{adminEmail}", method = RequestMethod.DELETE)
	public void deleteAllActions(@PathVariable("adminDomain") String adminDomain,
			@PathVariable("adminEmail") String adminEmail) {
//		this.enhancedActionService.deleteAllActions(adminDomain, adminEmail);
	}

	@RequestMapping(path = "/acs/admin/elements/{adminDomain}/{adminEmail}", method = RequestMethod.DELETE)
	public void deleteAllElements(@PathVariable("adminDomain") String adminDomain,
			@PathVariable("adminEmail") String adminEmail) {

//		this.enhancedElementService.deleteAllElements(adminDomain, adminEmail);
	}

	
	// delete all user also admin!!
	@RequestMapping(path = "/acs/admin/users/{adminDomain}/{adminEmail}", method = RequestMethod.DELETE)
	public void deleteAllUsers(@PathVariable("adminDomain") String adminDomain,
			@PathVariable("adminEmail") String adminEmail) {
		
//		this.enhancedUserService.deleteAllUsers(adminDomain, adminEmail);
	}

}