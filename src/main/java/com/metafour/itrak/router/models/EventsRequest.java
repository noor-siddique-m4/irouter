package com.metafour.itrak.router.models;

import java.util.List;

/**
 * @author noor
 *
 */
public class EventsRequest {
	private String device;
	private List<Event> events;
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
}
