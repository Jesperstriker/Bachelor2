package jUnit;

import static org.junit.Assert.*;

import org.junit.Test;

import model.Role;

public class RoleTest {

	@Test
	public void contains() {
		Role employee = new Role("employee",null);
		Role janitor = new Role("janitor",employee);
		Role admin = new Role("admin",janitor);
		
		assertTrue(admin.contains(janitor));
		assertTrue(admin.contains(employee));
		assertTrue(janitor.contains(employee));
		
		assertFalse(employee.contains(admin));
		assertFalse(employee.contains(janitor));
		assertFalse(janitor.contains(admin));
		
	}

}
