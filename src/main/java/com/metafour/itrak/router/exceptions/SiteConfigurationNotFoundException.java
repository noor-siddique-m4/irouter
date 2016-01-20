package com.metafour.itrak.router.exceptions;

/**
 * @author noor
 *
 */
public class SiteConfigurationNotFoundException extends ItrakRouterException {

	private static final long serialVersionUID = 3958261127023994476L;

	public SiteConfigurationNotFoundException() {

	}

	/**
	 * @param message
	 */
	public SiteConfigurationNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SiteConfigurationNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SiteConfigurationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public SiteConfigurationNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}