package com.training.pms.DAO;

import java.util.ArrayList;

import com.training.pms.Models.Customer;
import com.training.pms.Models.Employee;

public interface EmployeeDAO 
{
	public void 		addEmployee(Employee obj);
	public boolean 		deleteEmployee(Employee obj);
	public void 		updateEmployee(String fName, String lName);
	public Employee 	searchByEmployeeName(String fName);
	public Employee 	searchByEmployeeId(int id);
	ArrayList<Customer> getUserAccountsWaitingApproval();
}
