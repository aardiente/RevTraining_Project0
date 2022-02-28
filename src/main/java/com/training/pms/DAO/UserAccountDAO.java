package com.training.pms.DAO;

import com.training.pms.Models.UserAccount;

public interface UserAccountDAO 
{
	public UserAccount	addUserAccount(UserAccount obj);
	public boolean 		deleteUserAccount(UserAccount obj);
	public void 		updateUserAccount(int id, String fName, String lName);
	public UserAccount 	searchByUserAccountName(String fName);
	public void 		searchByUserAccountId(int id);
	public boolean		verifyLogin(String user, String pass);
	public boolean		 isEmployee(String username);
}

