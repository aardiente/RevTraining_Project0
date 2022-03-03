package com.training.pms.DAO;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.training.pms.Models.UserAccount;
import com.training.pms.Utility.DBConnection;

class UserAccountDAOImplTest
{
	static UserAccountDAO dao = null;
	static UserAccount after = null;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception 
	{
		DBConnection.initConnection();
		dao = new UserAccountDAOImpl();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception 
	{
		DBConnection.closeConnection();
	}


	@Test
	void testAddUserAccount() 
	{
		UserAccount beforeAdd = new UserAccount(-1, "ClassTest", "Password", "fName", "lName");
		after = dao.addUserAccount(beforeAdd);
		
		assertTrue(after.getUsername().equals(beforeAdd.getUsername()));
	}


	@Test
	void testUpdateUserAccount() 
	{
		dao.updateUserAccount(after.getAccountId(), "Class", "Test");
		after = dao.searchByUserAccountName("ClassTest");
		
		assertTrue(after.getFirstName().equals("Class") && after.getLastName().equals("Test"));
	}

	@Test
	void testSearchByUserAccountName() 
	{
		assertTrue(dao.searchByUserAccountName("ClassTest").getUsername().equals("ClassTest"));
	}

	@Test
	void testIsEmployee() 
	{
		assertFalse(after.isEmployee()); // We never marked this account as an Employee
	}

	@Test
	void testVerifyLogin()
	{
		assertFalse(dao.verifyLogin(after.getUsername(), after.getPassword())); // We didn't activate the account
	}

	@Test
	void testUpdateApprovalStatus() 
	{
		assertTrue(dao.updateApprovalStatus(after.getUsername()));
	}
	@Test
	void testDeleteUserAccount()
	{
		assertTrue(dao.deleteUserAccount(after));
	}


}
