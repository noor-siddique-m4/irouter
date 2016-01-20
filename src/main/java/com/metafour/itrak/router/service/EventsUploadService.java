package com.metafour.itrak.router.service;

import java.util.List;

import com.metafour.itrak.router.exceptions.ItrakRouterException;
import com.metafour.itrak.router.models.EventResponse;
import com.metafour.itrak.router.models.EventsRequest;

/**
 * @author noor
 *
 */
public interface EventsUploadService {
	List<EventResponse> uploadEvents(String site, EventsRequest request) throws ItrakRouterException;
}
