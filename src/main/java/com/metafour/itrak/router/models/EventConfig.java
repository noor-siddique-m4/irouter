/**
 * 
 */
package com.metafour.itrak.router.models;

/**
 * @author noor
 *
 */
public class EventConfig {
	private String code;
	private String label;
	private NextScreenConfig next;
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
	public NextScreenConfig getNext() {
		return next;
	}
	public void setNext(NextScreenConfig next) {
		this.next = next;
	}
}
