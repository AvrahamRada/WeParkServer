package acs.logic;

import java.util.Collection;
import java.util.List;

import acs.boundaries.ElementBoundary;
import acs.boundaries.ElementIdBoundary;

public interface ElementService {
	
	public ElementBoundary create(String managerDomain, String managerEmail, ElementBoundary element);
	public ElementBoundary update(String managerDomain, String managerEmail, String elementDomain, String elementId, ElementBoundary update);
	public List<ElementBoundary> getAll(String userDomain, String userEmail);
	public ElementBoundary getSpecificElement(String userDomain, String userEmail, String elementDomain, String elementId);
	public void deleteAllElements(String adminDomain, String adminEmail);
	public void bindParentElementToChildElement(String managerDomain, String managerEmail, String elementDomain, String elementId, ElementIdBoundary input);
	public Collection<ElementBoundary> getAllChildrenElements(String userDomain,String userEmail,String elementDomain,String elementId, int size, int page);
	public Collection<ElementBoundary> getAllOriginsElements(String userDomain,String userEmail,String elementDomain,String elementId, int size, int page);
	public List<ElementBoundary> getAll(String userDomain, String userEmail, int size, int page);
	public List<ElementBoundary> getAllElementsByName(String userDomain,String userEmail,String name, int size, int page);
	public List<ElementBoundary> getAllElementsByType(String userDomain,String userEmail,String type, int size, int page);
	public List<ElementBoundary> getAllElementsByLocation(String userDomain,String userEmail,String lat, String lng, String distance, int size, int page);
	
}
