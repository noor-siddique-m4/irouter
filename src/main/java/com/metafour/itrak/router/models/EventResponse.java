package com.metafour.itrak.router.models;

/**
 * @author noor
 *
 */
public class EventResponse {
	private String id;
	private boolean status;
	private String message;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status ? "success" : "failed";
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
