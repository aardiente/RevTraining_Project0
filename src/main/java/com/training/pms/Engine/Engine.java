package com.training.pms.Engine;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import com.training.pms.Models.*;
import com.training.pms.Models.Transaction.ConfirmationFlags;
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
	
	// Connection Related handles
	private static Scanner inputScanner;
	private static Connection dbHandle;
	
	// State Machine
	private static enum EngineFlags
	{
		MainMenu, Login, SignOut, CreateAccount, ViewAccount, Exit, 	// Engine logic
		MakeTransaction, ChangeAccountDetails,  						// Account logic
		Withdrawal, WithdrawalVerification, DepositFunds, TransferFunds,
		ViewPendingAccounts, ViewTransactions,
		TakeMenuInput, TakeCustomerInput, TakeEmployeeInput;			// Input Logic
	}
	
	// -> Engine states // engineStatus -> current state 
	private static EngineFlags engineState = EngineFlags.MainMenu;
	protected static UserAccount currentUser = null;
	
	/*****************************************************************************************************************************************/
	// Engine logic
	// -> Simply starts the engine, open/close connections, call runtime loop. The ONLY public method.
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
	// Runtime loop logic
	// -> This method contains runtime logic
	private void runEngineLogic()
	{
		boolean flag = false;
		
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
				case DepositFunds:
					depositFunds();
					break;
				case Withdrawal:
					withdrawalRequest();
					break;
				case TransferFunds:
					transferFundsLogic();
					break;
				case ChangeAccountDetails:
					changeAccountDetailsLogic();
					break;
				case ViewPendingAccounts:
					viewCustomerAccount();
					break;
				case ViewTransactions:
					if(currentUser.isEmployee())
						viewTransactions();
					else
						viewCustomerTransactions();
					break;
					

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
					System.out.println("Main menu logic error. Default hit. Closing program.");
					flag = true;
					break;
				}
			} catch (Exception e) 
			{
				e.printStackTrace();
				flag = true;
			}
		}while(!flag);
	}

	/*****************************************************************************************************************************************/
	// Main menu logic
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
				
				if(tBalance >= 1000.0f)
					help.userStatus = InputHelper.InputStates.idle;
				else
					System.out.println("Starting balance is $1000.00");
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

	// User Account Subclass logic
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
	private void signOutLogic()
	{
		currentUser = null;
		setEngine(EngineFlags.MainMenu);
	}
	
	// Customer Logic
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
				
				//engineInputHandler(input);
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
				}
				//setEngine(EngineFlags.MakeTransaction);
				flag = true;
			}
			else
			{
				System.out.println("Invalid option, try again...");
				inputScanner.next();
				input = -1;
			}
			
		}while(!flag);
		
		
	}
	private boolean depositFunds()
	{
		//CustomerDAO dao = new CustomerDAOImpl();
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
					if(dao.updateCustomerBalance(dao.searchByCustomerName(currentUser.getUsername()), amount))
						System.out.println("Succesfully deposited the amount of " + amount + " to your account.");
					else
						System.out.println("Failed Query, debug");
					
					printPadding();
					depFlag = true;
				}
			}
			else
			{
				System.out.println("Invalid input... Try again\nAny key to continue...");
				inputScanner.next();
			}
			
		}while(!depFlag);
		
		setEngine(EngineFlags.ViewAccount);
		return depFlag;
	}
	private Transaction withdrawalRequest()
	{
		Transaction newTrans = null;
		float amount = 0.0f;

		System.out.print("Please enter the amount that you want to withdrawal: ");
		
		if(inputScanner.hasNextFloat())
		{
			amount = inputScanner.nextFloat();
			
			if(amount > 250)
			{
				CustomerDAO cdao = new CustomerDAOImpl();
				Customer obj = cdao.searchByCustomerName(currentUser.getUsername());
				
				newTrans = new Transaction(obj.getAccountId(), amount, obj, obj);
				
				TransactionDAO dao = new TransactionDAOImpl();
				dao.addTransaction(newTrans);
				
				setEngine(EngineFlags.ViewAccount);
			}
			else if(amount <= 250 & amount > 0 )
			{
				CustomerDAO cdao = new CustomerDAOImpl();
				Customer obj = cdao.searchByCustomerName(currentUser.getUsername());
				
				if(cdao.withdrawFromBalance(obj, amount))
					System.out.println("Withdraw Success");
				else
					System.out.println("invalid transaction");
				
				setEngine(EngineFlags.ViewAccount);
			}
			else
			{
				System.out.println("Invalid input.");
				inputScanner.next();
			}
		}

		return newTrans;
	}
	private void transferFundsLogic()
	{
		printPadding(2);
		System.out.println("Please enter the username of the account you'd like to transfer with: ");
		
		InputHelper help = new InputHelper();
		String recieverInput = help.requestUsername(inputScanner);
		
		CustomerDAO cdao = new CustomerDAOImpl();
		
		Customer reciever = cdao.searchByCustomerName(recieverInput);
		
		if(reciever == null)
			System.out.println("There was no user by that name. Returning to menu");
		else
		{
			float amount = 0f;
			System.out.print("Please enter the amount you'd like to transfer: ");
			if(inputScanner.hasNextFloat())
			{
				amount = inputScanner.nextFloat();
				
				TransactionDAO tDao = new TransactionDAOImpl();
				tDao.addTransaction(new Transaction(-1, amount, cdao.searchByCustomerName(currentUser.getUsername()), reciever)); // -1 because its going to get assigned a value in the db
			}
		}
		setEngine(EngineFlags.ViewAccount);
	}
	
	private void viewCustomerTransactions()
	{
		TransactionDAO tDao = new TransactionDAOImpl();
		CustomerDAO cDao = new CustomerDAOImpl();
		Customer curCust = cDao.searchByCustomerName(currentUser.getUsername());
		ArrayList<Transaction> tList = tDao.getPendingTransactionsById(curCust.getAccountId());
		
		if(tList.size() > 0)
			printPadding(2);
		else
		{
			printPadding();
			System.out.println("You have no available transactions.");
		}
		for(Transaction t : tList)
		{
			if(t != null)
			{
				System.out.println(t);
				System.out.print("Would you like to approve this transaction? (Y/N): ");
				String input = inputScanner.next();
				
				if( input.toUpperCase().equals("Y") )
				{
					t.setStatus(ConfirmationFlags.approved);
					
					if(tDao.processTransaction(t))
						System.out.println("Transaction Approved");
				}
				else if(input.toUpperCase().equals("N"))
				{
					t.setStatus(ConfirmationFlags.denied);
					
					if(tDao.denyTransaction(t))
						System.out.println("Transaction Denied");
				}
				else
				{
					System.out.println("Invalid input");
					printPadding(2);
				}
				
			}
		}
		
		setEngine(EngineFlags.ViewAccount);
	}
	
	// Employee Logic
	private void viewPendingLogic() 
	{
		EmployeeDAO dao = new EmployeeDAOImpl();
		
		ArrayList<Customer> cList = dao.getUserAccountsWaitingApproval();
		
		if(cList.size() > 0)
		{
			Collections.sort(cList, (Customer c1, Customer c2) -> c1.getAccountId() - c2.getAccountId() ); // Sort the list on ID in desc order 
			printPadding(3);
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
					
					printPadding();
					System.out.print("Activate account (Y/N): ");
					String buff = inputScanner.next();
					
					if(buff.toUpperCase().equals("Y"))
					{
						if(udao.updateApprovalStatus(ref.getUsername()))
							System.out.println("Account Activated");
					}
					else if( buff.toUpperCase().equals("N") )
					{
						if(udao.deleteUserAccount(ref))
							System.out.println("Account Deactivated (Deleted)");
					}
					else
						System.out.println("Invalid input.");
					
				}
				else
					System.out.println("Customer Id: " + input + " doesn't exist. Returning to previous menu");
				
			}
			else if(inputScanner.hasNext())
			{
				String buffer = inputScanner.next();
				
				if(buffer.equals("r") || buffer.equals("R"))
				{
					setEngine(EngineFlags.ViewPendingAccounts);
				}
				else
					System.out.println("Invalid input");
			}
		}
		else
			System.out.println("Transaction Box is Empty... :(");
		
		//setEngine(EngineFlags.ViewAccount);
		
	}
	private void viewTransactions()
	{
		printPadding(2);
		System.out.print("1. For all transactions | 2. For Pending Transactions\nInput: ");
		
		int input = inputScanner.nextInt();
		
		TransactionDAO tDao = new TransactionDAOImpl();
		ArrayList<Transaction> tList = null;
		
		if(input == 1)
			tList = tDao.getAllTransactions();
		else if(input == 2)
			tList = tDao.getPendingTransactions();
		
		if(tList.size() > 0)
			printPadding(2);
		else 
			System.out.println("No Available Transactions");
		
		for(Transaction t : tList)
		{
			if(t != null)
			{
				System.out.println(t);
				printPadding(2);
			}
		}
		
		setEngine(EngineFlags.ViewAccount);
	}
	private void viewCustomerAccount()
	{
		boolean flag = false;
		CustomerDAO dao = new CustomerDAOImpl();
		
		do
		{
			System.out.print(	"---------- Options ----------\n"
							+	"1. Pending Accounts\n"
							+	"2. List All Accounts\n"
							+	"3. View Customer By ID\n"
							+	"9. To return to the menu\n"
							+	"Input: ");
			
			int input = -1;
			if(inputScanner.hasNextInt())
			{
				input = inputScanner.nextInt();
				switch(input)
				{
				case 1:
					//
					viewPendingLogic();
					flag = true;
					break;
				case 2:
					// List all customers
					
					ArrayList<Customer> cList = dao.getAllCustomers();
					
					cList.forEach( (Customer c)->
							{ 
								printPadding(2);
								System.out.printf("Customer ID: %-5d | Username: %-20s | Name: %-12s %-12s |\n", c.getAccountId(), c.getUsername(), c.getFirstName(), c.getLastName());
								printPadding(2);
							});
					break;
				case 3:
					System.out.print("Please enter the account you want to access: ");
					if(inputScanner.hasNextInt())
					{
						input = inputScanner.nextInt();
						
						printPadding(2);
						System.out.println(dao.searchByCustomerId(input));
						printPadding(2);
					}
					break;
				case 9:
					flag = true;
					break;
				}
				input = -1;
			}
		}while(!flag);
		
		setEngine(EngineFlags.ViewAccount);
	}
	
	/*****************************************************************************************************************************************/
	// Helper methods
	
	// State Machine helper
	// --> This basically is just a wrapper around assigning engineState. Its less typing and less ugly to look at.
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
		}
		else
		{
			System.out.println("An invalid input was provided.");
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
				setEngine(EngineFlags.ViewTransactions);
				break;
			case 3:
				engineState = EngineFlags.ChangeAccountDetails;
				break;
			case 9:
				engineState = EngineFlags.SignOut;
				break;
			}
			break;
			
	    /**************************************************/	
		case TakeEmployeeInput:
			switch(input)
			{
			case 1:	engineState = EngineFlags.ViewPendingAccounts;
				break;
			case 2: engineState = EngineFlags.ViewTransactions;
				break;
			case 3: engineState = EngineFlags.ChangeAccountDetails;
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
	// Console display helpers
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
				+	"1. Create Transaction\n"
				+	"2. View Transaction\n"
				+	"3. Change Account Details\n"
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
				+	"1. View Accounts\n"
				+	"2. View Transactions\n"
				+	"3. Change Account Details\n"
				+	"9. Sign out -> Return to Menu\n"
				);
	}

	// ----> Console formating help
	private void printPadding()
	{
		printPadding(1);
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
	
	/*****************************************************************************************************************************************/
	// Override methods
	// --> This is just ensuring our db is getting closed
	@Override
	public void finalize()
	{
		closeConnections();
	}

}
