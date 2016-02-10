package com.metafour.itrak.router.service;

import com.metafour.itrak.router.exceptions.ItrakRouterException;

/**
 * 
 * @author noor
 *
 */
public interface MessengerService {

	public String getMessengerCode(String site, String email) throws ItrakRouterException;
}
