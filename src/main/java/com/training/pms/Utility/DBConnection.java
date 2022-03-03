package com.training.pms.Utility;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBConnection 
{
	private static String url;
	private static String user;
	private static String pass;
	private static String driver;
	private static final String propPath = "db.properties";
	private static Connection dbConnection;
	
	public static Connection getConnection()
	{
        return dbConnection;
	}
	
	public static void initConnection()
	{
        try // initializing a JDBC type 4 driver
        {
        	FileReader rdr = new FileReader(propPath);
        	Properties prop = new Properties();
        	prop.load(rdr);
        	
        	url = prop.getProperty("url");
        	driver = prop.getProperty("driver");
        	pass = prop.getProperty("password");
        	user = prop.getProperty("username");
        	
        	Class.forName(driver);
        	System.out.println("Driver Initilaized");
        	dbConnection = DriverManager.getConnection(url, user, pass);
        	System.out.println("Connection Initialized");
		} catch (SQLException | ClassNotFoundException e) 
        {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void closeConnection()
	{
		try
		{
			dbConnection.close();
		}catch(SQLException e)
		{
			System.out.println(e);
		}catch(Exception e)
		{
			System.out.println(e);
		}
	}
}
