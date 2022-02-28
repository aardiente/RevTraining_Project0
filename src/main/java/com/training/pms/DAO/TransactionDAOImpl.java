package com.training.pms.DAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import com.training.pms.Models.Transaction;
import com.training.pms.Utility.DBConnection;

public class TransactionDAOImpl implements TransactionDAO 
{
	private static final String insertQuery = " INSERT INTO customertransaction	  ( transaction_amount, transaction_date, transaction_approved, fk_customerid_sender, fk_customerid_reciever)VALUES( ?, current_date, false, ?, ?)";// first is sender, 2nd is reciever, 3rd is the amount
	private static final String getPending = "select transaction_id, transaction_amount, fk_customerid_sender, fk_customerid_reciever from customertransaction where transaction_approved = false";
	private static final String getPendingById = "select transaction_id, transaction_amount, fk_customerid_sender, fk_customerid_reciever from customertransaction where transaction_approved = false and fk_customerId_reciever =?";
	private static Connection connection = DBConnection.getConnection();
	
	@Override
	public void addTransaction(Transaction obj) 
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
				System.out.println("Transaction created");
			else
				System.out.println("Insertion failed");
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	@Override
	public boolean deleteTransaction(Transaction obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateTransaction(String fName, String lName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void searchByTransactionName(String fName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void searchByTransactionId(int id) {
		// TODO Auto-generated method stub

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
							cDao.searchByCustomerId( Integer.valueOf(data[3]) )		// receiver
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
