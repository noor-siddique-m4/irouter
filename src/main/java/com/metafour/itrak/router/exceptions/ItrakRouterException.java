package com.metafour.itrak.router.exceptions;

/**
 * @author noor
 *
 */
public class ItrakRouterException extends Exception {

	private static final long serialVersionUID = -2500240221774718481L;

	public ItrakRouterException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ItrakRouterException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ItrakRouterException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ItrakRouterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ItrakRouterException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}