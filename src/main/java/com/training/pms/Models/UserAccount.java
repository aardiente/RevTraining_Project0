package com.training.pms.Models;

public class UserAccount 
{
	// Login information
	protected String username;
	protected String password;
	protected boolean signInStatus;
	
	// User information
	protected String firstName;
	protected String lastName;
	protected int accountId;
	public AccountType accountType;
	
	public static enum AccountType
	{
		Customer, Employee, Unmarked;
		
		@Override
		public String toString()
		{
			return "";
		}
	}
	
	
 	public UserAccount(int accountId, String username, String password, String firstName, String lastName)
	{
		super();
		this.username = username;
		this.password = password;
		this.signInStatus = false; // Defaulting to false, so I can force the user to login after creation
		this.firstName = firstName;
		this.lastName = lastName;
		this.accountId = accountId;
	}
	
	public boolean verifyLogin(String user, String pass)
	{
		return (this.username.equals(user) & this.password.equals(pass));
	}
	
	public boolean loginStatus()
	{
		return this.signInStatus;
	}
	
	
	
	// Getter / Setters
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isSignInStatus() {
		return signInStatus;
	}

	public void setSignInStatus(boolean signInStatus) {
		this.signInStatus = signInStatus;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public boolean isEmployee()
	{
		return this.accountType == AccountType.Employee;
	}
	
	@Override
	public String toString() 
	{
		return "User ID: " + accountId + " | Username " + username + " | Registered Name: " + firstName + " " + lastName + " |";  
	}
	

}
