package com.training.pms.Engine;

import java.sql.Connection;
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


public abstract class Engine
{
	/*****************************************************************************************************************************************/
	// 'Global' Variables handles
	
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
		TakeMenuInput, TakeCustomerInput, TakeEmployeeInput;			// Input Logic
	}
	
	// -> Engine states // engineStatus -> current state 
	private static EngineFlags engineState = EngineFlags.MainMenu;
	protected static UserAccount currentUser = null;
	
	/*****************************************************************************************************************************************/
	// Engine logic
	// -> Main runtime Loop
	public static void startEngine()
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
	private static void runEngineLogic()
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
					setEngine(EngineFlags.TakeMenuInput);
					break;
				case Login:
					loginLogic();
					break;
				case SignOut:
					signOutLogic();
					setEngine(EngineFlags.MainMenu);
					break;
				case CreateAccount:
					createAccountLogic();
					setEngine(EngineFlags.MainMenu);
					break;
				case ViewAccount:
					viewAccountLogic();
					
					if(currentUser.isEmployee())
						setEngine(EngineFlags.TakeEmployeeInput);
					else
						setEngine(EngineFlags.TakeCustomerInput);
					break;
				/************************************/
				case MakeTransaction:
					makeTransactionLogic();
					break;
				case ChangeAccountDetails:
					changeAccountDetailsLogic();
					setEngine(EngineFlags.ViewAccount);
					break;
				case ViewLogs:
					viewLogsLogic();
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
					System.out.println("Main menu logic error. Default hit.");
					break;
				}
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}while(!flag);
	}
	/*****************************************************************************************************************************************/
	//
	private static void mainMenuLogic()
	{
		displayMainMenu();
		//setEngine(EngineFlags.TakeMenuInput);
	}
	private static void loginLogic()
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
		}
	}
	private static void signOutLogic()
	{
		
	}
	private static void createAccountLogic()
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
					flag = true;
					break;
				default:
					System.out.println("Invalid input, please try again.");
					inputScanner.next();
					break;
				}
			}
			
		}while(!flag);
		
		flag = !flag;
		
		System.out.println("Please enter your information...");
		
		help.userStatus = InputHelper.InputStates.requestUsername;
		do
		{
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
				if(typeFlag == 2)
				{
					EmployeeDAO eDAO = new EmployeeDAOImpl();
					eDAO.addEmployee(new Employee(tUser,tPass, tFName, tLName));
				}
				else
				{
					CustomerDAO cDAO = new CustomerDAOImpl();
					cDAO.addCustomer(new Customer(tUser, tPass, tFName, tLName, tBalance));	
				}
				flag = true;
				break;
			}
		}while(!flag);
		

	}
	private static void viewAccountLogic()
	{
		// If a customer is the currentUser
		
		if(currentUser.isEmployee())
			displayEmployeeMenu();
		else
			displayCustomerMenu();
	}
	private static void changeAccountDetailsLogic()
	{
		InputHelper help = new InputHelper();
		
		String tFName = help.requestFirstname(inputScanner);
		String tLName = help.requestLastname(inputScanner);
		CustomerDAO dao = new CustomerDAOImpl();
		dao.updateCustomer(tFName, tLName, currentUser);
	}
	private static void viewLogsLogic()
	{
		
	}
	private static void makeTransactionLogic()
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

	private static boolean depositFunds(Customer obj)
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

	private static Transaction withdrawalRequest(Customer obj)
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
	
	private static void setEngine(EngineFlags flag)
	{
		engineState = flag;
	}
	// Input helper
	private static void menuInputHelper()
	{
		int input = -1;
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
	private static void engineInputHandler(int input)
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
	    /**************************************************/	
		case TakeEmployeeInput:
			switch(input)
			{
			case 1:	engineState = EngineFlags.Exit;
				break;
			case 2: engineState = EngineFlags.Exit;
				break;
			case 3: engineState = EngineFlags.Exit;
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
		default:
			//System.out.println("Something fucked up lol...");
			break;
		}
	}
	
	/*****************************************************************************************************************************************/
	// ---> Console display helpers
	private static void displayWelcomeMessage()
	{
		System.out.println(
				"-------------------------------------------------------------\n" 
			+	"             Welcome to my Banking Application!\n"
			+	"-------------------------------------------------------------"
	  		   );
	}
	private static void displayMainMenu()
	{
		System.out.println(
				"-------- Options --------\n"
			+	"1. Log in to an account\n"
			+	"2. Create an account\n"
			+	"9. Exit Program\n"
				);
	}
	private static void displayCustomerMenu()
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
	private static void displayTransactionMenu(Customer obj)
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
	private static void displayEmployeeMenu()
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
				+	"1. View Transactions\n"
				+	"2. Change Account Details\n"
				+	"9. Sign out -> Return to Menu\n"
				);
	}

	// ----> Console formating help
	private static void printPadding()
	{
		System.out.println("================================================");
	}
	
	/*****************************************************************************************************************************************/
	// --> Connection Methods
	private static void establishConnections()
	{
		// Init Scanner
		inputScanner = new Scanner(System.in);
		DBConnection.initConnection();
		dbHandle = DBConnection.getConnection();
	}
	
	private static void closeConnections()
	{
		// Close Scanner
		inputScanner.close();
		// Close DBServices
		dbHandle = null;
		DBConnection.closeConnection();
		// call gc();
		System.gc();
		
		// Call exit
		//System.exit(0);
	}
	

}
