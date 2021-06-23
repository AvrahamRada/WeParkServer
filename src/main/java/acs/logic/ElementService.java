package acs.logic;

import java.util.List;
import acs.boundaries.ElementBoundary;
import acs.logic.util.QueueingTheory;

public interface ElementService {

	public ElementBoundary create(String managerDomain, String managerEmail, ElementBoundary element,
			QueueingTheory queueingTheory);

	public ElementBoundary createWithoutSaving(String managerDomain, String managerEmail, ElementBoundary element,
			QueueingTheory queueingTheory);

	public ElementBoundary update(String managerDomain, String managerEmail, String elementDomain, String elementId,
			ElementBoundary update);

	public List<ElementBoundary> getAll(String userDomain, String userEmail);

	public ElementBoundary getSpecificElement(String userDomain, String userEmail, String elementDomain,
			String elementId);

	public void deleteAllElements(String adminDomain, String adminEmail);

	public List<ElementBoundary> getAll(String userDomain, String userEmail, int size, int page);

	public List<ElementBoundary> getAllElementsByName(String userDomain, String userEmail, String name, int size,
			int page);

	public List<ElementBoundary> getAllElementsByType(String userDomain, String userEmail, String type, int size,
			int page);
}
