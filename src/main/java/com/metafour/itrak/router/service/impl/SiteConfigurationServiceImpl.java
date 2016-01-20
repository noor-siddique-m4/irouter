package com.metafour.itrak.router.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.metafour.itrak.router.exceptions.SiteConfigurationNotFoundException;
import com.metafour.itrak.router.models.EventConfig;
import com.metafour.itrak.router.models.NextScreenConfig;
import com.metafour.itrak.router.models.SiteConfig;
import com.metafour.itrak.router.service.SiteConfigurationService;

/**
 * @author noor
 *
 */
@org.springframework.stereotype.Service
public class SiteConfigurationServiceImpl implements SiteConfigurationService {
	private Logger logger = LoggerFactory.getLogger(SiteConfigurationServiceImpl.class);

	@Value("${config.url.prefix:https://ms.m4.net/mtrak/config/}")
	private String configUrlPrefix;
	@Value("${config.url.suffix:.ini}")
	private String configUrlSuffix;
	@Value("${config.value.separator:=}")
	private String valueSeparator;

	@Override
	public SiteConfig getSiteConfiguration(String site) throws SiteConfigurationNotFoundException {
		SiteConfig sc = new SiteConfig();
		sc.setCode(site);
		String iniurl = configUrlPrefix + site + configUrlSuffix;
		logger.debug("ini file to download is {}", iniurl);
		try {
			URLConnection urlConn = new URL(iniurl).openConnection();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()))) {
				List<String> lines = br.lines().filter(l -> !l.isEmpty()).map(l -> l.trim()).collect(Collectors.toList());
				Optional<String> lineData = lines.stream().filter(l -> l.startsWith("Description")).findFirst();
				if (lineData.isPresent()) {
					sc.setLabel(getValue(lineData.get()));
				}
				lineData = lines.stream().filter(l -> l.startsWith("Logo")).findFirst();
				if (lineData.isPresent()) {
					sc.setLogo(getLogo(getValue(lineData.get())));
				}
				List<EventConfig> evs = new ArrayList<EventConfig>();
				lines.stream().forEach(l -> {
					if (l.equalsIgnoreCase("[EventTable]")) {
						evs.add(new EventConfig());
					} else if (!evs.isEmpty()) {
						EventConfig event = evs.get(evs.size() - 1);
						if (l.startsWith("Code")) {
							event.setCode(getValue(l));
						} else if (l.startsWith("Description")) {
							event.setLabel(getValue(l));
						} else if (l.startsWith("RqAdditionalText") && "Y".equalsIgnoreCase(getValue(l))) {
							event.setNext(new NextScreenConfig());
						} else if (event.getNext() != null) {
							if (l.startsWith("LbAdditionalText")) {
								event.getNext().setLabel(getValue(l));
							} else if (l.startsWith("MnAdditionalText")) {
								event.getNext().setRequired("Y".equalsIgnoreCase(getValue(l)));
							} else if (l.startsWith("RqSignature")) {
								event.getNext().setSign("Y".equalsIgnoreCase(getValue(l)));
							}
						}
					}
				});
				sc.setEvents(evs);
			} catch (Exception e) {
				logger.error("Failed to prepare configuration object from ini file for itrak site " + site, e);
				throw new SiteConfigurationNotFoundException(e.getMessage(), e);
			}
		} catch (IOException e) {
			logger.error("Failed to retrieve ini file " + iniurl, e);
			throw new SiteConfigurationNotFoundException(e.getMessage(), e);
		}
		return sc;
	}

	private String getLogo(String logo) throws Exception {
		URLConnection urlConn = new URL(logo).openConnection();
		byte[] bytes = Base64.getEncoder().encode(IOUtils.toByteArray(urlConn.getInputStream()));
		return new String(bytes);
	}
	
	private String getValue(String line) {
		return StringUtils.substringAfter(line, valueSeparator).trim();
	}
}