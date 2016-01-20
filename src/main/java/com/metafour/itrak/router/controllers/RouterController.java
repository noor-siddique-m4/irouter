package com.metafour.itrak.router.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.metafour.itrak.router.exceptions.ItrakRouterException;
import com.metafour.itrak.router.exceptions.SiteConfigurationNotFoundException;
import com.metafour.itrak.router.models.EventResponse;
import com.metafour.itrak.router.models.EventsRequest;
import com.metafour.itrak.router.models.SiteConfig;
import com.metafour.itrak.router.service.EventsUploadService;
import com.metafour.itrak.router.service.SiteConfigurationService;

/**
 * @author noor
 *
 */
@RestController
public class RouterController {
	private Logger logger = LoggerFactory.getLogger(RouterController.class);

	@Autowired
	SiteConfigurationService configService;
	
	@Autowired
	EventsUploadService eventsUploadService;

	@RequestMapping("/config/{site}")
	public SiteConfig config(@PathVariable String site) {
		logger.debug("Configuration request for site {}", site);
		try {
			return configService.getSiteConfiguration(site);
		} catch (SiteConfigurationNotFoundException e) {
			logger.error("Failed to get site configuration for itrak site " + site, e);
		}
		return null;
	}

	@RequestMapping(value="/events/{site}", method=RequestMethod.POST)
	public List<EventResponse> eventsUpload(@PathVariable String site, @RequestBody EventsRequest request) {
		logger.debug("Events upload request for site {}", site);
		try {
			return eventsUploadService.uploadEvents(site, request);
		} catch (ItrakRouterException e) {
			logger.error("Failed to upload events for itrak site " + site, e);
		}
		return null;
	}
}