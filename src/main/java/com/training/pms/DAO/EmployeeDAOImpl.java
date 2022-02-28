package com.training.pms.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.training.pms.Models.Customer;
import com.training.pms.Models.Employee;
import com.training.pms.Models.UserAccount;
import com.training.pms.Utility.DBConnection;

public class EmployeeDAOImpl implements EmployeeDAO {
	private static final String insertQuery = "insert into employee(fk_userid) values (?)";
	private static final String getEmployeeQuery = "select * from useraccount join employee on fk_userid = user_id where user_name=?";
	
	private static Connection connection = DBConnection.getConnection();
	
	
	@Override
	public void addEmployee(Employee obj) 
	{
		try
		{
			UserAccountDAO uaDAO = new UserAccountDAOImpl();
			PreparedStatement state = DBConnection.getConnection().prepareStatement(insertQuery);
			UserAccount t = uaDAO.addUserAccount(obj);
			t.accountType = UserAccount.AccountType.Employee;
			
			//System.out.println(t);
			state.setInt(1, t.getAccountId());
			
			
			int inserted = state.executeUpdate();
			
			if(inserted != 1)
				System.out.println("Failed Insertion");	
			
		} catch (Exception e)
		{
			System.out.println(e);
		}

	}

	@Override
	public boolean deleteEmployee(Employee obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateEmployee(String fName, String lName) {
		// TODO Auto-generated method stub

	}

	@Override
	public Employee searchByEmployeeName(String user) 
	{
		// TODO Auto-generated method stub
		PreparedStatement state = null;
		Employee obj = null;
		try 
		{
			state = connection.prepareStatement(getEmployeeQuery);
			state.setString(1, user);
			
			ResultSet res = state.executeQuery();
			ResultSetMetaData rsmd = res.getMetaData();
			int cLength = rsmd.getColumnCount();
			
			String[] queryData = new String[cLength];
			String[] queryColumns = new String[cLength];
			
			DAOHelper.getColumnNames(rsmd, queryColumns);
			
			while(res.next())// We should only have 1 Account with a given username
			{
				DAOHelper.getColumnStrings(cLength, res, queryData);
				DAOHelper.outputFormatHelper(cLength, queryColumns, queryData);
				obj = new Employee(Integer.valueOf(queryData[6]), queryData[1], queryData[2], queryData[3], queryData[4]);
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;

	}

	@Override
	public Employee searchByEmployeeId(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
