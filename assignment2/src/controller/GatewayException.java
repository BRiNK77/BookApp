package controller;

@SuppressWarnings("serial")
public class GatewayException extends Exception {
	

	public GatewayException(Exception e) {
		super(e);
	}
	
	public GatewayException(String s) {
		super(s);
	}
}