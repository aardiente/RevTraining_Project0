package com.training.pms.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.training.pms.Models.UserAccount;
import com.training.pms.Utility.DBConnection;

public class UserAccountDAOImpl implements UserAccountDAO 
{
	private static final String insertQuery = "INSERT INTO public.useraccount(user_name, user_password, first_name, last_name, date_created)VALUES(?, ?, ?, ?, ?)";
	private static final String updateQuery = "UPDATE public.useraccount SET first_name=?, last_name=? WHERE user_id=?";
	private static final String deleteQuery = "";
	private static final String searchByUserNameQuery = "select * from useraccount where user_name=?";
	private static final String loginVerificationQuery = "select * from useraccount where user_name=? and user_password=?";
	private static Connection connection = DBConnection.getConnection();
	
	@Override
	public UserAccount addUserAccount(UserAccount obj)
	{
		PreparedStatement state = null;
		
		try 
		{
			state = DBConnection.getConnection().prepareStatement(insertQuery);
			state.setString(1, obj.getUsername());
			state.setString(2, obj.getPassword());
			state.setString(3, obj.getFirstName());
			state.setString(4, obj.getLastName());
			state.setDate(5, new Date(System.currentTimeMillis())); // Stack overflow suggestion for current date
			
			int created = state.executeUpdate();
			obj = searchByUserAccountName(obj.getUsername());
			
			if(created >= 0)
				System.out.println("Object was created: " + obj);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return obj;
	}

	@Override
	public boolean deleteUserAccount(UserAccount obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateUserAccount(int id, String fName, String lName)
	{
		PreparedStatement state = null;
		
		try 
		{
			state = connection.prepareStatement(updateQuery);
			state.setString(1, fName);
			state.setString(2, lName);
			state.setInt(3, id);
			
			int inserted = state.executeUpdate();
			
			if(inserted < 1)
			{
				System.out.print("Update failed");
			}
			
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}

	@Override
	public UserAccount searchByUserAccountName(String username) 
	{
		PreparedStatement state = null;
		UserAccount obj = null;
		try 
		{
			state = connection.prepareStatement(searchByUserNameQuery);
			state.setString(1, username);
			ResultSet resSet = state.executeQuery();
			ResultSetMetaData rsmd = resSet.getMetaData();
			int cLength = rsmd.getColumnCount();
						
			String[] queryData = new String[cLength];
			String[] queryColumns = new String[cLength];
			
			DAOHelper.getColumnNames(rsmd, queryColumns);
			
			while(resSet.next())// We should only have 1 Account with a given username
			{
				DAOHelper.getColumnStrings(cLength, resSet, queryData);
				obj = new UserAccount(Integer.valueOf(queryData[0]), queryData[1], queryData[2], queryData[3], queryData[4]);
			}
			//System.out.println("UserAccount: " + obj);
		} 
		catch (SQLException e)
		{

			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public void searchByUserAccountId(int id) {
		// TODO Auto-generated method stub

	}

	public boolean isEmployee(String username)
	{
		try 
		{
			PreparedStatement stat = connection.prepareStatement("select * from useraccount join employee on fk_userid = user_id where user_name =?");
			stat.setString(1, username);
			stat.execute();

			ResultSet set = stat.getResultSet();
			if(set.next())
			{
				return true;
			}

		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	@Override
	public boolean verifyLogin(String user, String pass) 
	{
		boolean validUser = false;
		PreparedStatement state = null;
		
		try 
		{
			state = connection.prepareStatement(loginVerificationQuery);
			state.setString(1, user);
			state.setString(2, pass);
			
			ResultSet res = state.executeQuery();
			validUser = res.next();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return validUser;

	}
}
