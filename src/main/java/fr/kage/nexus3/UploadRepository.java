package fr.kage.nexus3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.net.*;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;


public class UploadRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadRepository.class);

	private final String url;
	private final String repositoryId;

	private boolean authenticate;
	private String username;
	private String password;

	private RestTemplate restTemplate;
	private ExecutorService executorService;

	private AtomicLong assetProcessed = new AtomicLong();
	private AtomicLong assetFound = new AtomicLong();


	public UploadRepository(String url, String repositoryId, boolean authenticate, String username, String password) {
		this.url = requireNonNull(url);
		this.repositoryId = requireNonNull(repositoryId);
		this.authenticate = authenticate;
		this.username = username;
		this.password = password;


		if (authenticate) {
			LOGGER.info("Configuring authentication for Artifactory repository");

			// Set auth for RestTemplate to retrieve list of assets
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
			restTemplate = restTemplateBuilder.basicAuthentication(username, password).build();

			// Set auth for Java to download individual assets using url.openStream();
			Authenticator.setDefault (new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication (username, password.toCharArray());
				}
			});
		} else {
			restTemplate = new RestTemplate();
		}

	}


	public void uploadItem(Item item, Path assetPath) throws MalformedURLException, URISyntaxException {
		LOGGER.info("Now uploading item {}",item);
		URL desturl = new URL(new URL(url), "repository/" + repositoryId + "/" + item.getPath());
		LOGGER.info("Now uploading item to {}",desturl);
		HttpEntity<Resource> entity = new HttpEntity<>(new FileSystemResource(assetPath));
		RequestCallback requestCallback = restTemplate.httpEntityCallback(entity,String.class);
		Object resp = restTemplate.execute(desturl.toString(), HttpMethod.PUT, requestCallback, null);
		LOGGER.info("Uploading done for item {}",item);
	}


}
