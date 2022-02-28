package com.training.pms.DAO;

import com.training.pms.Models.Customer;
import com.training.pms.Models.UserAccount;

public interface CustomerDAO //extends UserAccountDAO
{
	public void 		addCustomer(Customer obj);
	public boolean 		deleteCustomer(Customer obj);
	public void 		updateCustomer(String fName, String lName, UserAccount Obj);
	public Customer 	searchByCustomerName(String fName);
	public void 		searchByCustomerId(int id);
	public boolean		updateCustomerBalance(Customer obj, float amount);
}
