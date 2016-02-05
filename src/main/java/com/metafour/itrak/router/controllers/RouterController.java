package com.metafour.itrak.router.controllers;

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
import com.metafour.itrak.router.models.EventsRequest;
import com.metafour.itrak.router.models.ResponseWrapper;
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
	public ResponseWrapper config(@PathVariable String site) {
		logger.debug("Configuration request for site {}", site);
		ResponseWrapper rwrapper = new ResponseWrapper();
		try {
			rwrapper.setData(configService.getSiteConfiguration(site));
			rwrapper.setSuccess(true);
		} catch (SiteConfigurationNotFoundException e) {
			logger.error("Failed to get site configuration for itrak site " + site, e);
			rwrapper.setMessage(e.getMessage());
		}
		return rwrapper;
	}

	@RequestMapping(value="/events/{site}", method=RequestMethod.POST)
	public ResponseWrapper eventsUpload(@PathVariable String site, @RequestBody EventsRequest request) {
		logger.debug("Events upload request for site {}", site);
		ResponseWrapper rwrapper = new ResponseWrapper();
		try {
			rwrapper.setData(eventsUploadService.uploadEvents(site, request));
			rwrapper.setSuccess(true);
		} catch (ItrakRouterException e) {
			logger.error("Failed to upload events for itrak site " + site, e);
			rwrapper.setMessage(e.getMessage());
		}
		return rwrapper;
	}
}