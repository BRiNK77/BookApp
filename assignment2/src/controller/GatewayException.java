package controller;

// used to handle gateway exceptions in regards to the database, will show exception and message
@SuppressWarnings("serial")
public class GatewayException extends Exception {

	public GatewayException(Exception e) {
		super(e);
	}

	public GatewayException(String s) {
		super(s);
	}
}