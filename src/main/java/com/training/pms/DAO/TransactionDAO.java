package com.training.pms.DAO;

import java.util.ArrayList;

import com.training.pms.Models.Transaction;

public interface TransactionDAO 
{
	public void 	addTransaction(Transaction obj);
	public boolean 	deleteTransaction(Transaction obj);
	public void 	updateTransaction(String fName, String lName);
	public void 	searchByTransactionName(String fName);
	public void 	searchByTransactionId(int id);
	public boolean processTransaction(Transaction obj);
	public boolean denyTransaction(Transaction obj);
	public ArrayList<Transaction> getAllTransactions();
	public ArrayList<Transaction> getPendingTransactions();
	public ArrayList<Transaction> getPendingTransactionsById(int id);
	public ArrayList<Transaction> getPendingTransactionsByCustomerId( int id );
}
