package com.training.pms.DAO;

import com.training.pms.Models.Transaction;

public interface TransactionDAO 
{
	public void 	addTransaction(Transaction obj);
	public boolean 	deleteTransaction(Transaction obj);
	public void 	updateTransaction(String fName, String lName);
	public void 	searchByTransactionName(String fName);
	public void 	searchByTransactionId(int id);
}
