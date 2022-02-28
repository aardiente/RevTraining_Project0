package com.training.pms.DAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import com.training.pms.Models.Transaction;
import com.training.pms.Utility.DBConnection;

public class TransactionDAOImpl implements TransactionDAO 
{
	private static final String insertQuery = "createTransaction(?, ?, ?)";// first is sender, 2nd is reciever, 3rd is the amount
	private static Connection connection = DBConnection.getConnection();
	
	@Override
	public void addTransaction(Transaction obj) 
	{
		CallableStatement state = null;
		
		try 
		{
			state = connection.prepareCall(insertQuery);
			state.setInt(1, obj.getCustomerHandle().getAccountId());
			
			if(obj.getRecieverHandle() != null)
				state.setInt(2, obj.getRecieverHandle().getAccountId());
			else
				state.setInt(2, -1);
			
			state.setFloat(3, obj.getTransactionAmount());
			
			if(state.execute())
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

}
