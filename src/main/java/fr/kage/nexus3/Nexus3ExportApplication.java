package fr.kage.nexus3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Nexus3ExportApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Nexus3ExportApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		if (args.length > 0) {
			if (args.length >= 4) {

				Properties credentials = loadCredentials();

				String url = args[0];
				String repoId = args[1];
				String downloadPath = args.length == 5 ? args[4] : null;

				boolean authenticate = Boolean.valueOf(credentials.getProperty("authenticate", "false"));
				String username = removeTrailingQuotes(credentials.getProperty("username"));
				String password = removeTrailingQuotes(credentials.getProperty("password"));

				String desturl = args[2];
				String destrepoId = args[3];

				boolean destauthenticate = Boolean.valueOf(credentials.getProperty("destauthenticate", "false"));
				String destusername = removeTrailingQuotes(credentials.getProperty("destusername"));
				String destpassword = removeTrailingQuotes(credentials.getProperty("destpassword"));
				UploadRepository uploader = new UploadRepository(desturl, destrepoId, destauthenticate, destusername, destpassword);


				new DownloadRepository(url, repoId, downloadPath, authenticate, username, password, uploader).start();
				return;
			}
			else
				System.out.println("Missing arguments for download.");
		}
		else
			System.out.println("No specified argument.");

		System.out.println("Usage:");
		System.out.println("\tnexus3 http://url.to/nexus3 nexusRepositoryId http://url.to/artifactory artifactoryRepositoryId [localPath]");
		System.exit(1);
	}

	private Properties loadCredentials() {
		Properties properties = new Properties();

		File credentialsFile = new File(Paths.get("").toAbsolutePath().toFile(), "credentials.properties");

		if (!credentialsFile.exists()) {
			return properties;
		}

		try {
		properties.load(new FileInputStream(credentialsFile));
		} catch(IOException e) {
			System.err.println("Credentials file " + credentialsFile.getAbsolutePath() + " could not be found or is malformed!");
			System.exit(1);
		}

		return properties;
	}

	private String removeTrailingQuotes(String value) {
		String result; 
		if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("\'") && value.endsWith("\'"))) {
			result = value.substring(1, value.length() - 1);
		} else {
			result = value;
		}

		return result;
	}
}
