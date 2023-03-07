package fr.kage.nexus3;

import lombok.Data;

@Data
public class Item {

	private String downloadUrl;
	private String path;
	private String id;
	private String repository;
	private String format;
	private Checksum checksum;

}
