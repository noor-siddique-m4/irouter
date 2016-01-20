package com.metafour.itrak.router.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.metafour.itrak.router.exceptions.ItrakRouterException;
import com.metafour.itrak.router.models.Event;
import com.metafour.itrak.router.models.EventResponse;
import com.metafour.itrak.router.models.EventsRequest;
import com.metafour.itrak.router.service.EventsUploadService;

/**
 * @author noor
 *
 */
@Service
public class EventsUploadServiceImpl implements EventsUploadService {
	private Logger logger = LoggerFactory.getLogger(EventsUploadServiceImpl.class);

	private SimpleDateFormat tmfrmt = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat dtfrmt = new SimpleDateFormat("dd/MM/yyyy");
	
	@Value("${events.basicurl:https://ms.m4.net/}")
	private String basicUrl;
	@Value("${events.uploadurl:/PdaUpload}")
	private String filesUploadSuffix;
	@Value("${events.processurl:/PdaManager?site={SITE}&file={FILE}}")
	private String eventsProcessSuffix;
	@Value("${events.siterpt:{SITE}}")
	private String siteReplaceText;
	@Value("${events.filerpt:{FILE}}")
	private String fileReplaceText;
	@Value("${events.tmpdir:/tmp/irouter/}")
	private String tempFilesDir;

	@PostConstruct
	private void init() {
		if ((tempFilesDir.endsWith("/") || tempFilesDir.endsWith("\\")) == Boolean.FALSE) {
			tempFilesDir += System.getProperty("file.separator");
		}
		try {
			new File(tempFilesDir).mkdirs();
		} catch (SecurityException e) {
			logger.error("Failed to create temporary directory."
					+ "	This service will not work as it will fail to save temporary files."
					+ " Please fix this first", e);
		}
	}

	@Override
	public List<EventResponse> uploadEvents(String site, EventsRequest request) throws ItrakRouterException {
		List<EventResponse> items = new ArrayList<EventResponse>();
		Map<String, String> images = new HashMap<String, String>();
		String filename = saveFilesAndGetFilename(site, request, images);
		logger.debug("Saved text file {} for site {}", filename, site);
		if(uploadFiles(site, filename, images, request.getEvents())) {
			try (CloseableHttpClient httpclient = HttpClients.createDefault()){
				String processurl = basicUrl + site + eventsProcessSuffix.replace(siteReplaceText, site).replace(fileReplaceText, filename);
				logger.debug("Process url is {}", processurl);
				HttpGet hg = new HttpGet(processurl);
				try(CloseableHttpResponse rs = httpclient.execute(hg)) {
					HttpEntity resEntity = rs.getEntity();
					if (resEntity != null) {
						try(BufferedReader rd = new BufferedReader(new InputStreamReader(resEntity.getContent()))) {
							String line = "";
							int j = 0;
							while ((line = rd.readLine()) != null) {
								logger.debug(line);
								String[] parts = line.split(":");
								if ("EVENT".equals(parts[0])) {
									Event e = request.getEvents().get(j++);
									EventResponse er = new EventResponse();
									er.setId(e.getId());
									String stat = parts[parts.length - 1];
									if ("OK".equals(stat)) {
										er.setStatus(true);
									} else {
										er.setMessage(stat);
									}
									items.add(er);
								}
							}
						}
					}
					EntityUtils.consume(resEntity);
				}
			} catch (IOException e1) {
				logger.error("Failed to process events", e1);
				throw new ItrakRouterException("Failed to process events", e1);
			}
		}
		return items;
	}

	private String saveFilesAndGetFilename(String site, EventsRequest request, Map<String, String> images) throws ItrakRouterException {
		StringBuilder txt = new StringBuilder("SITE~");
		txt.append(site)
			.append("\n")
			.append("IMEI~")
			.append(request.getDevice())
			.append("\n")
			.append("DOWNLOAD~")
			.append(dtfrmt.format(new Date()))
			.append("~")
			.append(tmfrmt.format(new Date()))
			.append("\n")
			.append("COMMENT~LoginID~Event-code~Time~Date~JobID~Text~SignatureID~Shelfmark\n");
		for (Event ev : request.getEvents()) {
			Calendar cl = ev.getDate();
			txt.append("EVENT~")
				.append(ev.getMessenger())
				.append("~")
				.append(ev.getEvent())
				.append("~")
				.append(tmfrmt.format(cl.getTime()))
				.append("~")
				.append(dtfrmt.format(cl.getTime()))
				.append("~")
				.append(ev.getJob())
				.append("~T_")
				.append(StringUtils.isNotEmpty(ev.getText()) ? ev.getText() : "")
				.append("~S_");
			if (StringUtils.isNotEmpty(ev.getSignature())) {
				String ref = request.getDevice() + "_" + UUID.randomUUID().toString() + ".jpg";
				images.put(ref, ev.getSignature());
				try {
					saveAtTmp(ref, ev.getSignature());
				} catch (IOException e) {
					throw new ItrakRouterException("Failed to save image at temporary directory " + tempFilesDir, e);
				}
				txt.append(ref);
			}
			txt.append("~H_\n");
		}
		txt.append("STOP\n");
		logger.debug("Prepared file content to upload is \n{}", txt.toString());
		String filename = request.getDevice() + "_" + UUID.randomUUID().toString() + ".txt";
		try {
			saveAtTmp(filename, txt.toString());
		} catch (IOException e) {
			throw new ItrakRouterException("Failed to save file at temporary directory " + tempFilesDir, e);
		}
		return filename;
	}

	private FileBody prepareFileBody(String filename) {
		return new FileBody(new File(tempFilesDir + filename));
	}

	private boolean uploadFiles(String site, String fname, Map<String, String> images, List<Event> events) throws ItrakRouterException {
		try (CloseableHttpClient httpclient = HttpClients.createDefault()){
			String uploadurl = basicUrl + site + filesUploadSuffix;
			logger.debug("Upload url is {}", uploadurl);
			HttpPost httppost = new HttpPost(uploadurl);
			MultipartEntityBuilder meb = MultipartEntityBuilder
													.create()
													.addPart("up_path", new StringBody("/itrak2/" + site + "/p_itrak/import", ContentType.TEXT_PLAIN))
													.addPart("up_file1", prepareFileBody(fname));
			int i = 2;
			for (Entry<String, String> e : images.entrySet()) {
				meb.addPart("up_file" + i++, prepareFileBody(e.getKey()));
			}
			logger.debug("Number of images found for upload is {}", i - 2);
			HttpEntity reqEntity = meb.build();
			httppost.setEntity(reqEntity);
			try (CloseableHttpResponse response = httpclient.execute(httppost)) {
				// deletes the temporary files
				new File(tempFilesDir + fname).delete();
				for (Entry<String, String> e : images.entrySet()) {
					new File(tempFilesDir + e.getKey()).delete();
				}
				int statuscode  = response.getStatusLine().getStatusCode();
				logger.debug("Files upload status code {}", statuscode);
				return statuscode == 200;
			} catch (IOException e1) {
				logger.error("Failed to upload files to server", e1);
				throw new ItrakRouterException("Failed to upload files to server", e1);
			}
		} catch (IOException e2) {
			logger.error("Failed to upload files to server", e2);
			throw new ItrakRouterException("Failed to upload files to server", e2);
		}
	}

	private void saveAtTmp(String name, String content) throws IOException {
		try(FileWriter wr = new FileWriter(tempFilesDir + name)) {
			wr.write(content);
			wr.flush();
		}
	}
}