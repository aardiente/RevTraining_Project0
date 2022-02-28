package com.training.pms.Exceptions;
/**************************************************
 * This class will handle any login exception
 * ie. Mismatch Password / Username Cannot be found
 * 
 * 
 * 
 ***************************************************/




public class LoginAuthenticationException extends Exception 
{

	public LoginAuthenticationException() {
		// TODO Auto-generated constructor stub
	}

	public LoginAuthenticationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public LoginAuthenticationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public LoginAuthenticationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public LoginAuthenticationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
