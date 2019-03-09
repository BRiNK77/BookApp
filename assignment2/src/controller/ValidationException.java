package controller;

public class ValidationException extends Exception {
	public ValidationException(Exception e) {
		super(e);
	}
	
	public ValidationException(String msg) {
		super(msg);
	}
}