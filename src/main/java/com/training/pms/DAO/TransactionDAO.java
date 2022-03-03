package com.training.pms.DAO;

import java.util.ArrayList;

import com.training.pms.Models.Transaction;

public interface TransactionDAO 
{
	public boolean 		addTransaction(Transaction obj);
	public boolean 		deleteTransaction(Transaction obj);
	public boolean 		updateTransaction(String fName, String lName);
	public Transaction 	searchByTransactionName(String fName);
	public Transaction 	searchByTransactionId(int id);
	public boolean		processTransaction(Transaction obj);
	public boolean 		denyTransaction(Transaction obj);
	public ArrayList<Transaction> getAllTransactions();
	public ArrayList<Transaction> getPendingTransactions();
	public ArrayList<Transaction> getPendingTransactionsById(int id);
	public ArrayList<Transaction> getPendingTransactionsByCustomerId( int id );
}
