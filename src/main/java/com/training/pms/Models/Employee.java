package com.training.pms.Models;

public class Employee extends UserAccount
{
	public Employee(int accountId, String username, String password, String firstName, String lastName) 
	{
		super(accountId, username, password, firstName, lastName);
		accountType = UserAccount.AccountType.Employee;
		// TODO Auto-generated constructor stub
	}
	public Employee(String username, String password, String firstName, String lastName) 
	{
		this(-1, username, password, firstName, lastName);
		// TODO Auto-generated constructor stub
	}
}
