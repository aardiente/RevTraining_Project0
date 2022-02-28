package com.training.pms.DAO;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public abstract class DAOHelper 
{
	public static void getColumnNames(ResultSetMetaData rsmd, String[] names) throws SQLException
	{
		int count = rsmd.getColumnCount();
		
		for(int i = 1; i <= count; i++)
		{
			names[i - 1] = new String(rsmd.getColumnName(i));
		}
	}
    public static void getColumnStrings(int cLength, ResultSet res, String[] sArr) throws SQLException
    {
    	for(int i = 1; i <= cLength; i++)
    	{
    		sArr[i - 1] = res.getString(i);
    	}
    }
    public static void outputFormatHelper(int len, String[] names, String[] content)
    {
		for(int i = 1; i <= len; i++)
		{
			if(content[i - 1] != null)
			{
				if(content[i-1].length() >= 12)
					System.out.printf("%12s: %-26s", names[i - 1], content[i - 1]);
				else if(content[i-1].length() >= 26)
					System.out.printf("%12s: %-38s", names[i - 1], content[i - 1]);
				else
					System.out.printf("%12s: %-12s", names[i - 1], content[i - 1]);
			}
		}
		System.out.println();
    }
}
