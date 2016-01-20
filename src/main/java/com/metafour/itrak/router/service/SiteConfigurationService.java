package com.metafour.itrak.router.service;

import com.metafour.itrak.router.exceptions.SiteConfigurationNotFoundException;
import com.metafour.itrak.router.models.SiteConfig;

/**
 * @author noor
 *
 */
public interface SiteConfigurationService {

	SiteConfig getSiteConfiguration(String site) throws SiteConfigurationNotFoundException;
}
