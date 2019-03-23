package controller;

@SuppressWarnings("serial")
public class ValidationException extends Exception {
	
	public ValidationException(Exception e) {
		super(e);
	}
	
	public ValidationException(String msg) {
		super(msg);
	}
}