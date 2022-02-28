package com.training.pms.Engine;

import java.util.Scanner;

import com.training.pms.Exceptions.InvalidUserInputException;
import com.training.pms.Exceptions.LoginAuthenticationException;


public class InputHelper 
{
	// State Machine
	protected static enum InputStates
	{
		idle, requestUsername, requestPassword, requestFName, requestLName, requestBalance
	}
	protected InputStates userStatus = InputStates.idle; // Flag that we're supposed to be taking an input // Used by engine, not really the class itself.

	public void changeState(InputStates state)
	{
		this.userStatus = state;
	}
	
	// Input Requests
	public String requestFirstname(Scanner input)
	{
		boolean flag = false;
		String tName = "";
		
		do
		{
			try
			{
				System.out.print("Please enter your First Name: ");
				tName = input.next();
				
				if(tName.length() > 1)
					flag = true;
				else
					throw new InvalidUserInputException("Invalid Name. Must be more than one character");

			}catch (InvalidUserInputException e) 
			{
				System.out.println(e.getMessage());
				input.next();
			}catch (Exception e)
			{
				System.out.println("Unknown error: " + e.getMessage());
			}
		}while(!flag);
		
		return tName;
	}
	public String requestLastname(Scanner input)
	{
		boolean flag = false;
		String tName = "";
		
		do
		{
			try 
			{
				System.out.print("Please enter your Last Name: ");
				tName = input.next();
				
				if(tName.length() > 1)
					flag = true;
				else
					throw new InvalidUserInputException("Invalid Name. Must be more than one character");
					
			} catch (InvalidUserInputException e) 
			{
				System.out.println(e.getMessage() + "\nPlease try again");
				input.next();
			}catch (Exception e)
			{
				System.out.println("Unknown error: " + e.getMessage());
			}
		}while(!flag);
		
		
		return tName;
	}

	public String requestUsername(Scanner input) 
	{
		boolean flag = false;
		String tUser = "";
		
		System.out.print("Please enter your username: ");
		do
		{
			tUser = input.next();
			
			flag = true;
			
		}while(!flag);
		
		
		return tUser;
	}	
	
	// Obviously validates the password before returning the result of the users input
	public String requestPasswordWithValidation(Scanner input)
	{
		boolean flag = false;
		String tPass1 = "";
		String tPass2 = "";

		do
		{
			try
			{
				System.out.print("Please enter your password (at least 5 characters): ");
				tPass1 = input.next();
				
				if(!tPass1.equals("") & tPass1.length() >= 5 )
				{
					System.out.print("\nPlease verify your password: ");
					tPass2 = input.next();
					
					if(tPass1.equals(tPass2))
					{
						System.out.println("Passwords match.");
						flag = true;
					}
					else
						throw new LoginAuthenticationException("The passwords you provided do not match. Try again.");
				}
				else
					throw new LoginAuthenticationException("The passwords you provided do not match. Try again.");
			} catch (LoginAuthenticationException e) 
			{
				System.out.println(e.getMessage());
				tPass1 = ""; tPass2 = "";
			}
		}while(!flag);
		
		System.gc(); // Request garbage collection because I'm using Strings, this isn't memory efficient.
		return tPass1;
	}
	public String requestPassword(Scanner input)
	{
		boolean flag = false;
		String tPass = "";
		
		do
		{
			try 
			{
				System.out.print("Please enter your password: ");
				tPass = input.next();
				
				if(!tPass.equals("") & tPass.length() >= 5 )
				{
					flag = true;
				}
				else if(!tPass.equals("") )
					throw new LoginAuthenticationException("The password given is invalid");
			} catch (LoginAuthenticationException e) 
			{
				System.out.println(e.getMessage());
				flag = true;
			}

		}while(!flag);
		
		return tPass;
	}

	public float requestBalance(Scanner input) throws NullPointerException
	{
		boolean flag = false;
		float tBalance = 0.0f;
		
		do
		{
			try 
			{
				System.out.print("Please enter the starting balance: ");
				
				if(input.hasNextDouble())
				{
					tBalance = input.nextFloat();
					flag = true;
				}
				else
					throw new InvalidUserInputException("An inproper input was given, please try again.");
					

			} catch (InvalidUserInputException e) 
			{
				System.out.println(e.getMessage());
				input.next();
			}
			
		}while(!flag);
		
		return tBalance;
	}
}