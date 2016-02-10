package com.metafour.itrak.router.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.metafour.itrak.router.exceptions.ItrakRouterException;
import com.metafour.itrak.router.service.MessengerService;

/**
 * @author noor
 *
 */
@Service
public class MessengerServiceImpl implements MessengerService {
	private Logger logger = LoggerFactory.getLogger(MessengerServiceImpl.class);

	@Value("${itrak.basicurl:https://ms.m4.net/}")
	private String basicUrl;
	@Value("${messenger.codeurl:/Messenger?email={EMAIL}}")
	private String messengerCodeUrl;
	@Value("${messenger.emailrpt:{EMAIL}}")
	private String emailReplaceText;

	@Override
	public String getMessengerCode(String site, String email) throws ItrakRouterException {
		String rsp = "";
		String processurl = basicUrl + site + messengerCodeUrl.replace(emailReplaceText, email);
		try (CloseableHttpClient httpclient = HttpClients.createDefault()){
			HttpGet hg = new HttpGet(processurl);
			try(CloseableHttpResponse rs = httpclient.execute(hg)) {
				HttpEntity resEntity = rs.getEntity();
				if (resEntity != null) {
					try(BufferedReader rd = new BufferedReader(new InputStreamReader(resEntity.getContent()))) {
						String line = "";
						while ((line = rd.readLine()) != null) {
							logger.debug(line);
							rsp += line;
						}
						if (StringUtils.isEmpty(rsp)) {
							logger.error("No messenger code found for the email address '" + email + "'");
							throw new ItrakRouterException("No messenger code found for the email address");
						}
					}
				} else {
					logger.error("Failed to retrieve messenger code for the email '" + email + "'");
					throw new ItrakRouterException("Failed to retrieve messenger code for the email address");
				}
				EntityUtils.consume(resEntity);
			}
		} catch (IOException e) {
			logger.error("Failed to retrieve messenger code for the email '" + email + "'");
			throw new ItrakRouterException("Failed to retrieve messenger code for the email address");
		}
		
		return rsp;
	}

}
