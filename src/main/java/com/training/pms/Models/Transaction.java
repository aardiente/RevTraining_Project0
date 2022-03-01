package com.training.pms.Models;


public class Transaction 
{
	private int transactionId;

	private float transactionAmount;
	private float startingBalance;
	private float updatedBalance;
	private Customer senderHandle;
	private Customer recieverHandle;
	private Employee employeeHandle;
	
	private ConfirmationFlags status;
	public enum ConfirmationFlags
	{
		pending, approved, denied;
		
		@Override
		public String toString()
		{
			String temp;
			
			switch(this)
			{
			case pending:
				temp = "Pending";
				break;
			case approved:
				temp = "Approved";
				break;
			case denied:
				temp = "Denied";
				break;
			default:
				temp = "Invalid token";
				break;
			}
			
			return temp;
		}
	}
	
	public Transaction(int id, float amount, Customer obj) throws NullPointerException
	{
		transactionId = id;
		transactionAmount = amount;
		senderHandle = obj;
		startingBalance = obj.getAccountBalance();
		updatedBalance = 0.0f;
		status = ConfirmationFlags.pending; // defaulting to false for now
	}	
	public Transaction(int id, float amount, Customer owner, Customer reciever)
	{
		this(id, amount, owner);
		this.recieverHandle = reciever;
	}
	public Transaction(int id, float amount, Customer owner, Customer reciever, boolean approved)
	{
		this(id, amount, owner, reciever);
		this.status = (approved) ? ConfirmationFlags.pending : ConfirmationFlags.approved;
	}


	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public float getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(float transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public float getStartingBalance() {
		return startingBalance;
	}

	public void setStartingBalance(float startingBalance) {
		this.startingBalance = startingBalance;
	}

	public float getUpdatedBalance() {
		return updatedBalance;
	}

	public void setUpdatedBalance(float updatedBalance) {
		this.updatedBalance = updatedBalance;
	}

	public Customer getCustomerHandle() {
		return senderHandle;
	}

	public void setCustomerHandle(Customer customerHandle) {
		this.senderHandle = customerHandle;
	}

	public Customer getRecieverHandle() {
		return recieverHandle;
	}

	public void setRecieverHandle(Customer recieverHandle) {
		this.recieverHandle = recieverHandle;
	}

	public Employee getEmployeeHandle() {
		return employeeHandle;
	}

	public void setEmployeeHandle(Employee employeeHandle) {
		this.employeeHandle = employeeHandle;
	}

	public ConfirmationFlags getStatus() {
		return status;
	}

	public void setStatus(ConfirmationFlags status) {
		this.status = status;
	}

	@Override
	public String toString()
	{
		return "Transaction ID: " + transactionId + " | To: " + recieverHandle.username + " | From: " + senderHandle.username + " | Amount: " + transactionAmount + " | Status " + status;
	}
	
	@Override
	public void finalize()
	{
		senderHandle = null;
		employeeHandle = null;
	}


}
