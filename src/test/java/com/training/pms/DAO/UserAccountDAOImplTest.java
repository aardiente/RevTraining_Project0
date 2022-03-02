package com.training.pms.DAO;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.training.pms.Models.UserAccount;
import com.training.pms.Utility.DBConnection;

class UserAccountDAOImplTest
{
	private static Connection con = null;
	private static UserAccountDAO dao = new UserAccountDAOImpl();
	private static UserAccount userAfterAdd = null;
	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
		con = DBConnection.getConnection();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception 
	{
		con.close();
		con = null;
	}

	@BeforeEach
	void setUp() throws Exception
	{
		
	}

	@AfterEach
	void tearDown() throws Exception 
	{
	}

	@Test
	void testAddUserAccount() 
	{
		UserAccount temp = new UserAccount(-1, "Test Case 1", "I do things", "FName", "LName");   // -1 because the db assigns its Id, this is just a temp refence to create a real object
		userAfterAdd = dao.addUserAccount(temp);
		
		assertEquals( true, ( temp.getUsername().equals(userAfterAdd.getUsername()) ) ? true : false ); // checking if the usernames match, since its a unique column it should return the same string
	}

	@Test
	void testDeleteUserAccount() 
	{
		assertEquals( true, dao.deleteUserAccount(userAfterAdd) );
	}

	@Test
	void testUpdateUserAccount() 
	{
		//dao.updateUserAccount(userAfterAdd.getAccountId() , "Bob", "Dole");
		
		
	}

	@Test
	void testSearchByUserAccountName() {
		fail("Not yet implemented");
	}

	@Test
	void testIsEmployee() {
		fail("Not yet implemented");
	}

	@Test
	void testVerifyLogin() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateApprovalStatus() {
		fail("Not yet implemented");
	}

}
