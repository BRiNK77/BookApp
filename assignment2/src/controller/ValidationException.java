package controller;

// used to print exceptions for validation issues, will print the exception and its message
@SuppressWarnings("serial")
public class ValidationException extends Exception {

	public ValidationException(Exception e) {
		super(e);
	}

	public ValidationException(String msg) {
		super(msg);
	}
}