package com.kwic.exception;

public class DefinedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String code;
	
    public DefinedException(Throwable cause) {
        super(cause);
    }
    public DefinedException(String message, Throwable cause) {
        super(message, cause);
    }
    public DefinedException(String message) {
        super(message);
    }
    public DefinedException() {
        super();
    }
    public void setStatusCode(String code) {
    	this.code	= code;
    }
    public String getStatusCode() {
    	return this.code;
    }
	
}
