package com.training.pms.Models;


public class Customer extends UserAccount
{
	private final float minBalance = 1000.00F;
	private float accountBalance;
	
	public Customer(String username, String password, String firstName, String lastName, float balance) 
	{
		super(-1, username, password, firstName, lastName);
		accountBalance = (minBalance <= balance) ? balance : minBalance;
		accountType = UserAccount.AccountType.Customer;
	}
	public Customer(int id, String username, String password, String firstName, String lastName, float balance)
	{
		this(username, password, firstName, lastName, balance);
		this.accountId = id;
	}
	

	public float getAccountBalance()
	{
		return this.accountBalance;
	}

	@Override
	public String toString() 
	{
		return "Customer ID: " + accountId + " | Username " + username + " | Registered Name: " + firstName + " " + lastName + " | Balance: " + accountBalance + " |";  
	}


}
