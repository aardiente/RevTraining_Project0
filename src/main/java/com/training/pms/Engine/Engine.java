package com.training.pms.Engine;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import com.training.pms.Models.*;
import com.training.pms.DAO.CustomerDAO;
import com.training.pms.DAO.CustomerDAOImpl;
import com.training.pms.DAO.EmployeeDAO;
import com.training.pms.DAO.EmployeeDAOImpl;
import com.training.pms.DAO.TransactionDAO;
import com.training.pms.DAO.TransactionDAOImpl;
import com.training.pms.DAO.UserAccountDAO;
import com.training.pms.DAO.UserAccountDAOImpl;
import com.training.pms.Exceptions.LoginAuthenticationException;
import com.training.pms.Utility.DBConnection;


public class Engine
{
	/*****************************************************************************************************************************************/
	// 'Global' Variables handles
	private static final String adminPass = "admin";
	// Engine Related handles
	
	// Connection Related handles
	private static Scanner inputScanner;
	private static Connection dbHandle;
	
	// State Machine
	private static enum EngineFlags
	{
		MainMenu, Login, SignOut, CreateAccount, ViewAccount, Exit, 	// Engine logic
		MakeTransaction, ChangeAccountDetails, ViewLogs, 				// Account logic
		Withdrawal, WithdrawalVerification, DepositFunds, TransferFunds,
		ViewPendingAccounts,
		TakeMenuInput, TakeCustomerInput, TakeEmployeeInput;			// Input Logic
	}
	
	// -> Engine states // engineStatus -> current state 
	private static EngineFlags engineState = EngineFlags.MainMenu;
	protected static UserAccount currentUser = null;
	
	/*****************************************************************************************************************************************/
	// Engine logic
	// -> Main runtime Loop
	public void startEngine()
	{
		// Open Connections
		establishConnections();
		
		// Start runtimeLogic
		runEngineLogic();
		
		// Close connections
		closeConnections();
	}
	/*****************************************************************************************************************************************/
	// --> Runtime loop logic
		// This method contains runtime logic
	private void runEngineLogic()
	{
		boolean flag = false;
		//inputScanner.next();
		
		displayWelcomeMessage();
		
		do
		{
			try 
			{
				switch(engineState)
				{
				case MainMenu:
					mainMenuLogic();
					break;
				case Login:
					loginLogic();
					break;
				case SignOut:
					signOutLogic();
					break;
				case CreateAccount:
					createAccountLogic();
					break;
				case ViewAccount:
					viewAccountLogic();
					break;
				/************************************/
				case MakeTransaction:
					makeTransactionLogic();
					break;
				case ChangeAccountDetails:
					changeAccountDetailsLogic();
					
					break;
				case ViewLogs:
					viewLogsLogic();
					break;
				case ViewPendingAccounts:
					viewPendingLogic();
					break;
				/************************************/
					
					
				/************************************/
				case TakeMenuInput:
					menuInputHelper();
					break;
				case TakeCustomerInput:
					menuInputHelper();
					break;
				case TakeEmployeeInput:
					menuInputHelper();
					break;
				case Exit:
					flag = true;
					System.out.println("Good Bye!");
					break;
				default:
					System.out.println("Main menu logic error. Default hit.");
					break;
				}
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}while(!flag);
	}
	private void viewPendingLogic() 
	{
		EmployeeDAO dao = new EmployeeDAOImpl();
		
		printPadding(3);
		System.out.println("Displaying accounts awaiting approval");
		ArrayList<Customer> cList = dao.getUserAccountsWaitingApproval();
		
		Collections.sort(cList, (Customer c1, Customer c2) -> c1.getAccountId() - c2.getAccountId() ); // Sort the list on ID in desc order // Reference: https://mkyong.com/java8/java-8-lambda-comparator-example/
		
		for(Customer obj : cList)
			System.out.printf("Customer Id: %-5d | Username: %-12s | Name: %-32s | Balance: %12f |\n", 
					obj.getAccountId(), obj.getUsername(), obj.getFirstName() + " " + obj.getLastName(), obj.getAccountBalance());
		
		printPadding(3);
		
		System.out.println("Please enter the id of the account you want to approve.\n -> Note: If an invalid id is provided you will be bounced to the previous screen.");
		
		System.out.print("Input: ");
		
		if(inputScanner.hasNextInt())
		{
			int input = inputScanner.nextInt();
			
			Customer ref = null;
			
			for(Customer obj : cList)
				if(obj.getAccountId() == input)
				{
					ref = obj;
					break;
				}
			
			if(ref != null )
			{
				UserAccountDAO udao = new UserAccountDAOImpl();
				if(udao.updateApprovalStatus(ref.getUsername()))
					System.out.println("Success");
			}
			else
				System.out.println("Customer Id: " + input + " doesn't exist. Returning to previous menu");
			
		}
		else if(inputScanner.hasNext())
		{
			String buffer = inputScanner.next();
			
			if(buffer.equals("r") || buffer.equals("R"))
			{
				setEngine(EngineFlags.ViewAccount);
			}
			else
				System.out.println("Invalid input");
		}
		
		setEngine(EngineFlags.ViewAccount);
		
	}
	/*****************************************************************************************************************************************/
	//
	private void mainMenuLogic()
	{
		displayMainMenu();
		setEngine(EngineFlags.TakeMenuInput);
	}
	private void loginLogic()
	{
		InputHelper help = new InputHelper();
		String username = "";
		String password = "";
		try 
		{
			username = help.requestUsername(inputScanner);
			password = help.requestPassword(inputScanner);
			
			UserAccountDAO dao = new UserAccountDAOImpl();
			
			if(dao.verifyLogin(username, password))
			{
				System.out.println("Success");
				currentUser = dao.searchByUserAccountName(username);
				
				if(dao.isEmployee(currentUser.getUsername()))
					currentUser.accountType = UserAccount.AccountType.Employee;
				else
					currentUser.accountType = UserAccount.AccountType.Customer;
				
				setEngine(EngineFlags.ViewAccount);
			}
			else
			{
				setEngine(EngineFlags.MainMenu);
				throw new LoginAuthenticationException("Login attempt to " + username + " has failed");
			}
		} catch (NullPointerException e) 
		{
			System.out.println("Input was not initialized (most likely) Erorr: " + e.getMessage());
		} catch (LoginAuthenticationException e) 
		{
			System.out.println(e.getMessage());
			printPadding();
		}
	}
	private void signOutLogic()
	{
		currentUser = null;
		setEngine(EngineFlags.MainMenu);
	}
	private void createAccountLogic()
	{
		String tUser = "", tPass = "", tFName = "", tLName = "";
		float tBalance = 0.0f;
		boolean flag = false;
		int typeFlag = -1;
		
		InputHelper help = new InputHelper();
		
		
		do
		{
			printPadding();
			System.out.print("What type of account are you trying to create?\n(Customer = 1 / Employee = 2): ");
			
			if(inputScanner.hasNextInt())
			{	
				switch(typeFlag = inputScanner.nextInt())
				{
				case 1:
					flag = true;
					break;
				case 2:
					printPadding();
					System.out.println("Please Enter Admin Creation Password.");
					String t = help.requestPassword(inputScanner);
					
					if(t.equals(adminPass))
						flag = true;
					else
						System.out.println("Incorrect password, start over.");
					break;
				default:
					System.out.println("Invalid input, please try again. \ninput anything to continue.");
					inputScanner.next();
					break;
				}
			}
			
		}while(!flag);
		printPadding();
		flag = !flag;
		
		System.out.println("Please enter your information...");
		
		help.userStatus = InputHelper.InputStates.requestUsername;
		do
		{
			printPadding();
			switch(help.userStatus)
			{
			case requestUsername:
				tUser = help.requestUsername(inputScanner);
				help.userStatus = InputHelper.InputStates.requestPassword;
				break;
			case requestPassword:
				tPass = help.requestPasswordWithValidation(inputScanner);
				help.userStatus = InputHelper.InputStates.requestFName;
				break;
			case requestFName:
				tFName = help.requestFirstname(inputScanner);
				help.userStatus = InputHelper.InputStates.requestLName;
				break;
			case requestLName:
				tLName = help.requestLastname(inputScanner);
				if(typeFlag == 1)
					help.userStatus = InputHelper.InputStates.requestBalance;
				else
					help.userStatus = InputHelper.InputStates.idle;
				break;
			case requestBalance:
				tBalance = help.requestBalance(inputScanner);
				help.userStatus = InputHelper.InputStates.idle;
				break;
			case idle:
				printPadding(2);
				if(typeFlag == 2)
				{
					EmployeeDAO eDAO = new EmployeeDAOImpl();
					eDAO.addEmployee(new Employee(tUser,tPass, tFName, tLName));
					System.out.println("Employee Accounts are activated by default, feel free to sign in.");
				}
				else
				{
					CustomerDAO cDAO = new CustomerDAOImpl();
					cDAO.addCustomer(new Customer(tUser, tPass, tFName, tLName, tBalance));
					System.out.println("Please wait for your account to be verified by an admin before attempting to login.");
				}
				printPadding(2);
				flag = true;
				break;
			}
		}while(!flag);
		
		setEngine(EngineFlags.MainMenu);
	}
	private void viewAccountLogic()
	{
		// If a customer is the currentUser
		printPadding();
		if(currentUser.isEmployee())
		{
			displayEmployeeMenu();
			setEngine(EngineFlags.TakeEmployeeInput);
		}
		else
		{
			displayCustomerMenu();
			setEngine(EngineFlags.TakeCustomerInput);
		}
	}
	private void changeAccountDetailsLogic()
	{
		InputHelper help = new InputHelper();
		
		String tFName = help.requestFirstname(inputScanner);
		String tLName = help.requestLastname(inputScanner);
		CustomerDAO dao = new CustomerDAOImpl();
		dao.updateCustomer(tFName, tLName, currentUser);
		setEngine(EngineFlags.ViewAccount);
	}
	private void viewLogsLogic()
	{
		
	}
	private void makeTransactionLogic()
	{
		CustomerDAO dao = new CustomerDAOImpl();
		Customer tRef = dao.searchByCustomerName(currentUser.getUsername());
		
		
		int input = -1;
		boolean flag = false;
		
		do
		{
			displayTransactionMenu(tRef);
			// Deposits will resolve freely as long as the exit conditions aren't met
			// Withdrawal under $250 dont need to be approved by an admin (If input > 250, create a transaction request)
			// All fund transfers need approval by the 2nd party (Call a helper fn)
			
			
			if(inputScanner.hasNextInt())
			{
				input = inputScanner.nextInt();
				engineInputHandler(input);
				flag = true;
			}
			else
			{
				System.out.println("Invalid option, try again...");
				inputScanner.next();
				input = -1;
			}
			
			switch(engineState)
			{
			case MakeTransaction: // Do nothing default state
				break;
			case DepositFunds:
				depositFunds(tRef);
				setEngine(EngineFlags.ViewAccount);
				break;
			case Withdrawal:
				withdrawalRequest(tRef);
				setEngine(EngineFlags.ViewAccount);
				break;
			case WithdrawalVerification:
				setEngine(EngineFlags.ViewAccount);
				break;
				
			case TransferFunds:
				setEngine(EngineFlags.ViewAccount);
				break;
			default:
				break;
			}

		}while(!flag);
		
		
	}

	private boolean depositFunds(Customer obj)
	{
		boolean depFlag = false; // We can use this for the loop because you're forced to make a valid deposit
		float amount = 0.0f;
		
		do
		{
			System.out.print("Please enter the amount you want to deposit: ");
			if(inputScanner.hasNextFloat())
			{
				amount = inputScanner.nextFloat();
				System.out.println(amount);
				
				if(amount < 0.0f)
				{
					System.out.println("You can't add negative funds, we can just burn the money if you'd prefer that... we don't care.");
					inputScanner.next();
				}
				else // I guess adding 0 is funds is fine.
				{
					// For now I'm just going update the balance
					//obj.editBalance(amount); // After this line is where you call CustomerDAO and TransactionDAO
					CustomerDAO dao = new CustomerDAOImpl();
					
					printPadding();
					if(dao.updateCustomerBalance(obj, amount))
						System.out.println("Succesfully deposited the amount of " + amount + " to your account.");
					else
						System.out.println("Failed Query, debug");
					
					printPadding();
					depFlag = true;
				}
			}
			else
			{
				System.out.println("Invalid input... Try again");
				inputScanner.next();
			}
			
		}while(!depFlag);
			
		return depFlag;
	}

	private Transaction withdrawalRequest(Customer obj)
	{
		TransactionDAO dao = null;
		Transaction newTrans = null;
		boolean flag = false;
		float amount = 0.0f;
		
		do
		{
			System.out.print("Please enter the amount that you want to withdrawal: ");
			
			if(inputScanner.hasNextFloat())
			{
				amount = inputScanner.nextFloat();
				
				if(amount > 0)
				{
					newTrans = new Transaction(obj.getAccountId(), amount, obj);
					dao = new TransactionDAOImpl();
					dao.addTransaction(newTrans);
					flag = true;
				}
				else
				{
					System.out.println("Invalid input, please try again");
					inputScanner.next();
				}
			}
		}while(!flag);
		
		return newTrans;
	}
	
	
	/*****************************************************************************************************************************************/
	// Helper methods
	
	private void setEngine(EngineFlags flag)
	{
		engineState = flag;
	}
	// Input helper
	private void menuInputHelper()
	{
		int input = -1;
		System.out.print("Input: " );
		if(inputScanner.hasNextInt())
		{
			input = inputScanner.nextInt();
			engineInputHandler(input);
			printPadding();
		}
		else
		{
			System.out.println("An invalid input was provided.");
			printPadding();
			inputScanner.next();
		}
	}
	
	// --> This method is the input controller.
	private void engineInputHandler(int input)
	{
		switch(engineState)
		{
        /**************************************************/
		case TakeMenuInput:
			switch(input)
			{
			case 1:
				engineState = EngineFlags.Login;
				break;
			case 2:
				engineState = EngineFlags.CreateAccount;
				break;
			case 9:
				engineState = EngineFlags.Exit;
				break;	
			}
			break;
        /**************************************************/
		case TakeCustomerInput:
			switch(input)
			{
			case 1:
				engineState = EngineFlags.MakeTransaction;
				break;
			case 2:
				engineState = EngineFlags.ChangeAccountDetails;
				break;
			case 9:
				engineState = EngineFlags.SignOut;
				break;
			}
			break;
			
		case MakeTransaction:
			switch(input)
			{
			case 1:
				setEngine(EngineFlags.DepositFunds);
				break;
			case 2:
				setEngine(EngineFlags.Withdrawal);
				break;
			case 3: 
				setEngine(EngineFlags.TransferFunds);
				break;
			case 9:
				setEngine(EngineFlags.ViewAccount);
				break;
			default:
				break;
			}
	    /**************************************************/	
		case TakeEmployeeInput:
			switch(input)
			{
			case 1:	engineState = EngineFlags.ViewPendingAccounts;
				break;
			case 2: engineState = EngineFlags.SignOut;
				break;
			case 3: engineState = EngineFlags.SignOut;
				break;
			case 9:
				engineState = EngineFlags.SignOut;
				break;
			}
			break;

		/**************************************************/	
		default:
			break;
		}
	}
	
	/*****************************************************************************************************************************************/
	// ---> Console display helpers
	private void displayWelcomeMessage()
	{
		System.out.println(
				"-------------------------------------------------------------\n" 
			+	"             Welcome to my Banking Application!\n"
			+	"-------------------------------------------------------------"
	  		   );
	}
	private void displayMainMenu()
	{
		System.out.println(
				"-------- Options --------\n"
			+	"1. Log in to an account\n"
			+	"2. Create an account\n"
			+	"9. Exit Program\n"
				);
	}
	private void displayCustomerMenu()
	{
		if(currentUser == null)
		{
			setEngine( EngineFlags.MainMenu );
			System.out.println("There wasnt a current user"); //debugging help
			return;
		}
		CustomerDAO dao = new CustomerDAOImpl();
		
		
		Customer tRef = dao.searchByCustomerName(currentUser.getUsername());
		
		System.out.println(
					"========Account Details========\n"
				+	"         Account Id: " + tRef.getAccountId() + "\n"
				+	"------------------------------\n"
				+ 	"Name:     " + tRef.getFirstName() + " " + tRef.getLastName() + "\n"
				+	"Balance:  " + tRef.getAccountBalance() + "\n"
				+	"---------- Options ----------\n"
				+	"1. Make Transaction\n"
				+	"2. Change Account Details\n"
				+	"9. Sign out -> Return to Menu\n"
				);
	}
	private void displayTransactionMenu(Customer obj)
	{
		System.out.println(
					"Your balance is : " + obj.getAccountBalance()
				+	"\n------------Options------------"
				+	"\n1. Deposit Funds"
				+	"\n2. Withdrawal Funds"
				+	"\n3. Transfer funds"
				+	"\n9. Return to previous menu"
				);
	}
	private void displayEmployeeMenu()
	{
		if(currentUser == null)
		{
			setEngine( EngineFlags.MainMenu );
			System.out.println("There wasnt a current user"); //debugging help
			return;
		}
		EmployeeDAO dao = new EmployeeDAOImpl();
		
		Employee tRef = dao.searchByEmployeeName(currentUser.getUsername());
		
		System.out.println(
					"========Account Details========\n"
				+	"         Account Id: " + tRef.getAccountId() + "\n"
				+	"------------------------------\n"
				+ 	"Name:     " + tRef.getFirstName() + " " + tRef.getLastName() + "\n"
				+	"---------- Options ----------\n"
				+	"1. View Accounts awaiting approval\n"
				+	"2. Change Account Details\n"
				+	"9. Sign out -> Return to Menu\n"
				);
	}

	// ----> Console formating help
	private void printPadding()
	{
		System.out.println("================================================");
	}
	private void printPadding(int num)
	{
		StringBuilder temp = new StringBuilder("");
		
		for(int i = 0; i < num; i++)
			temp.append("================================================");
		
		System.out.println(temp.toString());
	}
	
	/*****************************************************************************************************************************************/
	// --> Connection Methods
	private void establishConnections()
	{
		// Init Scanner
		inputScanner = new Scanner(System.in);
		DBConnection.initConnection();
		dbHandle = DBConnection.getConnection();
	}
	
	private void closeConnections()
	{
		// Close Scanner
		if(inputScanner != null)
			inputScanner.close();
		
		if(dbHandle != null)
		{
		// Close DBServices
			dbHandle = null;
			DBConnection.closeConnection();
		}
		// call gc();
		System.gc();
	}
	
	@Override
	public void finalize()
	{
		closeConnections();
	}
	

}
