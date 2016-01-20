package com.metafour.itrak.router.models;

/**
 * @author noor
 *
 */
public class NextScreenConfig {
	private String label;
	private boolean required;
	private boolean sign;
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public boolean isSign() {
		return sign;
	}
	public void setSign(boolean sign) {
		this.sign = sign;
	}
}
