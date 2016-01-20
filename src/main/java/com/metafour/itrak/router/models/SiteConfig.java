package com.metafour.itrak.router.models;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noor
 *
 */
public class SiteConfig {
	private String code;
	private String label;
	private String logo;
	private String[] colors;
	private List<EventConfig> events = new ArrayList<EventConfig>();
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String[] getColors() {
		return colors;
	}
	public void setColors(String[] colors) {
		this.colors = colors;
	}
	public List<EventConfig> getEvents() {
		return events;
	}
	public void setEvents(List<EventConfig> events) {
		this.events = events;
	}
	public void addEvent(EventConfig event) {
		getEvents().add(event);
	}
}
