package com.training.pms.DAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import com.training.pms.Models.Transaction;
import com.training.pms.Models.Transaction.ConfirmationFlags;
import com.training.pms.Utility.DBConnection;

public class TransactionDAOImpl implements TransactionDAO 
{
	private static final String insertQuery = " INSERT INTO customertransaction	  ( transaction_amount, transaction_date, transaction_approved, fk_customerid_sender, fk_customerid_reciever)VALUES( ?, current_date, false, ?, ?)";// first is sender, 2nd is reciever, 3rd is the amount
	private static final String getPending = "select transaction_id, transaction_amount, fk_customerid_sender, fk_customerid_reciever, transaction_approved, transaction_denied from customertransaction where transaction_approved = false and transaction_denied = false";
	private static final String getAll = "select transaction_id, transaction_amount, fk_customerid_sender, fk_customerid_reciever, transaction_approved, transaction_denied from customertransaction";
	private static final String getPendingById = "select transaction_id, transaction_amount, fk_customerid_sender, fk_customerid_reciever from customertransaction where transaction_approved = false and transaction_denied = false and fk_customerId_reciever =?";
	private static final String processTransaction = "call processTransaction(?, ?, ?, ?)"; // id senderid receiverid amount
	
	private static Connection connection = DBConnection.getConnection();
	
	@Override
	public boolean addTransaction(Transaction obj) 
	{
		//CallableStatement state = null;
		PreparedStatement state = null;
		try 
		{
			state = connection.prepareStatement(insertQuery);
			state.setFloat(1, obj.getTransactionAmount());
			
		
			state.setInt(2, obj.getCustomerHandle().getAccountId());
			
			if(obj.getRecieverHandle() != null)
				state.setInt(3, obj.getRecieverHandle().getAccountId());
			else
				state.setInt(3, obj.getCustomerHandle().getAccountId());
			
			if(state.executeUpdate() > 0)
			{
				System.out.println("Transaction created");
				return true;
			}
			else
				System.out.println("Insertion failed");
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteTransaction(Transaction obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateTransaction(String fName, String lName)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Transaction searchByTransactionName(String fName) 
	{
		return null;
	}

	@Override
	public Transaction searchByTransactionId(int id) 
	{
		return null;

	}
	public boolean denyTransaction(Transaction obj) //Implement this and use it
	{
		if(obj.getStatus() == ConfirmationFlags.denied)
		{
			try 
			{
				PreparedStatement stat = connection.prepareStatement("update customertransaction set transaction_denied = true where transaction_id = ?");
				stat.setInt(1, obj.getTransactionId());
				
				if(stat.executeUpdate() > 0)
					return true;
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
			return false;
		}
		else
			return false;
	}
	
	public boolean processTransaction(Transaction obj)
	{
		try
		{
			if(obj.getCustomerHandle().getAccountBalance() - obj.getTransactionAmount() < 0 )
			{
				PreparedStatement stat = connection.prepareStatement("update customertransaction set transaction_approved = false and transaction_denied = true where transaction_id = ?");
				stat.setInt(1, obj.getTransactionId());
				stat.executeUpdate();
				stat.close();
				System.out.println("Transaction would result in a negative balance, mark for deletion.");
			}
			else if(obj.getCustomerHandle().getAccountId() != obj.getRecieverHandle().getAccountId() && obj.getStatus() == ConfirmationFlags.approved)
			{
				CallableStatement stat = connection.prepareCall(processTransaction);
				stat.setInt(1, obj.getTransactionId());
				stat.setInt(2, obj.getCustomerHandle().getAccountId());
				stat.setInt(3, obj.getRecieverHandle().getAccountId());
				stat.setFloat(4, obj.getTransactionAmount());
				
				if(stat.executeUpdate() > 0)
				{
					return true;
				}
			}
			else if(obj.getCustomerHandle().getAccountId() == obj.getRecieverHandle().getAccountId() && obj.getStatus() == ConfirmationFlags.approved)
			{
				PreparedStatement stat = connection.prepareStatement("update customer set balance = balance - ? where customer_id = ?");
				stat.setInt(2, obj.getCustomerHandle().getAccountId());
				stat.setFloat(1, obj.getTransactionAmount());
				
				if(stat.executeUpdate() > 0)
				{
					stat.close();
					stat = connection.prepareStatement("update customertransaction set transaction_approved = true where transaction_id = ?");
					stat.setInt(1, obj.getTransactionId());
					
					stat.executeUpdate();
					
					return true;
				}
			}
			
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	public ArrayList<Transaction> getAllTransactions()
	{
		ArrayList<Transaction> tList = new ArrayList<Transaction>();

		try 
		{
			PreparedStatement stat = connection.prepareStatement(getAll);
			stat.execute();
			
			ResultSet res = stat.getResultSet();
			ResultSetMetaData rsmd = res.getMetaData();
			int len = rsmd.getColumnCount();
			String[] data = new String[len];
			
			CustomerDAO cDao = new CustomerDAOImpl();
			
			while(res.next())
			{
				DAOHelper.getColumnStrings(len, res, data);
				Transaction temp =  new Transaction( 
						Integer.valueOf(data[0]), 	// id
						Float.valueOf(data[1]), 	// amount
						cDao.searchByCustomerId( Integer.valueOf(data[2]) ), 	// sender
						cDao.searchByCustomerId( Integer.valueOf(data[3]) ),	// receiver
						data[4].equals("t") ? true : false,		// Approved flag
						data[5].equals("t") ? true : false);	// Denied flag
				

				tList.add(temp);
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return tList;
	}
	
	public ArrayList<Transaction> getPendingTransactions()
	{
		ArrayList<Transaction> tList = new ArrayList<Transaction>();
		
		try 
		{
			PreparedStatement stat = connection.prepareStatement(getPending);
			stat.execute();
			
			ResultSet res = stat.getResultSet();
			ResultSetMetaData rsmd = res.getMetaData();
			int len = rsmd.getColumnCount();
			String[] data = new String[len];
			
			CustomerDAO cDao = new CustomerDAOImpl();
			
			while(res.next())
			{
				DAOHelper.getColumnStrings(len, res, data);
				tList.add( new Transaction( 
							Integer.valueOf(data[0]), 	// id
							Float.valueOf(data[1]), 	// amount
							cDao.searchByCustomerId( Integer.valueOf(data[2]) ), 	// sender
							cDao.searchByCustomerId( Integer.valueOf(data[3]) ),		// receiver
							data[4].equals("t") ? true : false,		// Approval flag
							data[5].equals("t") ? true : false
						));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return tList;
	}

	@Override
	public ArrayList<Transaction> getPendingTransactionsById(int id)
	{
		ArrayList<Transaction> tList = new ArrayList<Transaction>();
		
		try 
		{
			PreparedStatement stat = connection.prepareStatement(getPendingById);
			stat.setInt(1, id);
			stat.execute();
			
			ResultSet res = stat.getResultSet();
			ResultSetMetaData rsmd = res.getMetaData();
			int len = rsmd.getColumnCount();
			String[] data = new String[len];
			
			CustomerDAO cDao = new CustomerDAOImpl();
			
			while(res.next())
			{
				DAOHelper.getColumnStrings(len, res, data);
				tList.add( new Transaction( 
							Integer.valueOf(data[0]), 	// id
							Float.valueOf(data[1]), 	// amount
							cDao.searchByCustomerId( Integer.valueOf(data[2]) ), 	// sender
							cDao.searchByCustomerId( Integer.valueOf(data[3]) )		// receiver
						));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return tList;
	}
	
	public ArrayList<Transaction> getPendingTransactionsByCustomerId( int id )
	{
		ArrayList<Transaction> tList = new ArrayList<Transaction>();
		
		try 
		{
			PreparedStatement stat = connection.prepareStatement(getPendingById);
			stat.setInt(1, id);
			
			stat.execute();
			ResultSet res = stat.getResultSet();
			ResultSetMetaData rsmd = res.getMetaData();
			int len = rsmd.getColumnCount();
			String[] data = new String[len];
			
			CustomerDAO cDao = new CustomerDAOImpl();
			
			while(res.next())
			{
				DAOHelper.getColumnStrings(len, res, data);
				tList.add( new Transaction( 
							Integer.valueOf(data[0]), 	// id
							Float.valueOf(data[1]), 	// amount
							cDao.searchByCustomerId( Integer.valueOf(data[2]) ), 	// sender
							cDao.searchByCustomerId( Integer.valueOf(data[3]) )		// receiver
						));
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return tList;
	}
}
