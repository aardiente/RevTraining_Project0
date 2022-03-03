package com.training.pms.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.training.pms.Exceptions.InvalidTransactionException;
import com.training.pms.Models.Customer;
import com.training.pms.Models.UserAccount;
import com.training.pms.Utility.DBConnection;

public class CustomerDAOImpl implements CustomerDAO 
{
	// --> move these to functions to reduce screen space 
	private static final String insertQuery = "insert into customer(balance, fk_userid) values (?, ?)";
	private static final String updateQuery = "UPDATE public.useraccount SET first_name=?, last_name=? WHERE user_id=?";
	private static final String deleteQuery = "Select * from customer where customer_id = ?";
	//private static final String getCustomerQuery = "select * from useraccount join customer on fk_userid = user_id where user_name=?";
	private static final String customerInfo = "select customer_id, user_name, user_password, first_name, last_name, balance from useraccount join customer on fk_userid = user_id where user_name =?";
	private static final String depositQuery = "Update customer set balance = balance + ? where customer_id = ?";
	private static final String withdrawQuery = "Update customer set balance = balance - ? where customer_id = ?";
	
	private static Connection connection;
	
	
	public CustomerDAOImpl()
	{
		connection = DBConnection.getConnection();
	}
	
	public boolean addCustomer(Customer obj)
	{
		if(obj == null)
			return false;
		
		try
		{
			UserAccountDAO uaDAO = new UserAccountDAOImpl();
			PreparedStatement state = DBConnection.getConnection().prepareStatement(insertQuery);
			state.setFloat(1, obj.getAccountBalance());
			UserAccount t = uaDAO.addUserAccount(obj);
			
			System.out.println(t);
			state.setInt(2, t.getAccountId());
			
			
			return state.executeUpdate() > 0;
		
			
		} catch (Exception e)
		{
			System.out.println(e);
		}
		return false;
	}

	public boolean deleteCustomer(Customer obj) 
	{

		return false;
	}

	public void updateCustomer(String fName, String lName, UserAccount obj) 
	{
		// TODO Auto-generated method stub
		UserAccountDAO dao = new UserAccountDAOImpl();
		UserAccount temp = dao.searchByUserAccountName(obj.getUsername());
		dao.updateUserAccount(temp.getAccountId(), fName, lName);
	}

	public Customer searchByCustomerName(String user) 
	{
		// TODO Auto-generated method stub
		PreparedStatement state = null;
		Customer obj = null;
		try 
		{
			state = connection.prepareStatement(customerInfo);
			state.setString(1, user);
			
			ResultSet res = state.executeQuery();
			ResultSetMetaData rsmd = res.getMetaData();
			int cLength = rsmd.getColumnCount();
			
			String[] queryData = new String[cLength];

			while(res.next())// We should only have 1 Account with a given username
			{
				DAOHelper.getColumnStrings(cLength, res, queryData);
				return new Customer(Integer.valueOf(queryData[0]), queryData[1], queryData[2], queryData[3], queryData[4], Float.valueOf(queryData[5])); 
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;

	}

	public Customer searchByCustomerId(int id) 
	{
		Customer obj = null;
		
		try 
		{
			PreparedStatement stat = connection.prepareStatement("select customer_id, user_name, user_password, first_name, last_name, balance from useraccount join customer on fk_userid = user_id where customer_id=?");
			stat.setInt(1, id);
			ResultSet res = stat.executeQuery();
			ResultSetMetaData rsmd = res.getMetaData();
			int cLength = rsmd.getColumnCount();
			
			String[] queryData = new String[cLength];
			
			while(res.next())
			{
				DAOHelper.getColumnStrings(cLength, res, queryData);
				obj = new Customer(Integer.valueOf(queryData[0]), queryData[1], queryData[2], queryData[3], queryData[4], Float.valueOf(queryData[5])); 
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return obj;
	}

	public boolean updateCustomerBalance(Customer obj, float amount) 
	{
		PreparedStatement state = null;
		
		try 
		{
			state = connection.prepareStatement(depositQuery);
			state.setFloat(1, amount);
			state.setInt(2, obj.getAccountId());
			int res = state.executeUpdate();
			
			if(res < 1)
			{
				System.out.println("Insertion Failed");
				return false;
			}
		} 
		catch (SQLException e) 
		{
			e.getStackTrace();
			return false;
		}
		
		return true;
	}

	public boolean withdrawFromBalance(Customer obj, float amount) 
	{
		PreparedStatement state = null;
		
		try 
		{
			state = connection.prepareStatement(withdrawQuery);
			
			if(obj.getAccountBalance() - amount > 0)
			{
				state.setFloat(1, amount);
				state.setInt(2, obj.getAccountId());
				int res = state.executeUpdate();
				
				if(res < 1)
				{
					System.out.println("Insertion Failed");
					return false;
				}
			}
			else throw new InvalidTransactionException("This transaction would result with a negative balance");
		} 
		catch (SQLException e) 
		{
			e.getStackTrace();
			return false;
		}
		catch (InvalidTransactionException e)
		{
			System.out.println(e.getMessage());
			return false;
		}
		
		return true;
	}

	public ArrayList<Customer> getAllCustomers()
	{
		ArrayList<Customer> cList = new ArrayList<Customer>();
		
		try 
		{
			Statement stat = connection.createStatement();
			
			if(stat.execute("select customer_id, user_name, user_password, first_name, last_name, balance from useraccount join customer on fk_userid = user_id"))
			{
				ResultSet set = stat.getResultSet();
				
				ResultSetMetaData rsmd = set.getMetaData();
				int cLength = rsmd.getColumnCount();
				String[] queryData = new String[cLength];
				
				while(set.next())
				{
					DAOHelper.getColumnStrings(cLength, set, queryData);
					cList.add( new Customer(Integer.valueOf(queryData[0]), queryData[1], queryData[2], queryData[3], queryData[4], Float.valueOf(queryData[5]))); 
				}
			}
			
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return cList;
	}
}
