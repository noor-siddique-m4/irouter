package com.metafour.itrak.router.models;

/**
 * @author noor
 *
 */
public class ResponseWrapper {
	private boolean success;
	private String message;
	private Object data;
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean status) {
		this.success = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
