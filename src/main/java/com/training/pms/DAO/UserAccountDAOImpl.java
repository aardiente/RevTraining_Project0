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
	private static final String insertQuery = "INSERT INTO public.useraccount(user_name, user_password, first_name, last_name, date_created, account_activated)VALUES(?, ?, ?, ?, ?, ?)";
	private static final String updateQuery = "UPDATE public.useraccount SET first_name=?, last_name=? WHERE user_id=?";
	private static final String deleteQuery = "delete from useraccount where user_name =?";
	private static final String searchByUserNameQuery = "select * from useraccount where user_name=?";
	private static final String loginVerificationQuery = "select * from useraccount where user_name=? and user_password=? and account_activated=true";
	private static final String updateApprovalStatus = "update useraccount SET account_activated=true where user_name =?";
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
			state.setBoolean(6, obj.isActivationStatus());
			
			if( state.executeUpdate() > 0 )
				return searchByUserAccountName(obj.getUsername());
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			System.out.println(e.getMessage());
		}
		
		return obj;
	}

	@Override
	public boolean deleteUserAccount(UserAccount obj) 
	{
		try 
		{
			if(obj == null)
				throw new NullPointerException("Argument 'obj' is null");
			
			PreparedStatement stat = connection.prepareStatement(deleteQuery);
			stat.setString(1, obj.getUsername());
			
			if(stat.executeUpdate() > 0)
				return true;
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}catch (NullPointerException e)
		{
			System.out.println(e.getLocalizedMessage());
		}
		
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
			
			while(resSet.next())// We should only have 1 Account with a given username
			{
				DAOHelper.getColumnStrings(cLength, resSet, queryData);
				return new UserAccount(Integer.valueOf(queryData[0]), queryData[1], queryData[2], queryData[3], queryData[4]);
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return obj;
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

	@Override
	public boolean updateApprovalStatus(String username)
	{
		try 
		{
			PreparedStatement stat = connection.prepareStatement(updateApprovalStatus);
			stat.setString(1, username);
			
			if(stat.executeUpdate() > 0)
				System.out.println("User: " + username + " approved");
			else
				System.out.println("Approval failed");
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
